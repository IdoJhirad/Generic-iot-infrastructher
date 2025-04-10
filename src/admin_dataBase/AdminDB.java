/*
* code review by:
*
* */
package admin_dataBase;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class AdminDB {

    private final HashMap<String, DBMSHandler> handlerMap = new HashMap<>();

    private AdminDB() {
    }

    private static class Holder {
        private static final AdminDB INSTANCE = new AdminDB();
    }

    public static AdminDB getInstance() {
        return Holder.INSTANCE;
    }

    public void addDBMS(String DBMSType, DBMSHandler handler) {
        System.out.println("entered to AdminDB.addDBMS with Type "+DBMSType);
        handlerMap.put(DBMSType, handler);
    }

    public JsonObject handleRequest(JsonObject json) {
        String dbmsType = json.get("DBMS_Type").getAsString();
        DBMSHandler handler = handlerMap.get(dbmsType);
        System.out.println("Admin DB handle chosen " + handler);
        return handler != null ? handler.handleRequest(json) : null;
    }

    public interface DBMSHandler {
        JsonObject handleRequest(JsonObject json);
    }

    public interface TypeRequest {
        JsonObject executeTypeRequest(JsonObject json);
    }


    public static class MySQLHandler implements DBMSHandler {

        private final Connection DBConnection;
        private final HashMap<String, AdminDB.TypeRequest> requestMap = new HashMap<>();

        public MySQLHandler(String url, String userName, String password) {

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                this.DBConnection = DriverManager.getConnection(url, userName, password);
                DBConnection.setAutoCommit(false);

            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
            registerRequests();
            System.out.println("My_SQL handler Initialized");

        }

        public void registerRequests() {
            requestMap.put("RegisterCompany", this::registerCompany);
            requestMap.put("RegisterProduct", this::registerProduct);
            requestMap.put("GetCompany", this::getCompany);
            requestMap.put("GetCompanies", this::getCompanies);
            requestMap.put("GetProduct", this::getProduct);
            requestMap.put("GetProducts", this::getProducts);
            requestMap.put("GetCompanyName", this::getCompanyName);


        }

        @Override
        public JsonObject handleRequest(JsonObject json) {
            System.out.println("mysql handler");
            String requestName = json.get("Key").getAsString();

            AdminDB.TypeRequest handler = requestMap.get(requestName);
            System.out.println("mysql handler chosen " + handler);
            try {
                return handler != null ? handler.executeTypeRequest(json.getAsJsonObject("Data")) : null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private JsonObject registerCompany(JsonObject json) {
            System.out.println("MySQL handler - enterd to the register company " + json);

            String companySQL = "INSERT INTO Company (Company_Name) VALUES (  ? );";

            try (PreparedStatement companyStatement = DBConnection.prepareStatement(companySQL, Statement.RETURN_GENERATED_KEYS)) {
                //prepare the statement
                companyStatement.setString(1, json.get("Company_Name").getAsString());
                System.out.println("MySQL handler prepared statment " + companyStatement);

                //execute
                int effectedRows = companyStatement.executeUpdate();

                if (effectedRows == 0) {
                    System.out.println("MySQL handler failed efected rows " + effectedRows);
                    throw new SQLException("insertCompany failed");
                }
                //get CompanyID
                int companyID;
                try (ResultSet generatedKeysSet = companyStatement.getGeneratedKeys()) {
                    if (generatedKeysSet.next()) {
                        companyID = generatedKeysSet.getInt(1);
                        //register the contact

                        return registerContact(json, companyID);
                    } else {
                        throw new SQLException("Inserting company failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                //      e.printStackTrace();
                System.out.println("Register company failed");
                return handleError(e, new JsonObject());
            }

        }

        private JsonObject registerContact(JsonObject json, int companyID) {
            System.out.println("MySQL register contact entered " + json);

            JsonObject response = new JsonObject();
            String contactSQL = "INSERT INTO Contacts (Contact_Name ,Contact_Number,Company_ID, Address, " +
                    "Credit_Card, Expiry_Date , Security_Code ) " +
                    "VALUES (?,?,?,?,?,?,?);";
            try (PreparedStatement contactStatement = DBConnection.prepareStatement(contactSQL)) {

                contactStatement.setString(1, json.get("Contact_Name").getAsString());
                contactStatement.setString(2, json.get("Contact_Number").getAsString());
                contactStatement.setInt(3, companyID);
                contactStatement.setString(4, json.get("Address").getAsString());
                contactStatement.setString(5, json.get("Credit_Card").getAsString());
                //DATE parse
                //TODO check if check the date time for null
                contactStatement.setDate(6, extractDate(json.get("Expiry_Date").getAsString()));
                contactStatement.setString(7, json.get("Security_Code").getAsString());

                System.out.println("Prepared statment MySQL handler prepared statment " + contactStatement);

                int effectedRows = contactStatement.executeUpdate();
                // System.out.println(effectedRows);
                if (effectedRows == 0) {
                    throw new SQLException("insert contact failed");
                }
                response.addProperty("Status", "Success");
                response.addProperty("Company_ID", companyID);

                DBConnection.commit();
                return response;

            } catch (Exception e) {
                e.printStackTrace();
                //TODO CHECK IF THERE IS A better OPTIOn
                System.out.println("Register Contact failed");
                String deleteData = "DELETE FROM Company WHERE company_ID = " + companyID + " ;";
                try (PreparedStatement preparedStatement = DBConnection.prepareStatement(deleteData)) {
                    preparedStatement.executeUpdate();

                    DBConnection.commit();
                    return handleError(e, response);

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }


        private java.sql.Date extractDate(String expiryDate) {
            try {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse("01/" + expiryDate, DateTimeFormatter.ofPattern("dd/MM/yy"));
                return Date.valueOf(date.format(dateTimeFormatter));
            } catch (DateTimeParseException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        private JsonObject registerProduct(JsonObject json) {
            String ProductSQL = "INSERT INTO Product (Company_ID, Product_Name, Description) VALUES ( ?, ?,? );";
            JsonObject response = new JsonObject();

            try (PreparedStatement productStatement = DBConnection.prepareStatement(ProductSQL, Statement.RETURN_GENERATED_KEYS)) {
                //prepare the statement
                productStatement.setInt(1, json.get("Company_ID").getAsInt());
                productStatement.setString(2, json.get("Product_Name").getAsString());
                productStatement.setString(3, json.get("Description").getAsString());

                System.out.println("insert product PreparedStatement " + productStatement);

                //execute
                int effectedRows = productStatement.executeUpdate();
                if (effectedRows == 0) {
                    throw new SQLException("insertCompany failed");
                }
                System.out.println("insert product effectedRows " + effectedRows);

                //get CompanyID
                int productID;
                try (ResultSet generatedKeysSet = productStatement.getGeneratedKeys()) {
                    if (generatedKeysSet.next()) {
                        productID = generatedKeysSet.getInt(1);

                        //build the json
                        response.addProperty("Status", "Success");
                        response.addProperty("Product_ID", productID);
                        DBConnection.commit();
                        return response;

                    } else {
                        throw new SQLException("Inserting company failed, no ID obtained.");
                    }
                }
            } catch (Exception e) {
                return handleError(e, response);

            }

        }

        private JsonObject getCompanies(JsonObject json) {
            String getCompanies = "SELECT * FROM Company " +
                    "JOIN Contacts ON  Contacts.Company_ID = Company.Company_ID;";
            JsonObject response = new JsonObject();
            JsonArray companyArray = new JsonArray();
            try (PreparedStatement getCompanyStatement = DBConnection.prepareStatement(getCompanies)) {
                try (ResultSet resSet = getCompanyStatement.executeQuery()) {

                    while (resSet.next()) {
                        JsonObject company = new JsonObject();

                        company.addProperty("Company_ID", resSet.getInt("Company_ID"));
                        company.addProperty("Company_Name", resSet.getString("Company_Name"));
                        company.addProperty("Contact_ID", resSet.getInt("Contact_ID"));
                        company.addProperty("Contact_Name", resSet.getString("Contact_Name"));
                        company.addProperty("Contact_Number", resSet.getString("Contact_Number"));
                        company.addProperty("Address", resSet.getString("Address"));
                        company.addProperty("Credit_Card", resSet.getString("Credit_Card"));
                        company.addProperty("Expiry_Date", resSet.getDate("Expiry_Date").toString());
                        company.addProperty("Security_Code", resSet.getString("Security_Code"));

                        companyArray.add(company);
                    }
                }
                response.addProperty("Status", "Success");
                response.add("Data", companyArray);
                return response;

            } catch (Exception e) {
                return handleError(e, response);
            }
        }

        private JsonObject getCompany(JsonObject json) {
            System.out.println("get company json " + json);
            String getCompanySQL = "SELECT * FROM Company " +
                    "JOIN Contacts ON  Company.Company_ID = Contacts.Company_ID " +
                    "WHERE Company.Company_ID = ?;";
            JsonObject response = new JsonObject();
            try (PreparedStatement getCompanyStatement = DBConnection.prepareStatement(getCompanySQL)) {
                getCompanyStatement.setInt(1, json.get("Company_ID").getAsInt());
                System.out.println(getCompanyStatement);
                try (ResultSet resSet = getCompanyStatement.executeQuery()) {

                    while (resSet.next()) {
                        response.addProperty("Company_ID", resSet.getInt("Company_ID"));
                        response.addProperty("Company_Name", resSet.getString("Company_Name"));
                        response.addProperty("Contact_ID", resSet.getInt("Contact_ID"));
                        response.addProperty("Contact_Name", resSet.getString("Contact_Name"));
                        response.addProperty("Contact_Number", resSet.getString("Contact_Number"));
                        response.addProperty("Address", resSet.getString("Address"));
                        response.addProperty("Credit_Card", resSet.getString("Credit_Card"));
                        response.addProperty("Expiry_Date", resSet.getDate("Expiry_Date").toString());
                        response.addProperty("Security_Code", resSet.getString("Security_Code"));
                    }
                }
                response.addProperty("Status", "Success");
                return response;
            } catch (Exception e) {
                return handleError(e, response);
            }

        }

        private JsonObject getProduct(JsonObject json) {
            System.out.println("entered to get product " + json);
            String getProductSQL = "SELECT * FROM Product " +
                    "WHERE Company_ID = ? AND Product_ID = ?;";
            JsonObject response = new JsonObject();

            try (PreparedStatement getProductStm = DBConnection.prepareStatement(getProductSQL)) {
                getProductStm.setInt(1, json.get("Company_ID").getAsInt());
                getProductStm.setInt(2, json.get("Product_ID").getAsInt());

                try (ResultSet productRes = getProductStm.executeQuery()) {
                    while (productRes.next()) {
                        response.addProperty("Product_ID", productRes.getInt("Product_ID"));
                        response.addProperty("Company_ID", productRes.getInt("Company_ID"));
                        response.addProperty("Product_Name", productRes.getString("Product_Name"));
                        response.addProperty("Description", productRes.getString("Description"));
                    }
                }
                response.addProperty("Status", "Success");

                return response;

            } catch (Exception e) {
                e.printStackTrace();

                return handleError(e, response);
            }

        }

        private JsonObject getCompanyName(JsonObject json) {
            System.out.println("get company by name json " + json);
            String getCompanySQL = "SELECT Company_Name FROM Company WHERE ? = Company_ID";
            JsonObject response = new JsonObject();
            try (PreparedStatement getCompanyStatement = DBConnection.prepareStatement(getCompanySQL)) {
                getCompanyStatement.setInt(1, json.get("Company_ID").getAsInt());

                System.out.println(getCompanyStatement);
                try (ResultSet resSet = getCompanyStatement.executeQuery()) {

                    while (resSet.next()) {
                        response.addProperty("Company_Name", resSet.getString("Company_Name"));
                    }
                }
                System.out.println("Json respond " + response);
                response.addProperty("Status", "Success");
                return response;
            } catch (Exception e) {
                return handleError(e, response);
            }

        }

        private JsonObject getProducts(JsonObject json) {
            System.out.println("enterd get products Admin DB");
            String getProducts = "SELECT * FROM Product WHERE Company_ID = ?;";
            JsonObject response = new JsonObject();
            JsonArray ProductsArray = new JsonArray();

            try (PreparedStatement getProductsStatement = DBConnection.prepareStatement(getProducts)) {
                getProductsStatement.setInt(1, json.get("Company_ID").getAsInt());
                System.out.println(getProductsStatement);
                try (ResultSet resSet = getProductsStatement.executeQuery()) {

                    while (resSet.next()) {
                        JsonObject product = new JsonObject();

                        product.addProperty("Product_ID", resSet.getInt("Product_ID"));
                        product.addProperty("Company_ID", resSet.getInt("Company_ID"));
                        product.addProperty("Product_Name", resSet.getString("Product_Name"));
                        product.addProperty("Description", resSet.getString("Description"));

                        ProductsArray.add(product);
                    }
                }
                response.addProperty("Status", "Success");
                response.add("Data", ProductsArray);
                System.out.println("respond from admin DB products " + response);
                return response;

            } catch (SQLException e) {

                return handleError(e, response);
            }
        }

        private JsonObject handleError(Exception e, JsonObject response) {
            System.out.println("Entered handle error");
            System.out.println("Register Contact failed due to missing data: " + e.getMessage());
            response.addProperty("Status", "Failure");
            response.addProperty("info", "Missing or invalid data: " + e.getMessage());
            return response;
        }
    }

    public static class MongoDBLHandler implements DBMSHandler {
        private final HashMap<String, AdminDB.TypeRequest> requestMap = new HashMap<>();
        private MongoClient mongoClient;

        public MongoDBLHandler() {
            String connectionString = "mongodb+srv://ido:idojhirad@ido.41btr.mongodb.net/?retryWrites=true&w=majority&appName=Ido";
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .serverApi(serverApi)
                    .build();
            // Create a new client and connect to the server
            try {
                mongoClient = MongoClients.create(settings);
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");

            } catch (MongoException e) {
                e.printStackTrace();
            }
            registerRequests();
        }

        public void registerRequests() {
            requestMap.put("RegisterProduct", this::registerProduct);
            requestMap.put("RegisterIOT", this::registerIOT );
            requestMap.put("RegisterIOTUpdate", this::registerIOTUpdate);
            requestMap.put("GetIOTDevices", this::getIOTDevices);
            requestMap.put("GetIOTDeviceUpdates", this::getIOTUpdates);
//            requestMap.put("Get_Company_Name", this:: );


        }

        @Override
        public JsonObject handleRequest(JsonObject json) {
            System.out.println("MongoDb handler handle request");
            String requestName = json.get("Key").getAsString();

            AdminDB.TypeRequest handler = requestMap.get(requestName);
            System.out.println("MongoDb handler chosen " + handler);
            try {
                return handler != null ? handler.executeTypeRequest(json.getAsJsonObject("Data")) : null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private JsonObject registerCompany(JsonObject json) {
            //do nothing
            return null;
        }

        private JsonObject registerProduct(JsonObject json) {
            //Company name Db Products collection insert name Document
            //get details
            try {
                String companyID = json.get("Company_ID").getAsString();
                String productName = json.get("Product_Name").getAsString();
                Integer productID = json.get("Product_ID").getAsInt();

                //get or create the DB
                MongoDatabase companyDB = mongoClient.getDatabase("Company" + companyID);
                //get collection
                MongoCollection<Document> productsCollection = companyDB.getCollection("Products");
                try {
                    //insertion
                    InsertOneResult res = productsCollection.insertOne(new Document()
                            .append("Company_ID", companyID)
                            .append("Product_Name", productName)
                            .append("Product_ID", productID));
                    System.out.println("Product inserted with ID: " + res.getInsertedId());
                    if (res.getInsertedId() != null) {
                        System.out.println("Document inserted successfully with ID: " + res.getInsertedId());
                        JsonObject resp = new JsonObject();
                        resp.addProperty("Status", "Success");
                        return resp;
                    } else {
                        //TODO change
                        throw new NullPointerException("register product fail");
                    }
                } catch (MongoBulkWriteException e) {
                    return handleError(e);
                }
            } catch (NullPointerException e) {
                return handleError(e);
            }
        }

        private JsonObject registerIOT(JsonObject json) {
            System.out.println("mongo handler RegisterIOt json"+json);
            //Company name Db Products collection insert name Document
            try {
                String companyID = json.get("Company_ID").getAsString();
                Integer productID = json.get("Product_ID").getAsInt();
                Integer iotID = json.get("Iot_ID").getAsInt();
                //get the company DB
                MongoDatabase companyDB = mongoClient.getDatabase("Company" + companyID);

                /*Check if the product ID exist*/
                //get products collection
                MongoCollection<Document> productsCollection = companyDB.getCollection("Products");
                boolean productExists = isProductExists(productsCollection,"Product_ID",productID);
                if (!productExists) {
                    JsonObject errorResp = new JsonObject();
                    errorResp.addProperty("Status", "Error");
                    errorResp.addProperty("Message", "Product does not exist.");
                    return errorResp;
                }
                    MongoCollection<Document> deviceCollection = companyDB.getCollection("Device"+productID);
                    try {
                        InsertOneResult res = deviceCollection.insertOne(new Document()
                                .append("Company_ID", companyID)
                                .append("Iot_ID", iotID)
                                .append("_id", iotID)
                                .append("Product_ID", productID));
                        System.out.println("IoT inserted with ID: " + res.getInsertedId());
                        if (res.getInsertedId() != null) {
                            JsonObject resp = new JsonObject();
                            resp.addProperty("Status", "Success");
                            resp.addProperty("Iot_ID", iotID.toString());
                            return resp;
                        } else {
                            throw new NullPointerException("Failed to insert IoT record");
                        }
                    } catch (MongoWriteException e) {
                        return handleError(e);
                    }
            } catch (NullPointerException e) {
                return handleError(e);
            }
        }

        private JsonObject getIOTDevices(JsonObject json) {
            if(!json.has("Company_ID")||!json.has("Product_ID")){
                return handleError("Missing required fields.");
            }
            //get the company DB
            String companyID = json.get("Company_ID").getAsString();
            int productID = json.get("Product_ID").getAsInt();
            String companyDBName = "Company" + companyID;
            try {
                if(!isDBExists(companyDBName)) {
                    return handleError("Company not Exists.");
                }
                MongoDatabase companyDB = mongoClient.getDatabase(companyDBName);
                String collectionName = "Device" + productID;
                if(!isCollectionExists(companyDB, collectionName)) {
                    return handleError("Product not Exists.");
                }
                MongoCollection<Document> deviceCollection = companyDB.getCollection("Device" + productID);

                FindIterable<Document> devices = deviceCollection.find();
                JsonObject resp = new JsonObject();
                int index = 1;
                for (Document document : devices) {
                    JsonObject deviceJson = JsonParser.parseString(document.toJson()).getAsJsonObject();
                    resp.add("Device_"+index, deviceJson);
                    ++index;
                }
                resp.addProperty("Status", "Success");
                return resp;
            } catch (MongoBulkWriteException e) {
                return handleError(e);
            }

        }

        private JsonObject getIOTUpdates(JsonObject json) {
            if(!json.has("Company_ID")||!json.has("Product_ID")||!json.has("Iot_ID")){
                return handleError("Missing required fields.");
            }
            //get the company DB
            String companyID = json.get("Company_ID").getAsString();
            int iotID = json.get("Iot_ID").getAsInt();
            String companyDBName = "Company" + companyID;

            try {
                if(!isDBExists(companyDBName)) {
                    return handleError("Company not Exists.");
                }
                MongoDatabase companyDB = mongoClient.getDatabase(companyDBName);
                String deviceCollectionName = "Device_Update" + iotID;
                if (!isCollectionExists(companyDB, deviceCollectionName)) {
                    return handleError("IOT not Exists.");
                }
                MongoCollection<Document> DeviceUpdateCollection = companyDB.getCollection("Device_Update" + iotID);
                FindIterable<Document> devices = DeviceUpdateCollection.find();
                JsonObject resp = new JsonObject();
                int index = 1;
                for (Document document : devices) {
                    JsonObject deviceJson = JsonParser.parseString(document.toJson()).getAsJsonObject();
                    resp.add("Update_"+index, deviceJson);
                    ++index;
                }
                resp.addProperty("Status", "Success");
                return resp;

            } catch (MongoBulkWriteException e) {
                return handleError(e);
            }

        }

        private JsonObject registerIOTUpdate(JsonObject json) {
            //Company name Db Products collection insert name Document
            if (!json.has("Company_ID") || !json.has("Product_ID") || !json.has("Iot_ID") || !json.has("Update_ID")) {
                return handleError("Missing required fields.");
            }
            //get the company DB
            String companyID = json.get("Company_ID").getAsString();
            int productID = json.get("Product_ID").getAsInt();
            Integer iotID = json.get("Iot_ID").getAsInt();
            //company DB
            MongoDatabase companyDB = mongoClient.getDatabase("Company" + companyID);
            //check if product exists
            MongoCollection<Document> deviceCollection = companyDB.getCollection("Device" + productID);

            if (!isProductExists(deviceCollection, "Iot_ID", iotID)) {
                return handleError("IOT does not exist.");
            }
            MongoCollection<Document> DeviceUpdateCollection = companyDB.getCollection("Device_Update" + iotID);
            try {
                Document documentToInsert = Document.parse(json.toString());
                InsertOneResult res = DeviceUpdateCollection.insertOne(documentToInsert);
                if (res.getInsertedId() != null) {
                    JsonObject resp = new JsonObject();
                    resp.addProperty("Status", "Success");
                    return resp;
                } else {
                    JsonObject errorResp = new JsonObject();
                    errorResp.addProperty("Status", "Error");
                    errorResp.addProperty("Message", "Mongo failed");
                    return errorResp;
                }
            } catch (MongoBulkWriteException e) {
                return handleError(e);
            }
        }

        private JsonObject handleError(String message) {
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("Status", "Failure");
            errorResponse.addProperty("Message", message);
            return errorResponse;
        }

        private JsonObject handleError(Exception e) {
            JsonObject resp = new JsonObject();
            System.out.println("Mongo DB Entered handle error");
            System.out.println(e.getMessage());
            resp.addProperty("Status", "Failure");
            resp.addProperty("info", e.getMessage());
            return resp;
        }
        private boolean isProductExists(MongoCollection<Document> collection, String fieldName ,Integer toFind) {
            Document foundID = collection.find(eq(fieldName,toFind)).first();
            return foundID != null;
        }
        private boolean isDBExists(String DBName) {
           return mongoClient.listDatabaseNames().into(new ArrayList<>()).contains(DBName);
        }
        private boolean isCollectionExists(MongoDatabase database, String collectionName) {
            return database.listCollectionNames().into(new ArrayList<>()).contains(collectionName);
        }

    }

}

//package test;
//
//import admin_dataBase.AdminDB;
//import com.google.gson.JsonObject;
//
//public class AdminDBTest {
//    public static void main(String[] args) {
//        AdminDB adminDB= AdminDB.getInstance();
//        AdminDB.MySQLHandler handler = new AdminDB.MySQLHandler();
//
//        JsonObject insertCompany = new JsonObject();
//
//        insertCompany.addProperty( "Key", "Register_Company");
//        insertCompany.addProperty( "DBMS_Type", "MySQL");
//        JsonObject data = new JsonObject();
//        data.addProperty("Company_Name", "moshe");
//        data.addProperty("Contact_Name", "Ido");
//
//        data.addProperty( "Contact_Number" , "0526511032" );
//        data.addProperty( "Address", "ddfg");
//        data.addProperty( "Credit_Card" ,"84564");
//        data.addProperty( "Security_Code","123");
//        data.addProperty( "Expiry_Date", "21-10-1999");
//        insertCompany.add("Data", data);
//        JsonObject res = adminDB.handleRequest(insertCompany);
//        System.out.println(res);
//
//        //register product
////        JsonObject productInsert = new JsonObject();
////        productInsert.addProperty( "Key", "Register_Product");
////        productInsert.addProperty( "DBMS_Type", "MySQL");
////
////        JsonObject productData =new JsonObject();
////        productData.addProperty("Company_ID", 9);
////        productData.addProperty("Product_Name", "e");
////        productData.addProperty("Description", "kif");
////        productInsert.add("Data", productData);
////
////        JsonObject insertProductRes = adminDB.handleRequest(productInsert);
////        System.out.println("insert product jeson "+insertProductRes);
////
//        //get Company
////        JsonObject getCompanyJ = new JsonObject();
////        getCompanyJ.addProperty( "Key", "Get_Company");
////        getCompanyJ.addProperty( "DBMS_Type", "MySQL");
////        JsonObject getCompanyDataJ =new JsonObject();
////        getCompanyDataJ.addProperty("Company_ID",15 );
////        getCompanyJ.add("Data", getCompanyDataJ);
////        JsonObject getCompany = adminDB.handleRequest(getCompanyJ);
////        System.out.println("insert product json "+ getCompany);
//
////      Compnies
////            JsonObject getCompanyJ = new JsonObject();
////        getCompanyJ.addProperty( "Key", "Get_Companies");
////        getCompanyJ.addProperty( "DBMS_Type", "MySQL");
////        JsonObject getCompanyDataJ =new JsonObject();
////       // getCompanyDataJ.addProperty("Company_ID",15 );
////        getCompanyJ.add("Data", getCompanyDataJ);
////        JsonObject getCompany = adminDB.handleRequest(getCompanyJ);
////        System.out.println("insert product json "+ getCompany);
//
//        //Product
////        JsonObject getCompanyJ = new JsonObject();
////        getCompanyJ.addProperty( "Key", "Get_Product");
////        getCompanyJ.addProperty( "DBMS_Type", "MySQL");
////        JsonObject getCompanyDataJ =new JsonObject();
////        getCompanyDataJ.addProperty("Company_ID",9 );
////        getCompanyDataJ.addProperty("Product_ID",4 );
////
////        getCompanyJ.add("Data", getCompanyDataJ);
////        JsonObject getCompany = adminDB.handleRequest(getCompanyJ);
////        System.out.println("insert product json "+ getCompany);
//
//        //Products
//        JsonObject getCompanyJ = new JsonObject();
//        getCompanyJ.addProperty( "Key", "Get_Products");
//        getCompanyJ.addProperty( "DBMS_Type", "MySQL");
//        JsonObject getCompanyDataJ =new JsonObject();
//        getCompanyDataJ.addProperty("Company_ID",9 );
//
//
//        getCompanyJ.add("Data", getCompanyDataJ);
//        JsonObject getCompany = adminDB.handleRequest(getCompanyJ);
//        System.out.println("insert product json "+ getCompany);
//    }
//
//
//}
//

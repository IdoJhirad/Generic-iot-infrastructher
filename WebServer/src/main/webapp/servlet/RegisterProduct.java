package main.webapp.servlet;

import admin_dataBase.AdminDB;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import sun.net.www.http.HttpClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;

@WebServlet("/register_product")
public class RegisterProduct extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Entered to get RegisterProduct");
        req.getRequestDispatcher("/pages/RegProduct.html").forward(req,resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("entered register product");
        AdminDB adminDB = AdminDB.getInstance();

        Map<String, String[]> paramMap = req.getParameterMap();
        JsonObject jsonObject = new JsonObject();
        JsonObject data = new JsonObject();

        jsonObject.addProperty("Key", "RegisterProduct");
        jsonObject.addProperty("DBMS_Type", "My_SQL"); /*TODO check if it not to much hard coded*/

        int companyID = Integer.parseInt(paramMap.get("Company_ID")[0]);
        String product_Name = paramMap.get("Product_Name")[0];
        String Description = paramMap.get("Description")[0];


        data.addProperty("Company_ID", companyID);
        data.addProperty("Product_Name", product_Name);
        data.addProperty("Description", Description);


        jsonObject.add("Data", data);

        JsonObject res = adminDB.handleRequest(jsonObject);
        resp.setContentType("application/json");


        String status = res.get("Status").getAsString();

        //TODO
        if ("Success".equals(status)) {
            resp.setStatus(HttpServletResponse.SC_OK);

            //TODO maybe change design to go to the gateway server
            JsonObject mongoRequest = new JsonObject();
            mongoRequest.addProperty("DBMS_Type", "MongoDB");
            mongoRequest.addProperty("Key","RegisterProduct");
            JsonObject mongoData = new JsonObject();
            mongoData.addProperty("Product_ID",res.get("Product_ID").getAsInt());
            mongoData.addProperty("Company_ID",companyID);
            mongoData.addProperty("Product_Name",product_Name);
            mongoRequest.add("Data",mongoData);

            JsonObject mongoResp = sendTCP(mongoRequest);
            System.out.println("mongo responded "+mongoResp);
            resp.getWriter().write(res.toString());
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorMessage = res.has("Error") ? res.get("Error").getAsString() : "Unknown error";
            resp.getWriter().write("{\"message\": \"Registration failed\", \"error\": \"" + errorMessage + "\"}");
        }
    }
    private JsonObject sendTCP(JsonObject jsonObject) {
        System.out.println("servlet sent TCP!");
        try (SocketChannel socketChannel = SocketChannel.open()) {
                socketChannel.configureBlocking(true);
            System.out.println("try to connect tcp");
            socketChannel.connect(new InetSocketAddress(InetAddress.getByName("10.10.1.65"), 9090));
                String jsonString = jsonObject.toString();
                ByteBuffer buffer = ByteBuffer.wrap(jsonString.getBytes());
                while (buffer.hasRemaining()) {
                    socketChannel.write(buffer);
                }
                // get responds
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int bytesRead = socketChannel.read(readBuffer);
            System.out.println(bytesRead);
                if (bytesRead > 0) {
                    readBuffer.flip(); // Switch to read mode
                    byte[] responseBytes = new byte[readBuffer.remaining()];
                    readBuffer.get(responseBytes);
                    String response = new String(responseBytes);
                    System.out.println("Received: " + response);
                    return new Gson().fromJson(response, JsonObject.class);
                }else {
                    throw new IOException();
                }
            } catch (IOException e) {
            System.out.println("runtime exeption");
            throw new RuntimeException(e);
        }
    }
}

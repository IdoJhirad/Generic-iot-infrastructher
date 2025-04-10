package main.webapp.servlet;

import admin_dataBase.AdminDB;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@WebServlet("/get_product")
public class GetProduct extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("entered get product");
        AdminDB adminDB= AdminDB.getInstance();

        Map<String,String[]> paramMap = req.getParameterMap();
        JsonObject jsonObject = new JsonObject();
        JsonObject data = new JsonObject();
        System.out.println(Arrays.toString(paramMap.entrySet().toArray()));
        jsonObject.addProperty("Key", "GetProduct");
        jsonObject.addProperty("DBMS_Type", "My_SQL"); /*TODO check if it not to much hard coded*/

        System.out.println("companyID "+paramMap.get("Company_ID")[0]+" productID "+paramMap.get("Product_ID")[0]);

        int companyID = Integer.parseInt(paramMap.get("Company_ID")[0]);
        int productID = Integer.parseInt(paramMap.get("Product_ID")[0]);
        data.addProperty("Company_ID", companyID);
        data.addProperty("Product_ID",productID);
        jsonObject.add("Data", data);

        JsonObject respond = adminDB.handleRequest(jsonObject);

        resp.setContentType("application/json"); // Set response content type to JSON


        String status = respond.get("Status").getAsString();

        //TODO
        if ("Success".equals(status)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(respond.toString());
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"message\": \" failed\", \"error\": \"" + respond + "\"}");
        }


    }
}

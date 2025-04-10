package main.webapp.servlet;

import admin_dataBase.AdminDB;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

@WebServlet("/register_company")
public class RegisterCompany extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("enterd register company servlet get");
        req.getRequestDispatcher("/pages/RegisterCompany.html").forward(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("enterd register company servlet post");

        AdminDB adminDB = AdminDB.getInstance();

        Map<String,String[]> paramMap = req.getParameterMap();
        JsonObject jsonObject = new JsonObject();
        JsonObject data = new JsonObject();

        jsonObject.addProperty("Key", "RegisterCompany");
        jsonObject.addProperty("DBMS_Type", "My_SQL"); /*TODO check if it not to much hard coded*/

        String companyName = paramMap.get("Company_Name")[0];
        String contact_Name = paramMap.get("Contact_Name")[0];
        String contact_Number = paramMap.get("Contact_Number")[0];
        String address =  paramMap.get("Address")[0];
        String credit_Card = paramMap.get("Credit_Card")[0];
        String expiry_Date = paramMap.get("Expiry_Date")[0];
        String security_Code = paramMap.get("Security_Code")[0];

        data.addProperty("Company_Name", companyName);
        data.addProperty("Contact_Name", contact_Name);
        data.addProperty("Contact_Number", contact_Number);
        data.addProperty("Address", address);
        data.addProperty("Credit_Card", credit_Card);
        data.addProperty("Expiry_Date",expiry_Date );
        data.addProperty("Security_Code",security_Code);
        System.out.println(data);
        jsonObject.add("Data", data);

        JsonObject res = adminDB.handleRequest(jsonObject);
        System.out.println(res);
        resp.setContentType("application/json"); // Set response content type to JSON


        String status = res.get("Status").getAsString();

        //TODO
        if ("Success".equals(status)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            //int companyID = res.get("Company_ID").getAsInt();
            resp.getWriter().write(res.toString());
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorMessage = res.has("Error") ? res.get("Error").getAsString() : "Unknown error";
            resp.getWriter().write("{\"message\": \"Registration failed\", \"error\": \"" + errorMessage + "\"}");
        }
    }


}

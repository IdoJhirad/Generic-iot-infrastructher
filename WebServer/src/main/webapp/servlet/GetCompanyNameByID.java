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

@WebServlet("/get_company_name")
public class GetCompanyNameByID extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("entered to get company name by id");
        AdminDB adminDB = AdminDB.getInstance();
       int companyID = 0;
        try {
            companyID = Integer.parseInt(req.getParameter("Company_ID"));
        } catch (NumberFormatException e) {
            System.out.println("Invalid Company_ID parameter");
            resp.getWriter().write("Fail");
            return;
        }

        JsonObject jsonObject = new JsonObject();
        JsonObject data = new JsonObject();

        jsonObject.addProperty("Key", "GetCompanyName");
        jsonObject.addProperty("DBMS_Type", "My_SQL");

        data.addProperty("Company_ID", companyID);
        jsonObject.add("Data", data);

        JsonObject respond = adminDB.handleRequest(jsonObject);

        resp.setContentType("application/json"); // Set response content type to JSON

        String status = respond.get("Status").getAsString();
        String companyName = null;
        if(respond.has("Company_Name")&&"Success".equals(status)) {
            System.out.println("\tenter name by id success");
             companyName = respond.get("Company_Name").getAsString();
            resp.getWriter().write(companyName);
        }
        else {
            System.out.println("\tenter failed");

            resp.getWriter().write("Fail");

        }
    }

}

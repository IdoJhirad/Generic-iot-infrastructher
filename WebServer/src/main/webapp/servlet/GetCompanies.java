package main.webapp.servlet;

import admin_dataBase.AdminDB;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/get_companies")
public class GetCompanies extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AdminDB adminDB = AdminDB.getInstance();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Key", "GetCompanies");
        jsonObject.addProperty("DBMS_Type", "My_SQL"); /*TODO check if it not to much hard coded*/
        JsonObject res = adminDB.handleRequest(jsonObject);

        resp.setContentType("application/json");
         resp.getWriter().write(res.toString());
    }
}

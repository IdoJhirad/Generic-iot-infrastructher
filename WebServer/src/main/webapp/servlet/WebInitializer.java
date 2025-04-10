package main.webapp.servlet;

import admin_dataBase.AdminDB;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

//Class that add the MySql Handler into the AdminDB
@WebListener
public class WebInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("TomCat initialize AdminDB with DBMS handlers.");
        AdminDB adminDB = AdminDB.getInstance();
        adminDB.addDBMS("My_SQL", new AdminDB.MySQLHandler("jdbc:mysql://localhost:3306/AdminDB","ido","Ido@Jhirad242228"));
    }
}

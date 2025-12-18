package com.softwareengineering;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.javalite.activejdbc.Base;

public class Db {
    private static final String DB_URL = "jdbc:sqlite:" + "data/database.db";

    public static void init() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (connection != null) {
                DatabaseMetaData data = connection.getMetaData();
                System.out.println(data);
            }
        } catch (SQLException exception) {

        }
    }
    public static void connect() {
        if (Base.hasConnection()) return;
        Base.open("org.sqlite.JDBC", DB_URL, "", "");
    }
    public static void close() {
        Base.close();
    }
}

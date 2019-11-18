/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author JCR
 *
 * Classe responsável pela conexão com o banco de dados MySQL
 *
 * Database: estoque User: root Password: ""
 *
 */
public class ConnectionFactory_Estoque {

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String IP = "localhost";
    private static final String URL = "jdbc:mysql://" + IP + "/estoque";
    private static final String USER = "correia";
    private static final String PASS = "46444948";

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException("Erro na conexão MySQL", ex);
        }
    }

    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                System.err.println("ERRO MySQL: " + ex);
            }
        }
    }

    public static void closeConnection(Connection con, PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                System.err.println("ERRO MySQL: " + ex);
            }
        }
        closeConnection(con);
    }

    public static void closeConnection(Connection con, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                System.err.println("ERRO MySQL: " + ex);
            }
        }
        closeConnection(con, stmt);
    }

    public static String getIPServer() {
        return ConnectionFactory_Estoque.IP;
    }
}

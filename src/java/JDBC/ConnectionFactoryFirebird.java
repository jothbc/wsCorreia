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
 *
 * @author User
 */
public class ConnectionFactoryFirebird {

    /**
     * @author JCR
     *
     * Classe responsável pela conexão com o banco de dados Firebird
     *
     * Database: SISFATURA User: SYSDBA Password: masterkey
     *
     */
    private static final String DRIVER = "org.firebirdsql.jdbc.FBDriver";
    private static final String URL = "jdbc:firebirdsql:localhost:C:/IBS/Sisfatura/SISFATURA.FDB?lc_ctype=UNICODE_FSS";
    private static final String USER = "SYSDBA";
    private static final String PASS = "masterkey";

    public static Connection getConnection(){
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new RuntimeException("Erro na conexão Firebird", ex);
        }
    }
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                System.err.println("ERRO Firebird: " + ex);
            }
        }
    }

    public static void closeConnection(Connection con, PreparedStatement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                System.err.println("ERRO Firebird: " + ex);
            }
        }
        closeConnection(con);
    }

    public static void closeConnection(Connection con, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                System.err.println("ERRO Firebird: " + ex);
            }
        }
        closeConnection(con, stmt);
    }

}

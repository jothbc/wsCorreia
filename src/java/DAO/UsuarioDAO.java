/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Bean.Usuario;
import JDBC.ConnectionFactory_Estoque;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class UsuarioDAO extends DAO {

    public UsuarioDAO() {
        con = ConnectionFactory_Estoque.getConnection();
    }

    public boolean autenticarUsuario(Usuario u) {
        sql = "SELECT * FROM usuario WHERE nome = ? AND senha = ?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, u.login);
            stmt.setString(2, u.senha);
            rs = stmt.executeQuery();
            return rs.first();
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory_Estoque.closeConnection(con, stmt, rs);
        }
        return false;
    }

    public String getMac(String mac) {
        sql = "SELECT * FROM user_mobile WHERE mac_wifi = ?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, mac.toUpperCase());
            rs = stmt.executeQuery();
            while(rs.next()){
                return rs.getString("nick");
            }
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory_Estoque.closeConnection(con, stmt, rs);
        }
        return null;
    }

}

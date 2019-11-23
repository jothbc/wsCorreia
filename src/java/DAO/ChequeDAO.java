/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import JDBC.ConnectionFactory_Financas;
import funcoes.CDbl;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class ChequeDAO extends DAO {

    public ChequeDAO() {
        con = ConnectionFactory_Financas.getConnection();
    }

    public double getTotalAberto() throws Exception {
        sql = "SELECT sum(valor) as valor FROM cheques where saque is null";
        try {
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return CDbl.CDblDuasCasas(rs.getDouble("valor"));
            }
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(ChequeDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception(ex);
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt, rs);
        }
    }
}

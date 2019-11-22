/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import JDBC.ConnectionFactory_Estoque;
import JDBC.ConnectionFactory_Financas;
import funcoes.CDbl;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class ImpostoDAO extends DAO{

    public ImpostoDAO() {
        con = ConnectionFactory_Estoque.getConnection();
    }
    
    public double getTotalAberto() throws SQLException {
        sql = "SELECT sum(valor) as valor FROM impostos where pago is null";
        stmt = con.prepareStatement(sql);
        rs = stmt.executeQuery();
        rs.first();
        double valor = 0;
        if (rs.isFirst()) {
            valor = CDbl.CDblDuasCasas(rs.getDouble("valor"));
        }
        ConnectionFactory_Financas.closeConnection(con, stmt, rs);
        return valor;
    }
    
    
}

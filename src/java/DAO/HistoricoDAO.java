/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Bean.HistoricoProduto;
import JDBC.ConnectionFactory_Estoque;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class HistoricoDAO extends DAO {

    public HistoricoDAO() {
        con = ConnectionFactory_Estoque.getConnection();
    }

    public List<HistoricoProduto> getHistorico(String usuario) {
        List<HistoricoProduto> hist = new ArrayList<>();
        sql = "SELECT * FROM log_type_use WHERE user = ?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, usuario);
            rs = stmt.executeQuery();
            while (rs.next()) {
                HistoricoProduto h = new HistoricoProduto();
                h.codigo = rs.getString("code");
                h.descricao = rs.getString("description");
                h.quantidade = rs.getDouble("amount");
                h.tabela = rs.getString("local");
                h.usuario = usuario;
                h.data = rs.getString("hour");
                hist.add(h);
            }
        } catch (SQLException ex) {
            Logger.getLogger(HistoricoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory_Estoque.closeConnection(con, stmt, rs);
        }
        return hist;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Bean.Fornecedor;
import JDBC.ConnectionFactory_Financas;
import funcoes.BoletoFuncoes;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class FornecedorDAO extends DAO {
    
    public FornecedorDAO() {
        con = ConnectionFactory_Financas.getConnection();
    }
    
    public List<Fornecedor> getTesteFornecedor(String codigo_barras) {
        List<Fornecedor> todos_banco = new ArrayList<>();
        sql = "SELECT * FROM fornecedor WHERE banco = ?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, BoletoFuncoes.getBanco(codigo_barras));
            rs = stmt.executeQuery();
            while (rs.next()) {
                Fornecedor f = new Fornecedor();
                f.setId(rs.getInt("id"));
                f.setNome(rs.getString("nome"));
                f.setBanco(rs.getInt("banco"));
                f.setNumero(rs.getString("numero"));
                todos_banco.add(f);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FornecedorDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt, rs);
        }
        List<Fornecedor> numeros_e_nulos = new ArrayList<>();
        for (Fornecedor f : todos_banco) {
            if (f.getNumero() == null || codigo_barras.contains(f.getNumero())) {
                numeros_e_nulos.add(f);
            }
        }
        numeros_e_nulos.sort((t, t1) -> {
            if (t.getNumero().length() > t1.getNumero().length()) {
                return -1;
            }
            if (t.getNumero().length() < t1.getNumero().length()) {
                return 1;
            }
            return 0;
        });
        return numeros_e_nulos;
    }
    
}

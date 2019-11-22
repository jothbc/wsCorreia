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
import java.sql.Types;
import java.util.ArrayList;
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
        List<Fornecedor> foras_do_list_inicial = new ArrayList<>();
        for (Fornecedor f : todos_banco) {
            if (f.getNumero() == null || codigo_barras.contains(f.getNumero())) {
                numeros_e_nulos.add(f);
            } else {
                foras_do_list_inicial.add(f);
            }
        }
        numeros_e_nulos.sort((t, t1) -> {
            if (t.getNumero() != null && t1.getNumero() != null) {
                if (t.getNumero().length() > t1.getNumero().length()) {
                    return -1;
                }
                if (t.getNumero().length() < t1.getNumero().length()) {
                    return 1;
                }
            }
            return 0;
        });
        for (Fornecedor fora : foras_do_list_inicial) {
            if(!numeros_e_nulos.contains(fora)){
                numeros_e_nulos.add(fora);
            }
        }
        return numeros_e_nulos;
    }

    public boolean addFornecedor(Fornecedor fornecedor) {
        sql = "INSERT INTO fornecedor (nome,banco,numero) VALUES (?,?,?)";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, fornecedor.getNome());
            stmt.setInt(2, fornecedor.getBanco());
            if (fornecedor.getNumero() != null) {
                stmt.setString(3, fornecedor.getNumero());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(FornecedorDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt);
        }
        return false;
    }

}

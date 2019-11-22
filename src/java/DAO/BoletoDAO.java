/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Bean.Boleto;
import Bean.Fornecedor;
import JDBC.ConnectionFactory_Estoque;
import JDBC.ConnectionFactory_Financas;
import funcoes.CDate;
import funcoes.CDbl;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class BoletoDAO extends DAO {

    public BoletoDAO() {
        con = ConnectionFactory_Financas.getConnection();
    }

    public List<Boleto> findAll() {
        sql = "SELECT * FROM vw_fornecedor_boleto ORDER BY vencimento";
        List<Boleto> boletos = new ArrayList<>();
        try {
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Boleto boleto = new Boleto();
                Fornecedor fornecedor = new Fornecedor();
                fornecedor.setId(rs.getInt("bfornecedor_id"));
                fornecedor.setNome(rs.getString("nome_fornecedor"));
                boleto.setId(rs.getInt("bseq"));
                boleto.setFornecedor_id(fornecedor);
                boleto.setCd_barras(rs.getString("bcd_barras"));
                boleto.setValor(rs.getDouble("valor"));
                boleto.setVencimento(rs.getString("vencimento"));
                boleto.setPago(rs.getString("pago"));
                boletos.add(boleto);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            ConnectionFactory_Estoque.closeConnection(con, stmt, rs);
        }
        return boletos;
    }

    public List<Boleto> findPeriodo(String inicio, String fim) {
        sql = "SELECT * FROM vw_fornecedor_boleto WHERE vencimento >= ? AND vencimento <= ? ORDER BY vencimento";
        List<Boleto> boletos = new ArrayList<>();
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, CDate.PTBRtoMYSQL(inicio));
            stmt.setString(2, CDate.PTBRtoMYSQL(fim));
            rs = stmt.executeQuery();
            while (rs.next()) {
                Boleto boleto = new Boleto();
                Fornecedor fornecedor = new Fornecedor();
                fornecedor.setId(rs.getInt("bfornecedor_id"));
                fornecedor.setNome(rs.getString("nome_fornecedor"));
                boleto.setId(rs.getInt("bseq"));
                boleto.setFornecedor_id(fornecedor);
                boleto.setCd_barras(rs.getString("bcd_barras"));
                boleto.setValor(rs.getDouble("valor"));
                boleto.setVencimento(rs.getString("vencimento"));
                boleto.setPago(rs.getString("pago"));
                boletos.add(boleto);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            ConnectionFactory_Estoque.closeConnection(con, stmt, rs);
        }
        return boletos;
    }

    public boolean addBoleto(Boleto boleto) {
        sql = "INSERT INTO boletos (fornecedor_id,cd_barras,valor,vencimento) VALUES (?,?,?,?)";
        stmt = null;
        String datatemp = CDate.PTBRtoMYSQL(boleto.getVencimento());
        try {
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, boleto.getFornecedor_id().getId());
            stmt.setString(2, boleto.getCd_barras());
            stmt.setDouble(3, boleto.getValor());
            stmt.setString(4, datatemp);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("ERRO: " + ex);
            return false;
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt);
        }
    }

    public double getTotalAberto() throws SQLException {
        sql = "SELECT sum(valor) as valor FROM boletos where pago is null";
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

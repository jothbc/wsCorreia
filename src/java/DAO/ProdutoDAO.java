/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Bean.Produto;
import JDBC.ConnectionFactoryFirebird;
import JDBC.ConnectionFactory_Estoque;
import funcoes.CDate;
import funcoes.CDbl;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class ProdutoDAO extends DAO {

    public Produto getProduto(String codigo, boolean preco) {
        Produto produto = new Produto();
        con = ConnectionFactory_Estoque.getConnection();
        sql = "SELECT * FROM product WHERE code = ?";
        double qtd = 0;
        try {
            stmt = con.prepareStatement(sql);
            stmt.setBigDecimal(1, BigDecimal.valueOf(Long.parseLong(codigo)));
            rs = stmt.executeQuery();
            while (rs.next()) {
                produto.codigo = rs.getString("code");
                produto.descricao = rs.getString("description");
                produto.quantidade = 0;
                sql = "SELECT * FROM type_use WHERE code = ?";
                stmt = con.prepareStatement(sql);
                stmt.setBigDecimal(1, BigDecimal.valueOf(Long.parseLong(codigo)));
                rs = stmt.executeQuery();
                while (rs.next()) {
                    produto.quantidade += rs.getDouble("consumption");
                    produto.quantidade += rs.getDouble("bakery");
                    produto.quantidade += rs.getDouble("exchange");
                    produto.quantidade += rs.getDouble("break");
                }
                try (Connection con2 = ConnectionFactoryFirebird.getConnection()) {
                    sql = "SELECT CD_REF,QT_PROD FROM ESTOQUE WHERE CD_REF = ?";
                    stmt = con2.prepareStatement(sql);
                    stmt.setString(1, codigo);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        qtd = rs.getDouble("QT_PROD") - produto.quantidade;
                        produto.quantidade = qtd;
                        break;
                    }
                    if (preco) {
                        sql = "SELECT PRECOS_PDA.* FROM PRECOS_PDA join PRODUTO on PRECOS_PDA.cd_prod = PRODUTO.CD_PROD WHERE PRODUTO.cd_ref = ?";
                        stmt = con2.prepareStatement(sql);
                        stmt.setBigDecimal(1, BigDecimal.valueOf(Long.parseLong(codigo)));
                        rs = stmt.executeQuery();
                        while (rs.next()) {
                            produto.venda = rs.getDouble("VL_VENDA");
                            break;
                        }
                    }
                    con2.close();
                }
                return produto;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory_Estoque.closeConnection(con, stmt, rs);
        }
        return null;
    }

    public boolean inserirProduto(Produto p) {
        con = ConnectionFactory_Estoque.getConnection();
        sql = "SELECT * FROM type_use WHERE code = ?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setBigDecimal(1, BigDecimal.valueOf(Long.parseLong(p.codigo)));
            rs = stmt.executeQuery();
            rs.first();
            if (rs.isFirst()) {//já existe
                sql = "UPDATE type_use SET " + p.tabela + " = " + p.tabela + " + ? WHERE code = ?";
                stmt = con.prepareStatement(sql);
                stmt.setDouble(1, p.quantidade);
                stmt.setBigDecimal(2, BigDecimal.valueOf(Long.parseLong(p.codigo)));
                stmt.executeUpdate();
                addhistorico(p);
                return true;
            } else {//nao existe
                sql = "INSERT INTO type_use(code,description," + p.tabela + ") VALUES (?,?,?)";
                stmt = con.prepareStatement(sql);
                stmt.setBigDecimal(1, BigDecimal.valueOf(Long.parseLong(p.codigo)));
                stmt.setString(2, p.descricao);
                stmt.setDouble(3, p.quantidade);
                stmt.execute();
                addhistorico(p);
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory_Estoque.closeConnection(con, stmt, rs);
        }
        return false;
    }

    public void addhistorico(Produto p) {
        if (con == null) {
            con = ConnectionFactory_Estoque.getConnection();
        }
        sql = "INSERT INTO log_type_use(user,code,description,amount,local,hour,device) VALUES (?,?,?,?,?,?,?)";
        con = ConnectionFactory_Estoque.getConnection();
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, p.usuario);
            stmt.setBigDecimal(2, BigDecimal.valueOf(Long.parseLong(p.codigo)));
            stmt.setString(3, p.descricao);
            stmt.setDouble(4, CDbl.CDblDuasCasas(p.quantidade));
            stmt.setString(5, p.tabela);
            stmt.setString(6, (CDate.getHoraAtualPTBR() + " " + CDate.getHojePTBR().replaceAll("/", "-")));
            stmt.setString(7, "Android");
            stmt.execute();
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            ConnectionFactory_Estoque.closeConnection(con, stmt);
        }
    }

//    public Produto getProdutoVenda(String codigo) {
//        sql = "SELECT * FROM precos_pda WHERE CD_PROD = ?";
//        con = ConnectionFactoryFirebird.getConnection();
//        try {
//            stmt = con.prepareStatement(sql);
//            stmt.setBigDecimal(1, BigDecimal.valueOf(Long.parseLong(codigo)));
//            rs = stmt.executeQuery();
//            while (rs.next()) {
//                Produto produto = new Produto();
//                produto.codigo = codigo;
//                produto.venda = rs.getDouble("VL_VENDA");
//                return produto;
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(ProdutoDAO.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            ConnectionFactoryFirebird.closeConnection(con, stmt, rs);
//        }
//        return null;
//    }
}

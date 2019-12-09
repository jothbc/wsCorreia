/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import JDBC.ConnectionFactory_Estoque;
import JDBC.ConnectionFactory_Financas;
import funcoes.CDate;
import funcoes.CDbl;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.bean.Imposto;

/**
 *
 * @author User
 */
public class ImpostoDAO extends DAO {

    public static final int ABERTO = 1;
    public static final int TODOS = 2;

    public ImpostoDAO() {
        con = ConnectionFactory_Financas.getConnection();
    }

    public double getTotalAberto() throws Exception {
        try {
            sql = "SELECT sum(valor) as valor FROM impostos where pago is null";
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return CDbl.CDblDuasCasas(rs.getDouble("valor"));
            }
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(ImpostoDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception(ex);
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt, rs);
        }
    }

    public List<Imposto> findAll(int opcao) {
        switch (opcao) {
            case ABERTO:
                return getAberto();
            case TODOS:
                return getTodos();
            default:
                return new ArrayList<>();
        }
    }

    private List<Imposto> getAberto() {
        List<Imposto> impostos = new ArrayList<>();
        sql = "SELECT * FROM impostos WHERE pago is null";
        try {
            impostos = get(impostos);
        } catch (SQLException ex) {
            Logger.getLogger(ImpostoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt, rs);
        }
        return impostos;
    }

    private List<Imposto> getTodos() {
        List<Imposto> impostos = new ArrayList<>();
        sql = "SELECT * FROM impostos";
        try {
            impostos = get(impostos);
        } catch (SQLException ex) {
            Logger.getLogger(ImpostoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt, rs);
        }
        return impostos;
    }

    private List<Imposto> get(List<Imposto> impostos) throws SQLException {
        stmt = con.prepareStatement(sql);
        rs = stmt.executeQuery();
        while (rs.next()) {
            Imposto i = new Imposto();
            i.setDescricao(rs.getString("nome"));
            i.setSeq(rs.getInt("seq"));
            i.setValor(rs.getDouble("valor"));
            i.setVencimento(CDate.MYSQLtoPTBR(rs.getString("vencimento")));
            if (rs.getString("pago") != null) {
                i.setPago(CDate.MYSQLtoPTBR(rs.getString("pago")));
            }
            impostos.add(i);
        }
        return impostos;
    }

    public boolean add(Imposto imposto){
        sql = "INSERT INTO impostos (nome,vencimento,valor) VALUES (?,?,?)";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, imposto.getDescricao());
            stmt.setString(2, CDate.PTBRtoMYSQL(imposto.getVencimento()));
            stmt.setDouble(3, imposto.getValor());
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("ERRO: " + ex);
            return false;
        }finally {
            ConnectionFactory_Financas.closeConnection(con, stmt);
        }
    }
    
    public boolean pagar(Imposto imposto) { 
        sql = "UPDATE impostos SET pago = ? WHERE seq = ?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1,CDate.PTBRtoMYSQL(CDate.getHojePTBR()));
            stmt.setInt(2, imposto.getSeq());
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("ERRO: " + ex);
            return false;
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt);
        }
    }
    
    public boolean deletar(Imposto imposto) {
        sql = "DELETE FROM impostos WHERE seq = ?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setInt(2, imposto.getSeq());
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("ERRO: " + ex);
            return false;
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt);
        }
    }
}

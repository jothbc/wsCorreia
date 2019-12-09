/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Bean.Cheque;
import Bean.Fornecedor;
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

    public List<Cheque> findAll() {
        return find("");
    }

    public List<Cheque> findPagos() {
        return find("where cheques.saque is not null");
    }

    public List<Cheque> findAberto() {
        return find("where cheques.saque is null and emissao is not null");
    }

    public List<Cheque> findNulo() {
        /*
        poderia ser verificado o fornecedor_id =999 mas caso alguma alteração futura ocorra é preciso garantir as informações
         */
        return find("where emissao is null and vencimento is null and valor is null and saque is null");
    }

    private List<Cheque> find(String filtro) {
        List<Cheque> cheques = new ArrayList<>();
        sql = "select cheques.*,fornecedor.nome from cheques join fornecedor on cheques.fornecedor_id = fornecedor.id " + filtro;
        try {
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Cheque c = new Cheque();
                c.setSeq(rs.getInt("seq"));
                if (rs.getString("emissao") != null) {
                    c.setEmissao(CDate.MYSQLtoPTBR(rs.getString("emissao")));
                }
                if (rs.getString("vencimento") != null) {
                    c.setPredatado(CDate.MYSQLtoPTBR(rs.getString("vencimento")));
                }
                if (rs.getString("saque") != null) {
                    c.setSaque(CDate.MYSQLtoPTBR(rs.getString("saque")));
                }
                c.setValor(rs.getDouble("valor"));
                Fornecedor f = new Fornecedor();
                f.setId(rs.getInt("fornecedor_id"));
                f.setNome(rs.getString("nome"));
                c.setFornecedor(f);
                cheques.add(c);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ChequeDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt, rs);
        }
        return cheques;
    }

    public boolean addCheque(Cheque c) {
        if (c.getFornecedor().getNome().equals("NULO")) {
            sql = "INSERT INTO cheques(seq,fornecedor_id) VALUES (?,?)";
        } else {
            sql = "INSERT INTO cheques(seq,emissao,vencimento,fornecedor_id,valor) VALUES (?,?,?,?,?)";
        }
        try {
            stmt = con.prepareStatement(sql);
            if (c.getFornecedor().getNome().equals("NULO")) {
                stmt.setInt(1, c.getSeq());
                stmt.setInt(2, c.getFornecedor().getId());
            } else {
                stmt.setInt(1, c.getSeq());
                stmt.setString(2, CDate.PTBRtoMYSQL(c.getEmissao()));
                stmt.setString(3, CDate.PTBRtoMYSQL(c.getPredatado()));
                stmt.setInt(4, c.getFornecedor().getId());
                stmt.setDouble(5, c.getValor());
            }
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ChequeDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt, rs);
        }
    }

    public boolean isReleased(int seq) throws Exception {
        sql = "SELECT * FROM cheques WHERE seq = ?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, seq);
            rs = stmt.executeQuery();
            return rs.first(); //true se ja existir um
        } catch (SQLException ex) {
            Logger.getLogger(ChequeDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Erro na função isRealeased no ChequeDAO");
        }

    }

    public int getProximo() throws Exception {
        sql = "SELECT max(seq) as seq FROM cheques";
        try {
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            rs.first();
            return rs.getInt("seq") + 1;
        } catch (SQLException ex) {
            Logger.getLogger(ChequeDAO.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("falha ao buscar MAX(SEQ) FROM CHEQUES.");
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt, rs);
        }
    }

    public boolean baixarCheque(int seq) {
        if (new ChequeDAO().notIsNull(seq)) {
            return baixarChequeSeq(seq);
        }
        return false;
    }

    private boolean baixarChequeSeq(int seq) {
        sql = "UPDATE cheques SET saque = ? WHERE seq = ?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, CDate.PTBRtoMYSQL(CDate.getHojePTBR()));
            stmt.setInt(2, seq);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ChequeDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt);
        }
    }

    private boolean notIsNull(int seq) {
        List<Cheque> nulos = new ChequeDAO().findNulo();
        for (Cheque cheque : nulos) {
            if (cheque.getSeq() == seq) {
                return false;
            }
        }
        return true;
    }

    public boolean remove(int seq) {
        sql = "DELETE FROM cheques WHERE seq =?";
        try {
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, seq);
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ChequeDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            ConnectionFactory_Financas.closeConnection(con, stmt);
        }
    }
}

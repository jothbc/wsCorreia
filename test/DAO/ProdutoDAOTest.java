/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Bean.Boleto;
import Bean.FiltroData;
import Bean.Fornecedor;
import Bean.Produto;
import funcoes.CDate;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author User
 */
public class ProdutoDAOTest {

    public ProdutoDAOTest() {
    }

    @Test
    public void testSomeMethod() {
    }

    @Test
    //@Ignore
    public void testGetProduto() {
        Produto p = new ProdutoDAO().getProduto("7891153096919", true);
        System.out.println(p.descricao);
        System.out.println(p.venda);
    }

    @Test
    @Ignore
    public void testGetBoletosALl() {
        List<Boleto> boletos = new BoletoDAO().findAll();
        System.out.println(boletos);
    }

    @Test
    @Ignore
    public void testGetBoletosPeriodo() {
        String inicio, fim;
        inicio = CDate.incrementarMes(-3, CDate.getHojePTBR());
        fim = CDate.getHojePTBR();
        List<Boleto> boletos = new BoletoDAO().findPeriodo(inicio, fim);
        System.out.println(boletos);
    }

    @Test
    @Ignore
    public void testGetFornecedorBoleto() {
        String cod = "82660000005920510270000000000108370211911100";
        List<Fornecedor> fornecedores = new FornecedorDAO().getTesteFornecedor(cod);
        if (fornecedores.isEmpty()) {
            return;
        }
        for (Fornecedor f : fornecedores) {
            System.out.println(f.getNome());
        }

    }

}

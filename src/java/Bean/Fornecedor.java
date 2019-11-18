/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bean;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author User
 */
public class Fornecedor implements Comparable {

    private int id;
    private String nome;
    private int banco;
    private String numero;


    public Fornecedor() {
        this.banco = -1;
    }

    public Fornecedor(String nome) {
        this.nome = nome;
        this.banco = -1;
        this.numero = null;
    }

    public Fornecedor(String nome, int banco) {
        this.nome = nome;
        this.banco = banco;
        this.numero = null;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getBanco() {
        return banco;
    }

    public void setBanco(int banco) {
        this.banco = banco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int compareTo(Fornecedor t) {
        if (this.numero.length() < t.numero.length()) {
            return -1;
        }
        if (this.numero.length() > t.numero.length()) {
            return 1;
        }
        return 0;
    }

    public String toString() {
        return this.nome + "  Banco: " + this.banco;
    }

    @Override
    public int compareTo(Object o) {
        Fornecedor t = (Fornecedor) o;
        if (this.numero.length() < t.numero.length()) {
            return -1;
        }
        if (this.numero.length() > t.numero.length()) {
            return 1;
        }
        return 0;
    }
}

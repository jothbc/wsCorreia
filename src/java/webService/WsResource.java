/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webService;

import Bean.Boleto;
import Bean.FiltroData;
import Bean.Fornecedor;
import Bean.HistoricoProduto;
import Bean.Produto;
import Bean.Usuario;
import DAO.BoletoDAO;
import DAO.FornecedorDAO;
import DAO.HistoricoDAO;
import DAO.ProdutoDAO;
import DAO.UsuarioDAO;
import com.google.gson.Gson;
import funcoes.BoletoFuncoes;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("ws")
public class WsResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of WsResource
     */
    public WsResource() {
    }

    /**
     * Retrieves representation of an instance of webService.WsResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Produto/get/{codigo}")
    public Response getProduto(@PathParam("codigo") String codigo) {
        Produto p = new ProdutoDAO().getProduto(codigo, false);
        if (p != null) {
            //return new Gson().toJson(p);
            return Response.status(Response.Status.OK).entity(new Gson().toJson(p)).build();
        }
        //return "";
        return Response.status(Response.Status.NO_CONTENT).entity(p).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Produto/get/preco/{codigo}")
    public String getPreco(@PathParam("codigo") String codigo) {
        Produto p = new ProdutoDAO().getProduto(codigo, true);
        if (p != null) {
            return new Gson().toJson(p);
        }
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Produto/get/historico/{usuario}")
    public String getHistorico(@PathParam("usuario") String usuario) {
        List<HistoricoProduto> hist = new HistoricoDAO().getHistorico(usuario.trim().toUpperCase());
        return new Gson().toJson(hist);
    }

    /*
     DESABILITAR
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("Produto/insert/{codigo}/{quantidade}/{tabela}")
    public String addProd(@PathParam("codigo") String codigo, @PathParam("quantidade") double quantidade, @PathParam("tabela") String tabela) {
        Produto p = new ProdutoDAO().getProduto(codigo, false);
        if (p != null) {
            p.tabela = tabela;
            p.quantidade = quantidade;
            if (new ProdutoDAO().inserirProduto(p)) {
                return "concluido";
            }
        }
        return "erro";
    }

    /*
        CRIPMD5 TEM QUE TA NO ANDROID
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("Usuario/get/{login}/{senha}")
    public String getUsuario(@PathParam("login") String login, @PathParam("senha") String senha) {
        if (new UsuarioDAO().autenticarUsuario(new Usuario(login, cripMD5.criptografar(senha)))) {
            return "autenticado";
        }
        return "erro";
    }

    /**
     * PUT method for updating or creating an instance of WsResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("Produto/post/")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean insertProduto(String json) {
        if (json.isEmpty() || json.equals("")) {
            return false;
        }
        Produto produto = new Gson().fromJson(json, Produto.class);
        return new ProdutoDAO().inserirProduto(produto);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Boleto/get/all")
    public String getBoletos() {
        return new Gson().toJson(new BoletoDAO().findAll());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Boleto/get/periodo/{inicio}/{fim}")
    public String getBoletosPeriodo(@PathParam("inicio") String inicio, @PathParam("fim") String fim) {
        List<Boleto> boletos = new BoletoDAO().findPeriodo(inicio.replaceAll("-", "/"), fim.replaceAll("-", "/"));
        return new Gson().toJson(boletos);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Boleto/get/testeFornecedor/{codigo}")
    public String getTesteFornecedor(@PathParam("codigo") String codigo) {
        if (codigo.length() != 44) {
            codigo = BoletoFuncoes.linhaDigitavelEmCodigoDeBarras(codigo);
            if (codigo == null) {
                return null;
            }
        }
        List<Fornecedor> fornecedores = new FornecedorDAO().getTesteFornecedor(codigo);
        return new Gson().toJson(fornecedores);
    }

    @POST
    @Path("Boleto/post/")
    @Consumes(MediaType.APPLICATION_JSON)
    public String addBoleto(String json) {
        Boleto boleto = new Gson().fromJson(json, Boleto.class);
        if (boleto != null) {
            for (Boleto b : new BoletoDAO().findAll()) {
                if (b.getCd_barras().equals(boleto.getCd_barras())) {
                    return "existe";
                }
            }
            if (new BoletoDAO().addBoleto(boleto)) {
                return "concluido";
            }
        }
        return "falha";
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webService;

import Bean.HistoricoProduto;
import Bean.MobileAccount;
import Bean.Produto;
import Bean.Usuario;
import DAO.HistoricoDAO;
import DAO.MobileAccountDAO;
import DAO.ProdutoDAO;
import DAO.UsuarioDAO;
import com.google.gson.Gson;
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
        return Response.status(Response.Status.NO_CONTENT).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Produto/get/preco/{codigo}")
    public Response getPreco(@PathParam("codigo") String codigo) {
        Produto p = new ProdutoDAO().getProduto(codigo, true);
        if (p != null) {
            return Response.status(Response.Status.OK).entity(new Gson().toJson(p)).build();
        }
        return Response.status(Response.Status.NO_CONTENT).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Produto/get/historico/{usuario}")
    public Response getHistorico(@PathParam("usuario") String usuario) {
        List<HistoricoProduto> hist = new HistoricoDAO().getHistorico(usuario.trim().toUpperCase());
        //return new Gson().toJson(hist);
        if (!hist.isEmpty()) {
            return Response.status(Response.Status.OK).entity(new Gson().toJson(hist)).build();
        }
        return Response.status(Response.Status.NO_CONTENT).entity(null).build();
    }


    /*
        CRIPMD5 TEM QUE TA NO ANDROID
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("Usuario/get/{login}/{senha}/{mac}")
    public Response getUsuario(@PathParam("login") String login, @PathParam("senha") String senha, @PathParam("mac") String mac) {
        if (new UsuarioDAO().autenticarUsuario(new Usuario(login, senha))) {
            if (mac != null && !mac.equals("")) {
                MobileAccount account = new MobileAccount(mac, login);
                if (new MobileAccountDAO().addAcount(account)) {
                    return Response.status(Response.Status.OK).entity("autenticado").build();
                } else {
                    return Response.status(Response.Status.OK).entity("Não foi possível criar a conta.").build();
                }
            }else{
                return Response.status(Response.Status.OK).entity("Ative seu Wifi e tente novamente.").build();
            }
        }
        return Response.status(Response.Status.OK).entity("Não autenticado.").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Usuario/get/{mac_wifi}")
    public Response getUsuarioMac(@PathParam("mac_wifi") String mac_wifi) {
        String response = new UsuarioDAO().getMac(mac_wifi);
        if (response != null) {
            return Response.status(Response.Status.OK).entity(response).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity(response).build();
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
    public Response insertProduto(String json) {
        if (json.isEmpty() || json.equals("")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(false).build(); //400
        }
        if (new ProdutoDAO().inserirProduto(new Gson().fromJson(json, Produto.class))) {
            return Response.status(Response.Status.OK).entity(true).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity(false).build();
    }

}

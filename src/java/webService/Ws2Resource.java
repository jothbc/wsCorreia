/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webService;

import Bean.Boleto;
import Bean.Cheque;
import Bean.Fornecedor;
import DAO.BoletoDAO;
import DAO.ChequeDAO;
import DAO.FornecedorDAO;
import DAO.ImpostoDAO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import funcoes.BoletoFuncoes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.bean.Imposto;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("ws2")
public class Ws2Resource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Ws2Resource
     */
    public Ws2Resource() {
    }

    /**
     * Retrieves representation of an instance of webService.Ws2Resource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of Ws2Resource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Boleto/get/all")
    public Response getBoletos() {
        return Response.status(Response.Status.OK).entity(new Gson().toJson(new BoletoDAO().findAll())).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Boleto/get/periodo/{inicio}/{fim}")
    public Response getBoletosPeriodo(@PathParam("inicio") String inicio, @PathParam("fim") String fim) {
        List<Boleto> boletos = new BoletoDAO().findPeriodo(inicio.replaceAll("-", "/"), fim.replaceAll("-", "/"));
        if (!boletos.isEmpty()) {
            return Response.status(Response.Status.OK).entity(new Gson().toJson(boletos)).build(); //200
        }
        return Response.status(Response.Status.NO_CONTENT).entity(null).build(); //204
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Boleto/get/testeFornecedor/{codigo}")
    public Response getTesteFornecedor(@PathParam("codigo") String codigo) {
        if (codigo.length() != 44) {
            codigo = BoletoFuncoes.linhaDigitavelEmCodigoDeBarras(codigo);
            if (codigo == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(null).build();
            }
        }
        List<Fornecedor> fornecedores = new FornecedorDAO().getTesteFornecedor(codigo);
        if (fornecedores.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity(null).build();
        }
        return Response.status(Response.Status.OK).entity(new Gson().toJson(fornecedores)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Boleto/get/valor/aberto")
    public Response getValorEmAbertoBoleto() {
        try {
            double valor = new BoletoDAO().getTotalAberto();
            return Response.status(Response.Status.OK).entity(new Gson().toJson(valor)).build();
        } catch (Exception ex) {
            Logger.getLogger(WsResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();
        }
    }

    @POST
    @Path("Boleto/post/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putBoleto(String json) {
        Boleto boleto = new Gson().fromJson(json, Boleto.class);
        if (boleto != null) {
            for (Boleto b : new BoletoDAO().findAll()) {
                if (b.getCd_barras().equals(boleto.getCd_barras())) {
                    return Response.status(Response.Status.NOT_MODIFIED).entity("existe").build(); //304
                }
            }
            if (new BoletoDAO().addBoleto(boleto)) {
                return Response.status(Response.Status.OK).entity("concluido").build(); //200
            }
        }
        return Response.status(Response.Status.NO_CONTENT).entity("falha").build(); //204
    }

    @POST
    @Path("Boleto/pagar/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pagarBoleto(String json) {
        try {
            Boleto boleto = new Gson().fromJson(json, Boleto.class);
            if (new BoletoDAO().pagar(boleto)) {
                return Response.ok().entity("true").build(); //200
            }
            return Response.notModified().entity("false").build(); //304
        } catch (JsonSyntaxException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build(); //204
        }
    }

    @POST
    @Path("Boleto/excluir/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response excluirBoleto(String json) {
        try {
            Boleto boleto = new Gson().fromJson(json, Boleto.class);
            if (new BoletoDAO().deletar(boleto)) {
                return Response.ok().entity("true").build(); //200
            }
            return Response.notModified().entity("false").build(); //304
        } catch (JsonSyntaxException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build(); //204
        }
    }

    @GET
    @Path("Cheque/get/proximo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProximoCheque() {
        try {
            int next = new ChequeDAO().getProximo();
            return Response.status(Response.Status.OK).entity(new Gson().toJson(next)).build();
        } catch (Exception ex) {
            Logger.getLogger(Ws2Resource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Cheque/get/valor/aberto")
    public Response getValorEmAbertoCheque() {
        try {
            double valor = new ChequeDAO().getTotalAberto();
            return Response.status(Response.Status.OK).entity(new Gson().toJson(valor)).build();
        } catch (Exception ex) {
            Logger.getLogger(WsResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Cheque/get/{param}")
    public Response getListCheque(@PathParam("param") String param) {
        if (param == null || param.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(null).build(); //400
        }
        switch (param) {
            case "tudo": {
                List<Cheque> cheques = new ChequeDAO().findAll();
                return Response.status(Response.Status.OK).entity(new Gson().toJson(cheques)).build();
            }
            case "pago": {
                List<Cheque> cheques = new ChequeDAO().findPagos();
                return Response.status(Response.Status.OK).entity(new Gson().toJson(cheques)).build();
            }
            case "aberto": {
                List<Cheque> cheques = new ChequeDAO().findAberto();
                return Response.status(Response.Status.OK).entity(new Gson().toJson(cheques)).build();
            }
            case "nulo": {
                List<Cheque> cheques = new ChequeDAO().findNulo();
                return Response.status(Response.Status.OK).entity(new Gson().toJson(cheques)).build();
            }
            default:
                return Response.status(Response.Status.BAD_REQUEST).entity(new Gson().toJson("Parametros Válidos: tudo,pago,aberto,nulo")).build(); //400
        }
    }

    @POST //substituir por PUT
    @Path("Cheque/post/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response baixarCheque(String seq) {
        try {
            if (new ChequeDAO().isReleased(Integer.parseInt(seq))) {
                if (new ChequeDAO().baixarCheque(Integer.parseInt(seq))) {
                    return Response.status(Response.Status.OK).entity("true").build(); //200
                } else {
                    return Response.status(Response.Status.OK).entity("false").build(); //200
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Sequencia incorreta").build(); //404
            }
        } catch (Exception ex) {
            Logger.getLogger(Ws2Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_GATEWAY).entity("ParseExeption").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Cheque/excluir/{id}")
    public Response removeCheque(@PathParam("id") String id) {
        if (new ChequeDAO().remove(Integer.parseInt(id))) {
            return Response.ok().entity("true").build();
        }
        return Response.noContent().entity("false").build();
    }

    @POST
    @Path("Cheque/post/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCheque(String json) {
        try {
            Cheque cheque = new Gson().fromJson(json, Cheque.class);
            if (!new ChequeDAO().isReleased(cheque.getSeq())) {
                if (new ChequeDAO().addCheque(cheque)) {
                    return Response.status(Response.Status.OK).entity("true").build(); //200
                } else {
                    return Response.status(Response.Status.OK).entity("false").build(); //200
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Sequencia já lançada.").build(); //404
            }
        } catch (Exception ex) {
            Logger.getLogger(Ws2Resource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.BAD_GATEWAY).entity("ParseExeption").build(); //502
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Imposto/get/valor/aberto")
    public Response getValorEmAbertoImposto() {
        try {
            double valor = new ImpostoDAO().getTotalAberto();
            return Response.status(Response.Status.OK).entity(new Gson().toJson(valor)).build();
        } catch (Exception ex) {
            Logger.getLogger(WsResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(null).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Imposto/get/aberto")
    public Response getImpostoAberto() {
        List<Imposto> imposto = new ImpostoDAO().findAll(ImpostoDAO.ABERTO);
        return Response.ok().entity(new Gson().toJson(imposto)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("Imposto/get/todos")
    public Response getImpostoTodos() {
        List<Imposto> imposto = new ImpostoDAO().findAll(ImpostoDAO.TODOS);
        return Response.status(Response.Status.OK).entity(new Gson().toJson(imposto)).build();
    }

    @POST
    @Path("Imposto/add/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addImposto(String json) {
        try {
            Imposto imposto = new Gson().fromJson(json, Imposto.class);
            if (new ImpostoDAO().add(imposto)) {
                return Response.ok().entity("true").build(); //200
            }
            return Response.notModified().entity("false").build(); //304
        } catch (JsonSyntaxException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e).build(); //406
        }
    }
    
    @POST
    @Path("Imposto/pagar/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response pagarImposto(String json) {
        try {
            Imposto imposto = new Gson().fromJson(json, Imposto.class);
            if (new ImpostoDAO().pagar(imposto)) {
                return Response.ok().entity("true").build(); //200
            }
            return Response.notModified().entity("false").build(); //304
        } catch (JsonSyntaxException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e).build(); //406
        }
    }
    
    @POST
    @Path("Imposto/remover/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removerImposto(String json) {
        try {
            Imposto imposto = new Gson().fromJson(json, Imposto.class);
            if (new ImpostoDAO().deletar(imposto)) {
                return Response.ok().entity("true").build(); //200
            }
            return Response.notModified().entity("false").build(); //304
        } catch (JsonSyntaxException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e).build(); //406
        }
    }
    

    @POST
    @Path("Fornecedor/post/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addFornecedor(String json) {
        if (json == null || json.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(false).build();
        }
        Fornecedor fornecedor = new Gson().fromJson(json, Fornecedor.class);
        if (new FornecedorDAO().addFornecedor(fornecedor)) {
            return Response.status(Response.Status.OK).entity(true).build(); //200
        }
        return Response.status(Response.Status.NO_CONTENT).entity(false).build(); //204
    }

    @GET
    @Path("Fornecedor/get/all/cheques")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFornecedoresCheques() {
        List<Fornecedor> fornecedores = new FornecedorDAO().getAllFornecedorCheque();
        return Response.status(Response.Status.OK).entity(new Gson().toJson(fornecedores)).build();
    }

}

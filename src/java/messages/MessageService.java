/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author iwuvhugs
 */
@ApplicationScoped
@Path("/messages")
public class MessageService {

    private MessageController controller;

    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public MessageService() {
        this.controller = new MessageController();
//        this.controller.add(new Message());
    }

    /**
     *
     * @return JSON array of all messages
     */
    @GET
    @Produces("application/json")
    public Response getAll() {
        return Response.ok(controller.toJSON()).build();
    }

    /**
     *
     * @param id
     * @return JSON object of a message with given id
     */
    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getById(@PathParam("id") int id) {
        for (Message m : controller.getMessages()) {
            if (m.getId() == id) {
                return Response.ok(m.toJSON()).build();
            }
        }
        return Response.status(404).entity("Message not found").build();
    }

    /**
     *
     * @param sd startDate in String
     * @param ed endDate in String
     * @return JSON array of messages in a given date range
     */
    @GET
    @Path("{startDate}/{endDate}")
    @Produces("application/json")
    public Response getByDateRange(@PathParam("startDate") String sd, @PathParam("endDate") String ed) {
        try {
            Date startDate = formatter.parse(sd);
            Date endDate = formatter.parse(ed);
            MessageController messagesInRange = new MessageController();
            for (Message m : controller.getMessages()) {
                if (m.getSentTime().after(startDate) && m.getSentTime().before(endDate)) {
                    messagesInRange.add(m);
                }
            }
            if (!messagesInRange.getMessages().isEmpty()) {
                return Response.ok(messagesInRange.toJSON()).build();
            } else {
                return Response.status(404).entity("Messages not found").build();
            }
        } catch (ParseException ex) {
            Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(500).entity("Please use 'yyyy-mm-dd' date format").build();
        }
    }

    /**
     *
     * @param str
     * @return JSON array of all messages
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response add(String str) {
        JsonObject json = Json.createReader(new StringReader(str)).readObject();
        Message newMessage;
        try {
            newMessage = new Message(json);
        } catch (ParseException ex) {
            Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(500).entity("Date format error").build();
        }
        if (newMessage != null) {
            controller.add(newMessage);
            return Response.ok(newMessage.toJSON()).build();
        } else {
            return Response.status(404).entity("Message not found").build();
        }
    }

    @PUT
    @Path("{id}")
    public Response put(@PathParam("id") int id, String str) {
        JsonObject json = Json.createReader(new StringReader(str)).readObject();
        Message updatedMessage;
        try {
            updatedMessage = new Message(json);
        } catch (ParseException ex) {
            Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(500).entity("Date format error").build();
        }
        if (updatedMessage != null) {
            for (int i = 0; i < controller.getMessages().size(); i++) {
                if (controller.getMessages().get(i).getId() == id) {
                    controller.put(updatedMessage, id);
                    return Response.ok(updatedMessage.toJSON()).build();
                }
            }
            return Response.status(404).entity("Message not updated").build();
        } else {
            return Response.status(404).entity("Message not found").build();
        }
    }

    /**
     *
     * @param id
     * @return JSON array of all messages
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        for (int i = 0; i < controller.getMessages().size(); i++) {
            if (controller.getMessages().get(i).getId() == id) {
                controller.delete(id);
                return Response.ok().build();
            }
        }
        return Response.status(404).entity("Message not found").build();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

    @Inject
    private MessageController controller;

    /**
     *
     * @return JSON array of all messages
     */
    @GET
    @Produces("application/json")
    public Response getAll() {

        List<Message> allMessages = controller.getAll();
        if (allMessages != null) {
            return Response.ok(controller.toJSON()).build();
        } else {
            return Response.status(404).entity("Messages not found").build();
        }

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

        Message message = controller.getMessageById(id);
        if (message != null) {
            return Response.ok(message.toJSON()).build();
        } else {
            return Response.status(404).entity("Message not found").build();
        }

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

        MessageController messagesInDateRange = controller.getMessagesInDateRange(sd, ed);
        if (messagesInDateRange != null) {
            return Response.ok(messagesInDateRange.toJSON()).build();
        } else {
            return Response.status(404).entity("Messages not found").build();
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

        Message newMessage = controller.addMessage(str);
        if (newMessage != null) {
            return Response.ok(newMessage.toJSON()).build();
        } else {
            return Response.status(404).entity("Message not found").build();
        }

    }

    @PUT
    @Path("{id}")
    public Response put(@PathParam("id") int id, String str) {

        Message updatedMessage = controller.putMessage(id, str);
        if (updatedMessage != null) {
            return Response.ok(updatedMessage.toJSON()).build();
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

        if (controller.deleteMessage(id)) {
            return Response.ok().build();
        } else {
            return Response.status(404).entity("Message not deleted").build();
        }
    }

}

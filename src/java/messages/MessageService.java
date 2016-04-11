/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

    @Inject
    private MessageController controller;

    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    /**
     *
     * @return JSON array of all messages
     */
    @GET
    @Produces("application/json")
    public Response getAll() {

        Connection conn;
        try {
            conn = DBUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM messages");
            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setTitle(rs.getString("title"));
                message.setContents(rs.getString("contents"));
                message.setAuthor(rs.getString("author"));
                message.setSentTime(new Date());
                if (!controller.contains(message)) {
                    controller.add(message);
                }
            }
            return Response.ok(controller.toJSON()).build();
        } catch (SQLException ex) {
            Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(500).entity("Database error").build();

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

        if (controller.contains(controller.getById(id))) {
            return Response.ok(controller.getById(id).toJSON()).build();
        } else {
            Connection conn;
            try {
                conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages WHERE id = ?");
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.isBeforeFirst()) {
                    return Response.status(404).entity("Message not found").build();
                } else {
                    Message message = new Message();
                    message.setId(rs.getInt("id"));
                    message.setTitle(rs.getString("title"));
                    message.setContents(rs.getString("contents"));
                    message.setAuthor(rs.getString("author"));
                    System.out.println(rs.getDate("sentTime"));
                    message.setSentTime(new Date());
                    controller.add(message);
                    return Response.ok(message.toJSON()).build();
                }
            } catch (SQLException ex) {
                Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(500).entity("Database error").build();
            }
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

        // update controller if no data found
        if (!controller.contains(controller.getById(id))) {
            updateController();
        }
        if (controller.contains(controller.getById(id))) {
            Connection conn;
            try {
                conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM messages WHERE id = ?");
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(500).entity("Database error").build();
            }
            controller.deleteById(id);
            return Response.ok().build();
        } else {
            return Response.status(404).entity("Message not found").build();
        }
    }

    private void updateController() {
        Connection conn;
        try {
            conn = DBUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM messages");
            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setTitle(rs.getString("title"));
                message.setContents(rs.getString("contents"));
                message.setAuthor(rs.getString("author"));
                message.setSentTime(new Date());
                if (!controller.contains(message)) {
                    controller.add(message);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

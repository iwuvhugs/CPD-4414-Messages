/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.websocket.OnMessage;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.core.Response;

/**
 *
 * @author iwuvhugs
 */
@ServerEndpoint("/messageSocket")
public class MessageServer {

    @Inject
    private MessageController controller;

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {

        System.out.println(message);
        JsonObject json = Json.createReader(new StringReader(message)).readObject();

        RemoteEndpoint.Basic basic = session.getBasicRemote();
        if (json.containsKey("getAll")) {

            boolean isGetAll = json.getBoolean("getAll");
            if (isGetAll) {
                List<Message> allMessages = controller.getAll();
                if (allMessages != null) {
                    basic.sendText(controller.toJSON().toString());
                } else {
                    basic.sendText("{ 'error' : 'Some meaningful error message.' }");
                }
            } else {
                basic.sendText("{ 'error' : 'Some meaningful error message.' }");
            }

        } else if (json.containsKey("getById")) {

            int id = json.getInt("getById");
            Message msg = controller.getMessageById(id);
            if (msg != null) {
                basic.sendText(msg.toJSON().toString());
            } else {
                basic.sendText("{ 'error' : 'Some meaningful error message.' }");
            }

        } else if (json.containsKey("getFromTo")) {

            JsonArray array = json.getJsonArray("getFromTo");
            String sd = array.getJsonString(0).getString();
            String ed = array.getJsonString(1).getString();

            MessageController messagesInDateRange = controller.getMessagesInDateRange(sd, ed);
            if (messagesInDateRange != null) {
                basic.sendText(messagesInDateRange.toJSON().toString());
            } else {
                basic.sendText("{ 'error' : 'Some meaningful error message.' }");
            }

        } else if (json.containsKey("post")) {

            String str = json.getJsonObject("post").toString();
            Message newMessage = controller.addMessage(str);
            if (newMessage != null) {
                basic.sendText(newMessage.toJSON().toString());
            } else {
                basic.sendText("{ 'error' : 'Some meaningful error message.' }");
            }

        } else if (json.containsKey("put")) {

            int id = json.getJsonObject("put").getInt("id");
            String str = json.getJsonObject("put").toString();
            Message updatedMessage = controller.putMessage(id, str);
            if (updatedMessage != null) {
                basic.sendText("{ 'ok' : true }");
            } else {
                basic.sendText("{ 'error' : 'Some meaningful error message.' }");
            }

        } else if (json.containsKey("delete")) {

            int id = json.getInt("delete");
            if (controller.deleteMessage(id)) {
                basic.sendText("{ 'ok' : true }");
            } else {
                basic.sendText("{ 'error' : 'Some meaningful error message.' }");
            }

        } else {

            basic.sendText("{ 'error' : 'Some meaningful error message.' }");

        }

    }

//    @OnOpen
//    public void onOpen() {
//    }
}

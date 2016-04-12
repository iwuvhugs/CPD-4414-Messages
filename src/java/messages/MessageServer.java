/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

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

            basic.sendText("getAll");

        } else if (json.containsKey("getById")) {

            basic.sendText("getById");

        } else if (json.containsKey("getFromTo")) {

            basic.sendText("getFromTo");

        } else if (json.containsKey("post")) {

            basic.sendText("post");

        } else if (json.containsKey("put")) {

            basic.sendText("put");

        } else if (json.containsKey("delete")) {

            basic.sendText("delete");

        } else {

            basic.sendText("Some meaningful error message");
        }

    }

//    @OnOpen
//    public void onOpen() {
//    }
}

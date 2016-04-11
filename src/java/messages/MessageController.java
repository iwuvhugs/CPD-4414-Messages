/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author iwuvhugs
 */
@ApplicationScoped
public class MessageController {

    private final List<Message> chat = new ArrayList<>();

    public void add(Message message) {
        chat.add(message);
    }

    public void put(Message message, int position) {
        chat.set(position, message);
    }

    public void delete(int position) {
        chat.remove(position);
    }

    public void deleteById(int id) {
        for (int i = 0; i < chat.size(); i++) {
            if (chat.get(i).getId() == id) {
                chat.remove(i);
            }
        }
    }

    public Message getById(int id) {
        for (Message m : chat) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public List<Message> getMessages() {
        return chat;
    }

    public JsonArray toJSON() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Message m : getMessages()) {
            builder.add(m.toJSON());
        }
        return builder.build();
    }

    boolean contains(Message message) {
        if (message != null) {
            for (Message m : chat) {
                if (m.getId() == message.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

}

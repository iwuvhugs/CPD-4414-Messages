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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

/**
 *
 * @author iwuvhugs
 */
@ApplicationScoped
public class MessageController {

    private final List<Message> chat = new ArrayList<>();

    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public JsonArray toJSON() {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (Message m : getMessages()) {
            builder.add(m.toJSON());
        }
        return builder.build();
    }

    private void add(Message message) {
        chat.add(message);
    }

    private void put(Message message, int position) {
        chat.set(position, message);
    }

    private void delete(int position) {
        chat.remove(position);
    }

    private List<Message> getMessages() {
        return chat;
    }

    private void deleteById(int id) {
        for (int i = 0; i < chat.size(); i++) {
            if (chat.get(i).getId() == id) {
                chat.remove(i);
            }
        }
    }

    private Message getById(int id) {
        for (Message m : chat) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    private boolean contains(Message message) {
        if (message != null) {
            for (Message m : chat) {
                if (m.getId() == message.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean contains(int id) {
        if (id != 0) {
            for (Message m : chat) {
                if (m.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    private void putAtId(Message message, int id) {
        if (message != null) {
            for (int i = 0; i < chat.size(); i++) {
                if (chat.get(i).getId() == id) {
                    chat.set(i, message);
                }
            }
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
                message.setSentTime(rs.getDate("sentTime"));
                if (!chat.contains(message)) {
                    chat.add(message);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // REST methods
    public List<Message> getAll() {
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
                message.setSentTime(rs.getDate("sentTime"));
                if (!chat.contains(message)) {
                    chat.add(message);
                }
            }
            return chat;
        } catch (SQLException ex) {
            Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Message getMessageById(int id) {
        if (chat.contains(getById(id))) {
            return getById(id);
        } else {
            Connection conn;
            try {
                conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM messages WHERE id = ?");
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.isBeforeFirst()) {
                    return null;
                } else {
                    Message message = new Message();
                    message.setId(rs.getInt("id"));
                    message.setTitle(rs.getString("title"));
                    message.setContents(rs.getString("contents"));
                    message.setAuthor(rs.getString("author"));
                    message.setSentTime(rs.getDate("sentTime"));
                    chat.add(message);
                    return message;
                }
            } catch (SQLException ex) {
                Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }

    public MessageController getMessagesInDateRange(String sd, String ed) {
        try {
            updateController();
            Date startDate = formatter.parse(sd);
            Date endDate = formatter.parse(ed);
            MessageController messagesInRange = new MessageController();
            for (Message m : chat) {
                if (m.getSentTime().after(startDate) && m.getSentTime().before(endDate)) {
                    messagesInRange.add(m);
                }
            }
            if (!messagesInRange.getMessages().isEmpty()) {
                return messagesInRange;
            } else {
                return null;
            }
        } catch (ParseException ex) {
            Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Message addMessage(String str) {
        JsonObject json = Json.createReader(new StringReader(str)).readObject();
        Message newMessage;
        try {
            newMessage = new Message(json);
        } catch (ParseException ex) {
            Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if (newMessage != null) {
            Connection conn;
            try {
                conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO messages (title, contents, author, sentTime) "
                        + "VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, newMessage.getTitle());
                pstmt.setString(2, newMessage.getContents());
                pstmt.setString(3, newMessage.getAuthor());
                pstmt.setDate(4, new java.sql.Date(newMessage.getSentTime().getTime()));
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    return null;
                } else {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            newMessage.setId(generatedKeys.getInt(1));
                            chat.add(newMessage);
                            return newMessage;
                        } else {
                            return null;
                        }
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return null;
        }
    }

    public Message putMessage(int id, String str) {
        JsonObject json = Json.createReader(new StringReader(str)).readObject();
        if (!contains(id)) {
            updateController();
        }
        Message updatedMessage;
        try {
            updatedMessage = new Message(json);
        } catch (ParseException ex) {
            Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if (updatedMessage != null) {
            Connection conn;
            try {
                conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE messages SET title = ?, contents = ?, author = ?, sentTime = ? "
                        + "WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, updatedMessage.getTitle());
                pstmt.setString(2, updatedMessage.getContents());
                pstmt.setString(3, updatedMessage.getAuthor());
                pstmt.setDate(4, new java.sql.Date(updatedMessage.getSentTime().getTime()));
                pstmt.setInt(5, id);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    return null;
                } else {
                    updatedMessage.setId(id);
                    putAtId(updatedMessage, id);
                    return updatedMessage;
                }
            } catch (SQLException ex) {
                Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean deleteMessage(int id) {
        // update controller if no data found
        if (!chat.contains(getById(id))) {
            updateController();
        }
        if (chat.contains(getById(id))) {
            Connection conn;
            try {
                conn = DBUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM messages WHERE id = ?");
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(MessageService.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}

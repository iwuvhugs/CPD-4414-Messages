/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonObject;

/**
 *
 * @author iwuvhugs
 */
public class Message {

    private int id;
    private String title;
    private String contents;
    private String author;
    private Date sentTime;

    final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            'T'HH:mm:ss.SSS'Z'");

    public Message() {
        this.id = 0;
        this.title = "";
        this.contents = "";
        this.author = "";
    }

    public Message(JsonObject json) throws ParseException {
        System.out.println(json.toString());
        this.id = 0;
        this.title = json.getString("title");
        this.contents = json.getString("contents");
        this.author = json.getString("author");
        this.sentTime = formatter.parse(json.getString("sentTime"));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public JsonObject toJSON() {
        JsonObject object = Json.createObjectBuilder()
                .add("id", id)
                .add("title", title)
                .add("contents", contents)
                .add("author", author)
                .add("sentTime", (sentTime != null) ? formatter.format(sentTime) : "")
                .build();

        return object;

    }

}

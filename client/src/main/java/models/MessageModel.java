package models;

import org.json.JSONObject;

public class MessageModel {
    private String from;
    private String to;
    private String command;
    private Object extraData;
    private JSONObject json;

    public MessageModel(String from, String to) {
        this.from = from;
        this.to = to;
        json = new JSONObject(this);
    }

    public MessageModel(String from, String to, Object extraData) {
        this.from = from;
        this.to = to;
        this.extraData = extraData;
        json = new JSONObject(this);
    }

    public MessageModel(String from, String to, String command) {
        this.from = from;
        this.to = to;
        this.command = command;
        json = new JSONObject(this);
    }

    public MessageModel(String from, String to, String command, Object extraData) {
        this.from = from;
        this.to = to;
        this.command = command;
        this.extraData = extraData;
        json = new JSONObject(this);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }

    public MessageModel put(String key, Object obj) {
        this.json.put(key, obj);
        return this;
    }

    public String json() {
        return this.json.toString();
    }
}

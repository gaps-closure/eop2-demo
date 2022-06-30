package com.peratonlabs.closure.eop2.video.requester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Command
{
    private String id;
    private String command;
    
    private static Gson gson = new GsonBuilder()
//            .registerTypeAdapterFactory(typeAdapterFactory)
            .setPrettyPrinting()
            .create();
    
    public static Command fromJson(String json) {
        return gson.fromJson(json, Command.class);
    }
    
    public String toJson() {
        return gson.toJson(this, getClass());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}

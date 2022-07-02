package com.peratonlabs.closure.eop2.video.requester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Request
{
    private String id;
    private String command;
    
    private Integer frameRate;
    private long delay;
    private boolean color = true;
    private boolean blur;
    private boolean scale;
    private Integer scalePercentage;
    
    private static Gson gson = new GsonBuilder()
//            .registerTypeAdapterFactory(typeAdapterFactory)
            .setPrettyPrinting()
            .create();
    
    public Request(String id) {
        this.id = id;
    }
    
    public static Request fromJson(String json) {
        return gson.fromJson(json, Request.class);
    }
    
    public void update(Request request) {
        this.frameRate = request.frameRate;
        this.delay = request.delay;
        this.color = request.color;
        this.blur = request.blur;
        this.scale = request.scale;
        this.scalePercentage = request.scalePercentage;
    }
    
    public String toJson() {
        return gson.toJson(this, getClass());
    }

    public Integer getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(Integer frameRate) {
        this.frameRate = frameRate;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean isColor() {
        return color;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public boolean isBlur() {
        return blur;
    }

    public void setBlur(boolean blur) {
        this.blur = blur;
    }

    public boolean isScale() {
        return scale;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }

    public Integer getScalePercentage() {
        return scalePercentage;
    }

    public void setScalePercentage(Integer scalePercentage) {
        this.scalePercentage = scalePercentage;
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

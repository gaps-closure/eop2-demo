package com.peratonlabs.closure.eop2.video.requester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Request
{
    private Integer frameRate;
    private long delay;
    private boolean isGrayScale;
    private boolean addBlur;
    private boolean scaleImage;
    private Integer imageScale;
    
    private static Gson gson = new GsonBuilder()
//            .registerTypeAdapterFactory(typeAdapterFactory)
            .setPrettyPrinting()
            .create();
    
    public static Request fromJson(String json) {
        return gson.fromJson(json, Request.class);
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

    public boolean isGrayScale() {
        return isGrayScale;
    }

    public void setGrayScale(boolean isGrayScale) {
        this.isGrayScale = isGrayScale;
    }

    public boolean isAddBlur() {
        return addBlur;
    }

    public void setAddBlur(boolean addBlur) {
        this.addBlur = addBlur;
    }

    public boolean isScaleImage() {
        return scaleImage;
    }

    public void setScaleImage(boolean scaleImage) {
        this.scaleImage = scaleImage;
    }

    public Integer getImageScale() {
        return imageScale;
    }

    public void setImageScale(Integer imageScale) {
        this.imageScale = imageScale;
    }
}

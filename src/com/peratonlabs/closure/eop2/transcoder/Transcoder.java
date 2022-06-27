package com.peratonlabs.closure.eop2.transcoder;

import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.*;

import com.peratonlabs.closure.eop2.WebSocketServer;
import com.peratonlabs.closure.eop2.video.requester.Request;

import io.undertow.websockets.core.WebSocketChannel;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Transcoder
{
    private static HashMap<String, Transcoder> clients = new HashMap<String, Transcoder>();
    
    private Integer frameRate;
    private long delay;
    private boolean isGrayScale;
    private boolean addBlur;
    private boolean scaleImage;
    private Integer imageScale;
    
    private Request request;

    private Transcoder(Request request) {
        this.request = request;
    }
    
    public Transcoder() {
        frameRate = 60;
        // Delay is in milliseconds
        delay = (1 / frameRate) * 1000;
        isGrayScale = false;
        addBlur = false;
        scaleImage = false;
        imageScale = 100;
    }

    public Transcoder(Integer rate) {
        frameRate = rate;
        // Delay is in milliseconds
        delay = (1 / frameRate) * 1000;
        isGrayScale = false;
        addBlur = false;
        scaleImage = false;
        imageScale = 100;
    }
    
    public static void addClient(Request request) {
        Transcoder transcoder = new Transcoder(request);
        clients.put(request.getId(), transcoder);
    }
    
    public static void removeClient(Request request) {
        clients.remove(request.getId());
    }

    public static void broadcast(Mat mat) {
        for (Transcoder transcoder : clients.values()) {
            MatOfByte mem = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, mem);
            byte[] memBytes = mem.toArray();

            WebSocketServer.broadcast(memBytes);
            
            System.out.println("Transcoder: " + transcoder.request.getId());
        }
    }
    
    public Mat getNextFrame(Mat mat) {
        // Do not send back a frame until delay has passed (sets a frame rate)
        try {
            Thread.sleep(delay);
        }
        catch (Exception e) {
            System.out.println(e);
        }

        if (isGrayScale) {
            mat = convertGrayScale(mat);
        }

        if (addBlur) {
            mat = addBlur(mat);
        }

        if (scaleImage) {
            mat = changeImageScale(mat);
        }
        return mat;
    }

    public void setFrameRate(Integer newRate) {
        frameRate = newRate;
    }

    public void setGrayScale(Boolean flag) {
        isGrayScale = flag;
    }

    public void addBlur(Boolean flag) {
        addBlur = flag;
    }

    public void disableImageScaling(Boolean flag) {
        scaleImage = false;
    }

    public void scaleImages(Integer percent) {
        scaleImage = true;
        imageScale = percent;
    }

    private Mat changeImageScale(Mat frame) {
        Integer width = (int) frame.size().width;
        Integer height = (int) frame.size().height;
        Integer newWidth = (int) (width * imageScale / 100);
        Integer newHeight = (int) (height * imageScale / 100);

        Size newSz = new Size(newWidth, newHeight);
        Imgproc.resize(frame, frame, newSz);

        Size sz = new Size(width, height);
        Imgproc.resize(frame, frame, sz);
        return frame;
    }

    private Mat convertGrayScale(Mat frame) {
        Mat grayMat = new Mat();
        Imgproc.cvtColor(frame, grayMat, Imgproc.COLOR_RGB2GRAY);
        return grayMat;
    }

    private Mat addBlur(Mat frame) {
        Size size = new Size(45, 45);
        Point point = new Point(20, 30);
        Imgproc.blur(frame, frame, size, point, Core.BORDER_DEFAULT);
        return frame;
    }
}

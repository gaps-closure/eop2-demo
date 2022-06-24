/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Jun 24, 2022
 */
package com.peratonlabs.closure.eop2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.TrackType;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.muxer.MP4Muxer;

public class SequenceEncoder 
{
    private SeekableByteChannel ch;
    private Picture toEncode;
    private RgbToYuv420 transform;
    private H264Encoder encoder;
    private ArrayList<ByteBuffer> spsList;
    private ArrayList<ByteBuffer> ppsList;
    private CompressedTrack outTrack;
    private ByteBuffer _out;
    private int frameNo;
    private MP4Muxer muxer;

    public SequenceEncoder(File out) throws IOException {
        this.ch = NIOUtils.writableFileChannel(out);

        // Transform to convert between RGB and YUV
        transform = new RgbToYuv420(0, 0);

        // Muxer that will store the encoded frames
        muxer = new MP4Muxer(ch, Brand.MP4);

        // Add video track to muxer
        outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, 25);

        // Allocate a buffer big enough to hold output frames
        _out = ByteBuffer.allocate(1920 * 1080 * 6);

        // Create an instance of encoder
        encoder = new H264Encoder();

        // Encoder extra data ( SPS, PPS ) to be stored in a special place of
        // MP4
        spsList = new ArrayList<ByteBuffer>();
        ppsList = new ArrayList<ByteBuffer>();

    }

    public void imageToMP4(BufferedImage bi) {
        // A transform to convert RGB to YUV colorspace
        RgbToYuv420 transform = new RgbToYuv420(0, 0);

        // A JCodec native picture that would hold source image in YUV colorspace
        Picture toEncode = Picture.create(bi.getWidth(), bi.getHeight(), ColorSpace.YUV420);

        // Perform conversion
        transform.transform(AWTUtil.fromBufferedImage(bi), yuv);

        // Create MP4 muxer
        MP4Muxer muxer = new MP4Muxer(sink, Brand.MP4);

        // Add a video track
        CompressedTrack outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, 25);

        // Create H.264 encoder
        H264Encoder encoder = new H264Encoder(rc);

        // Allocate a buffer that would hold an encoded frame
        ByteBuffer _out = ByteBuffer.allocate(ine.getWidth() * ine.getHeight() * 6);

        // Allocate storage for SPS/PPS, they need to be stored separately in a special place of MP4 file
        List<ByteBuffer> spsList = new ArrayList<ByteBuffer>();
        List<ByteBuffer> ppsList = new ArrayList<ByteBuffer>();

        // Encode image into H.264 frame, the result is stored in '_out' buffer
        ByteBuffer result = encoder.encodeFrame(_out, toEncode);

        // Based on the frame above form correct MP4 packet
        H264Utils.encodeMOVPacket(result, spsList, ppsList);

        // Add packet to video track
        outTrack.addFrame(new MP4Packet(result, 0, 25, 1, 0, true, null, 0, 0));

        // Push saved SPS/PPS to a special storage in MP4
        outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));

        // Write MP4 header and finalize recording
        muxer.writeHeader();

    }
    de
}

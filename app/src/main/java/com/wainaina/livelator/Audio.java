package com.wainaina.livelator;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import static com.wainaina.livelator.home.audioOn;

/**
 * Created by Wainaina on 1/11/17.
 * This class will handle all audio requests and functions.
 */

public class Audio {


    private boolean streamingAudio = false;
    public byte[] buffer;
    public static DatagramSocket socket;
    private int port=8080;

    AudioRecord recorder;

    private int sampleRate = 16000 ; // 44100 for music
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;

    public void initializeRecording() {
    streamingAudio = audioOn;
    }

    private boolean stream(){
        return streamingAudio;
    }

    public void startStreaming() {

        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    DatagramSocket socket = new DatagramSocket();
                    Log.d("VS", "Socket Created");

                    byte[] buffer = new byte[minBufSize];

                    Log.d("VS","Buffer created of size " + minBufSize);
                    DatagramPacket packet;

                    final InetAddress livelatorServer = InetAddress.getByName("https://livelator.mybluemix.net");
                    //SocketAddress livelatorAddress = new InetSocketAddress(livelatorServer, port);

                   // final InetAddress livelatorServer = InetAddress.getByName("http://livelator.mybluemix.net");
                   // final SocketAddress livelatorAddress = new InetSocketAddress(livelatorServer);

                    Log.d("VS", "Address retrieved");

                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize*10);
                    Log.d("VS", "Recorder initialized");

                    recorder.startRecording();

                    while(stream()) {

                        //Recording audio from mic into buffer
                        minBufSize = recorder.read(buffer, 0, buffer.length);
                        //I am going to code you sana sana sana Just wait and see. I am King David.
                        //put buffer into a packet
                        packet = new DatagramPacket(buffer,buffer.length,livelatorServer,port);

                        socket.send(packet);
                        System.out.println("MinBufferSize: " +minBufSize);
                    }

                } catch (UnknownHostException hoste) {
                    Log.e("VS", "UnknownHostException");
                }
                catch (IOException ioe) {
                    Log.e("VS", "IOException");
                }
            }
        });

        streamThread.start();
    }
}

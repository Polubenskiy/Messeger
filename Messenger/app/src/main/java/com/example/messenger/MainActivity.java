package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity
{
    byte[] sendBuffer = new byte[100]; // content packet
    byte[] receiveBuffer = new byte[100];

    DatagramSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try
        {
            InetAddress local_network = InetAddress.getByName("0.0.0.0"); // subnet address, to which we will accept
            SocketAddress local_address = new InetSocketAddress(local_network, 9000); // listening port
            socket = new DatagramSocket(null); // creating socket
            socket.bind(local_address); // waiting for packet from everywhere
        } catch (UnknownHostException | SocketException e)
        {
            e.printStackTrace();
        }

        Runnable receiver = new Runnable()
        {
            @Override
            public void run()
            {
                DatagramPacket received_packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                while(true)
                {
                    try
                    {
                        socket.receive(received_packet);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    String data = new String(received_packet.getData(), 0, received_packet.getLength());
                }
            }
        };
        Thread receivingThread = new Thread(receiver, "ReceivingThread");
        receivingThread.start();
    }

    DatagramPacket sendPacket;

    public void onClick(View v)
    {
        EditText txt_address = findViewById(R.id.editText_address); // get address recipient
        EditText txt_port = findViewById(R.id.editText_port);

        String ip = txt_address.getText().toString(); // get port recipient
        int port = Integer.parseInt(txt_port.getText().toString());

        try
        {
            InetAddress address = InetAddress.getByName(ip);
            sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, port);

        } catch (UnknownHostException e)
        {
            e.printStackTrace();
        }

        sendBuffer[0] = 'h';
        sendBuffer[1] = 'e';
        sendBuffer[2] = 'l';
        sendBuffer[3] = 'l';
        sendBuffer[4] = 'o';

        sendPacket.setLength(5);

        Runnable sending = new Runnable()
        {
            @Override
            public void run ()
            {
                try
                {
                    socket.send(sendPacket); // sending a letter
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        };
        Thread sendingThread = new Thread(sending, "SendThread"); // Create flow for send
        sendingThread.start();
    }
}
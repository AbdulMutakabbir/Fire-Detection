package com.samayer.csfd;

import android.util.Log;
import android.widget.Toast;

import com.samayer.csfd.MainActivity;

import java.net.*;
import java.io.*;

public class ClientSocket
{
    private static String TAG = "Client Socket";
    private Socket socket            = null;
    private static DataOutputStream out     = null;
   public ClientSocket(String address, int port, String data)
    {
        try
        {
            socket = new Socket(address, port);
            out    = new DataOutputStream(socket.getOutputStream());
        }
        catch(UnknownHostException u)
        {
            Log.d(TAG,"Cannot connect to host...");
        }
        catch(IOException i)
        {
            Log.d(TAG,"Sending Error...");
        }
        try {
            out.writeChars(data);
            out.close();
        } catch (IOException e) {
            Log.d(TAG,"Sending Error...");
        }
    }
} 
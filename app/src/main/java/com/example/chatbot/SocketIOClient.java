package com.example.chatbot;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIOClient {

    private static SocketIOClient instance;

    private SocketIOClient() {}

    public static synchronized SocketIOClient getInstance() {
        if(instance == null) {
            instance = new SocketIOClient();
        }
        return instance;
    }

    private Socket socket;
    public ChattingManager chatManager;

    public boolean connected;

    public boolean connect(String ip) {
        try {
            if(socket != null)
                socket.disconnect();

            socket = IO.socket(ip);
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on("msg", onMessageReceived);

            if(chatManager != null) {
                setSender(chatManager);
            }

            connected = true;
        }
        catch (Exception e) {
            Log.e("connect", e.getStackTrace().toString());
            connected = false;
        }
        return connected;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // your code...
            Log.d("connect", "connected");
            try {
                socket.emit("register client");
            } catch (Exception e) {
                Log.e("register client", e.getStackTrace().toString());
            }
        }
    };

    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("msg", args[0].toString());
        }
    };

    public void SendMessage(String data) {
        if(!connected)
            return;

        Log.d("request message", data);
        socket.emit("request message", data);
    }

    private void setSender(ChattingManager chattingManager) {
        Log.d("setSender", "success");
        Emitter.Listener receiver = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("push message", "args");
                if(args.length > 0) {
                    String response = args[0].toString();
                    Log.d("push message", response);

                    try {
                        JSONObject data = new JSONObject(response);
                        chattingManager.sendMessageByRoom(data.getString("room"), data.getString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        socket.on("push message", receiver);
    }
}

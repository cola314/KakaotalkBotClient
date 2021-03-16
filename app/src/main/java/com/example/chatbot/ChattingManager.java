package com.example.chatbot;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.HashMap;

public class ChattingManager {

    private Context context;
    private HashMap<String, Notification.Action> sessionMap = new HashMap<String, Notification.Action>();
    private SocketIOClient client = SocketIOClient.getInstance();

    public ChattingManager(Context context) {
        this.context = context;
        client.chatManager = this;
    }

    public void chatHook(String sender, String msg, String room, boolean isGroupChat, Notification.Action session){
        sessionMap.put(room, session);

        JSONObject data = new JSONObject();
        try {
            data.put("sender", sender);
            data.put("msg", msg);
            data.put("room", room);
            data.put("isGroupChat", isGroupChat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.SendMessage(data.toString());
    }

    public void sendMessageByRoom(String room, String value) {
        Notification.Action session = sessionMap.get(room);
        if(session != null) {
            send(session, value);
        }
    }

    private void send(Notification.Action session, String value) {
        Log.i("send()", value);
        Intent sendIntent = new Intent();
        Bundle msg = new Bundle();
        for (RemoteInput inputable : session.getRemoteInputs()) {
            msg.putCharSequence(inputable.getResultKey(), value);
        }
        RemoteInput.addResultsToIntent(session.getRemoteInputs(), sendIntent, msg);

        try {
            session.actionIntent.send(context, 0, sendIntent);
            Log.i("send() complete", value);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}

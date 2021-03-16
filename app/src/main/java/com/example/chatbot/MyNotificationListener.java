package com.example.chatbot;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Toast;

public class MyNotificationListener extends NotificationListenerService {

    private ChattingManager chattingmanager;

    @Override
    public void onCreate() {
        super.onCreate();
        chattingmanager = new ChattingManager(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        if (sbn.getPackageName().equals("com.kakao.talk")) {
            try {
                Notification.WearableExtender wExt = new Notification.WearableExtender(sbn.getNotification());
                for (Notification.Action act : wExt.getActions()) {
                    if (act.getRemoteInputs() != null && act.getRemoteInputs().length > 0) {
                        if (act.title.toString().toLowerCase().contains("reply") ||
                                act.title.toString().toLowerCase().contains("답장")) {
                            Bundle data = sbn.getNotification().extras;
                            String room, sender, msg;
                            boolean isGroupChat = data.get("android.text") instanceof SpannableString;
                            if (Build.VERSION.SDK_INT > 23) {
                                room = data.getString("android.summaryText");
                                if (room == null) isGroupChat = false;
                                else isGroupChat = true;
                                sender = data.get("android.title").toString();
                                msg = data.get("android.text").toString();
                            } else {
                                room = data.getString("android.subText");
                                msg = data.getString("android.text");
                                sender = data.getString("android.title");
                                if (room == null) isGroupChat = false;
                                else isGroupChat = true;
                            }
                            if (room == null) room = sender;
                            chatHook(sender, msg.trim(), room, isGroupChat, act);
                        }
                    }
                }
            } catch(Exception e){
                Log.d("NOTI",e.toString()+"\nAt:"+e.getStackTrace()[0].getLineNumber());
            }
        }
    }

    private void chatHook(String sender, String msg, String room, boolean isGroupChat, Notification.Action session) {
        Log.d("NOTI", "sender: " + sender + "\nmsg: " + msg + "\nroom: " + room + "\nisGroupChat: " + isGroupChat);
        chattingmanager.chatHook(sender, msg, room, isGroupChat, session);
    }
}

package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    final static String BOT_NAME = "야꿍봇";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void start(View v) {
        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));

        String serverIp = ((EditText)findViewById(R.id.serverIp)).getText().toString();
        boolean connectResult = SocketIOClient.getInstance().connect(serverIp);

        if(connectResult) {
            Toast.makeText(this, "연결 성공", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "연결 실패", Toast.LENGTH_SHORT).show();
        }

        /*
        Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (sets != null && sets.contains(getPackageName())) {
            Toast.makeText(this, "알림읽기 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "시작됨", Toast.LENGTH_SHORT).show();
        */
    }
}
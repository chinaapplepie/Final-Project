package com.example.go_in_a_group_test;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private EditText Text_ID;
    private EditText Text_Password;
    private Socket socket;
    final Handler handler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Text_ID = (EditText)findViewById(R.id.editText_ID);
        Text_Password = (EditText)findViewById(R.id.editText_Password);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("192.168.43.247", 10011);
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];//内存中开辟块缓冲区通常采用4的倍数，或1K的倍数作为buffer，有利于较少内存碎片
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, len);
                        // 发到主线程中 收到的数据
                        Message message = Message.obtain();//obtain机制就是最大限度的重复利用对象，避免new太多的msg对象
                        message.what = 1;
                        message.obj = data;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void Log_in(View v){
        if(Text_ID.getText().toString().isEmpty() || Text_Password.getText().toString().isEmpty()){
            Toast.makeText(this,"content cannot be empty!",Toast.LENGTH_SHORT).show();
        }
        else{
            final String id = Text_ID.getText().toString();
            final String password = Text_Password.getText().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //下面得到socket的输出流，并写入内容
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write((id + "//" + password).getBytes("utf-8"));
                        outputStream.flush();//刷新缓冲，将缓冲区中的数据全部取出来

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //Handler主要用于异步消息的处理，这里用于解析msg数据并在RecyclerView控件中显示
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String flag = msg.obj.toString();
                if(flag.equals("True")){
                    Toast.makeText(MainActivity.this,"right!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,MapActivity.class);
                    startActivity(intent);
                }
                else if(flag.equals("False")){
                    Toast.makeText(MainActivity.this,"failed!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

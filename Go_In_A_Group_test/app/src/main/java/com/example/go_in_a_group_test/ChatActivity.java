package com.example.go_in_a_group_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rv;
    private EditText et;
    private Button btn;
    private Socket socket;
    private ArrayList<MyBean> list;
    private MyAdapter adapter;
    private TextView title;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rv = (RecyclerView) findViewById(R.id.rv);
        et = (EditText) findViewById(R.id.et);
        btn = (Button) findViewById(R.id.btn);
        list = new ArrayList<>();
        adapter = new MyAdapter(this);
        title = (TextView) findViewById(R.id.text_Grouptitle);

        final Handler handler = new MyHandler();

        Intent intent = getIntent();
        final String id_s = intent.getStringExtra("id");
        title.setText("Group_ID:"+id_s);
        id = Integer.valueOf(id_s);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("192.168.43.247", 10010);
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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String data = et.getText().toString();
                et.setText("");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //下面得到socket的输出流，并写入内容
                            OutputStream outputStream = socket.getOutputStream();
                            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");    //设置日期格式
                            //本机的port，发送的内容，日期，你的名字，群的id
                            outputStream.write((socket.getLocalPort() + "//" + data + "//" + df.format(new Date())+"//"+
                                    Local_Static_Value.you.Name+"//"+id_s).getBytes("utf-8"));
                            outputStream.flush();//刷新缓冲，将缓冲区中的数据全部取出来

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    //Handler主要用于异步消息的处理，这里用于解析msg数据并在RecyclerView控件中显示
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                int localPort = socket.getLocalPort();
                String[] split = ((String) msg.obj).split("//");
                if(id == Integer.valueOf(split[4])){
                    if (split[0].equals(localPort + "")) {
                        //数据+ 样式 + 时间 +名字
                        MyBean bean = new MyBean(split[1],1,split[2],Local_Static_Value.you.Name);
                        list.add(bean);
                    } else {
                        MyBean bean = new MyBean(split[1],2,split[2],("来自：" + split[3]));
                        list.add(bean);
                    }
                    // 向适配器set数据
                    adapter.setData(list);
                    rv.setAdapter(adapter);
                    //设置布局管理器（垂直布局）
                    LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
                    rv.setLayoutManager(manager);
                }
                else{
                    return;
                }
            }
        }
    }
}

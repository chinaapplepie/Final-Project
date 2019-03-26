package com.example.go_in_a_group_test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Group_Fragment extends Fragment {
    private Socket socket;
    final Handler handler = new Group_Fragment.MyHandler();

    //用于存放ListView字符串的内容
    List<String> dataList;
    ArrayAdapter<String> adapter;
    ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_layout, container, false);

        //下面显示ListView
        dataList = new ArrayList<>();

        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listview = (ListView) view.findViewById(R.id.group_Listview);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int temp_id = Local_Static_Value.yourGroup.get(position).getID();
                Intent intent = new Intent(getContext(),ChatActivity.class);
                intent.putExtra("id",String.valueOf(temp_id));
                startActivity(intent);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("192.168.43.247", 10015);
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];//内存中开辟块缓冲区通常采用4的倍数，或1K的倍数作为buffer，有利于较少内存碎片
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, len);
                        // 发到主线程中 收到的数据
                        Message message = Message.obtain();//obtain机制就是最大限度的重复利用对象，避免new太多的msg对象
                        message.what = 1;
                        message.obj = data;
                        if(data.equals("GetGroup")){

                        }else{
                            handler.sendMessage(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return view;
    }

    //点击搜索按钮
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button sweepButton = (Button) getActivity().findViewById(R.id.btn_retrieve);
        sweepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Local_Static_Value.yourGroup.clear();//每次检索都会清空本地群信息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //下面得到socket的输出流，并写入内容
                            OutputStream outputStream = socket.getOutputStream();
                            outputStream.write(("GetGroup" +"//"+String.valueOf(Local_Static_Value.you.getID())).getBytes("utf-8"));
                            outputStream.flush();//刷新缓冲，将缓冲区中的数据全部取出来
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String[] split = ((String) msg.obj).split("&&");
                for(int i=1; i<split.length ; i++){
                    Local_Static_Value.yourGroup.add(Rebuild_String(split[i]));
                }
                dataList.clear();
                for(int i=0;i<Local_Static_Value.yourGroup.size();i++){
                    dataList.add("ID:"+String.valueOf(Local_Static_Value.yourGroup.get(i).getID())+"\n"+"Centre_Address:"+
                            Local_Static_Value.yourGroup.get(i).getCentre()+"\n"+"Number:"+Local_Static_Value.yourGroup.get(i).getCount());
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    //利用字符串得到群对象
    public Local_Group Rebuild_String(String s){
        String[] split = s.split("//");
        int temp_id = Integer.valueOf(split[0]);
        String temp_centre = split[1];
        String temp_latitude = split[2];
        String temp_Longitude = split[3];
        int temp_count = Integer.valueOf(split[8]);
        Local_Group temp_group = new Local_Group(temp_id,temp_centre,temp_latitude,temp_Longitude,temp_count);
        ArrayList<String>temp_name = new ArrayList<>();
        if(temp_count == 1){
            temp_name.add(split[4]);
        }
        else if(temp_count == 2){
            temp_name.add(split[4]);
            temp_name.add(split[5]);
        }
        else if(temp_count == 3){
            temp_name.add(split[4]);
            temp_name.add(split[5]);
            temp_name.add(split[6]);
        }
        else if(temp_count == 4){
            temp_name.add(split[4]);
            temp_name.add(split[5]);
            temp_name.add(split[6]);
            temp_name.add(split[7]);
        }
        temp_group.Name = temp_name;
        return temp_group;
    }
}
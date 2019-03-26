package com.example.go_in_a_group_test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ListAdapter;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Search_Fragment extends Fragment {
    private Socket socket;
    final Handler handler = new Search_Fragment.MyHandler();
    final Handler handler2 = new Search_Fragment.MyHandler2();
    ArrayList<Local_Group> local_groups = new ArrayList<>();

    TextureMapView mMapView = null;
    AMap aMap;
    //用于记录搜索地址的经纬度
    double latitude;
    double longititude;
    //编辑框
    EditText text_search;
    //目的地标记
    MarkerOptions markerOption;//自定义标记设置类

    //用于存放ListView字符串的内容
    List<String> dataList;
    ArrayAdapter<String> adapter;
    ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_layout, container, false);

        text_search = (EditText) view.findViewById(R.id.editText_search);

        mMapView = (TextureMapView) view.findViewById(R.id.mapView2);
        mMapView.onCreate(savedInstanceState);

        //初始化mMapView
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        //设置地图的放缩级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //设置英文
        aMap.setMapLanguage(AMap.ENGLISH);

        aMap.setMyLocationEnabled(false);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        //初始化目的地标记点
        markerOption = new MarkerOptions();
        markerOption.draggable(false);//设置Marker不可拖动
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果

        aMap.addMarker(markerOption);

        //下面显示ListView
        dataList = new ArrayList<>();

        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listview = (ListView) view.findViewById(R.id.search_Listview);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String temp_id = String.valueOf(local_groups.get(position).getID());
                final int temp_count = local_groups.get(position).getCount();

                Toast.makeText(getContext(),"clicked:"+temp_id,Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Join")//设置对话框的标题
                        .setMessage("Do you want to join a new group?")//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "You cancelled!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(temp_count >=4){
                                    Toast.makeText(getContext(), "The group has reached its maximum size!", Toast.LENGTH_SHORT).show();
                                }else{
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                //下面得到socket的输出流，并写入内容
                                                OutputStream outputStream = socket.getOutputStream();
                                                outputStream.write(("Join"+"//"+temp_id+"//"+String.valueOf(Local_Static_Value.you.getID())).getBytes("utf-8"));
                                                outputStream.flush();//刷新缓冲，将缓冲区中的数据全部取出来
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("192.168.43.247", 10014);
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];//内存中开辟块缓冲区通常采用4的倍数，或1K的倍数作为buffer，有利于较少内存碎片
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, len);
                        // 发到主线程中 收到的数据
                        Message message = Message.obtain();//obtain机制就是最大限度的重复利用对象，避免new太多的msg对象
                        message.what = 1;
                        message.obj = data;
                        System.out.println("返回数据文件："+data);
                        String[] split = ((String) data).split("&&");
                        if(((String) data).equals("Search")){

                        }
                        else if(split[0].equals("Search")){
                            handler.sendMessage(message);
                        }
                        else if(split[0].equals("Join")){
                            handler2.sendMessage(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return view;
    }

    //已知地址获取经纬度方法
    private void getLatlon(String cityName){
        GeocodeSearch geocodeSearch=new GeocodeSearch(getActivity());
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

            }
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                if (i==1000){
                    if (geocodeResult!=null && geocodeResult.getGeocodeAddressList()!=null &&
                            geocodeResult.getGeocodeAddressList().size()>0){

                        GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
                        latitude = geocodeAddress.getLatLonPoint().getLatitude();//纬度
                        longititude = geocodeAddress.getLatLonPoint().getLongitude();//经度
                        String adcode= geocodeAddress.getAdcode();//区域编码

                        Log.e("lgq地理编码", geocodeAddress.getAdcode()+"");
                        Log.e("lgq纬度latitude",latitude+"");
                        Log.e("lgq经度longititude",longititude+"");
                        Log.i("lgq","dddwww===="+longititude);

                    }else {
                        Toast.makeText(getContext(),"地名出错",Toast.LENGTH_SHORT).show();
                        // oastUtils.show(context,"地址名出错");
                    }
                }
            }
        });
        GeocodeQuery geocodeQuery=new GeocodeQuery(cityName.trim(),"29");
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    //点击搜索按钮
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button sweepButton = (Button) getActivity().findViewById(R.id.btn_Searchgroup);
        sweepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_search.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Search content cannot be empty!",Toast.LENGTH_SHORT).show();
                }
                else{

                    //调用百度翻译
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String s = text_search.getText().toString();
                                s = Baidu_Translate.sentStringToBaidu(s);
                                getLatlon(s);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    aMap.clear();
                    markerOption.title(text_search.getText().toString()).
                            snippet(latitude+","+longititude);
                    markerOption.position(new LatLng(latitude,longititude));
                    aMap.addMarker(markerOption);
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longititude), 14.0f));

                    local_groups.clear();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //下面得到socket的输出流，并写入内容
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(("Search"+"//"+latitude+ "//" + longititude+"//"+String.valueOf(Local_Static_Value.you.getID())).getBytes("utf-8"));
                                outputStream.flush();//刷新缓冲，将缓冲区中的数据全部取出来
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
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
                    local_groups.add(Rebuild_String(split[i]));//把符合条件的所有群的信息生成了表单
                }
                System.out.println("表单项："+local_groups.get(0).getCentre());
                dataList.clear();
                for(int i=0;i<local_groups.size();i++){
                    dataList.add("ID:"+String.valueOf(local_groups.get(i).getID())+"\n"+"Centre_Address:"+
                            local_groups.get(i).getCentre()+"\n"+"Number:"+local_groups.get(i).getCount());
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class MyHandler2 extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String[] split = ((String) msg.obj).split("&&");
                if(split[1].equals("True")){
                    Toast.makeText(getContext(),"Success to join!",Toast.LENGTH_SHORT).show();
                }
                else if(split[1].equals("False_Exist")){
                    Toast.makeText(getContext(),"You're already in the group",Toast.LENGTH_SHORT).show();
                }
                else if (split[1].equals("False_No")){
                    Toast.makeText(getContext(),"The group no longer exists.Please Search again",Toast.LENGTH_SHORT).show();
                }
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

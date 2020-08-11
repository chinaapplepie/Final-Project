package com.example.go_in_a_group_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class Compass_Fragment extends Fragment {
    private Socket socket;
    final Handler handler = new MyHandler();

    TextureMapView mMapView = null;
    AMap aMap;
    MyLocationStyle myLocationStyle;//用于实现定位蓝点
    //声明AMapLocationClient类对象
    AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    AMapLocationClientOption mLocationOption = null;
    //用于记录搜索地址的经纬度
    double latitude;
    double longititude;
    //编辑框
    EditText text_compass;
    //目的地标记
    MarkerOptions markerOption;//自定义标记设置类

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compass_layout, container, false);

        text_compass = (EditText) view.findViewById(R.id.editText_compass);

        mMapView = (TextureMapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        //初始化mMapView
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        //设置地图的放缩级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        //设置英文
        aMap.setMapLanguage(AMap.ENGLISH);

        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        aMap.setMyLocationStyle(myLocationStyle);//设置蓝点样式
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        //初始化目的地标记点
        markerOption = new MarkerOptions();
        markerOption.draggable(false);//设置Marker不可拖动
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(true);//设置marker平贴地图效果
        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Create")//设置对话框的标题
                        .setMessage("Do you want to create a new group?")//设置对话框的内容
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
                                dialog.dismiss();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            //下面得到socket的输出流，并写入内容
                                            OutputStream outputStream = socket.getOutputStream();
                                            outputStream.write((text_compass.getText().toString()+"//"+latitude +
                                                    "//" + longititude + "//" + String.valueOf(Local_Static_Value.you.getID())).getBytes("utf-8"));
                                            outputStream.flush();//刷新缓冲，将缓冲区中的数据全部取出来
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }
                        }).create();
                dialog.show();
                return false;
            }
        };
        // 绑定 Marker 被点击事件
        aMap.addMarker(markerOption);
        aMap.setOnMarkerClickListener(markerClickListener);

        /*
        下面是关于一次定位的实现
         */
        mLocationClient = new AMapLocationClient(getContext());
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("192.168.43.247", 10012);
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

        return view;
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    LatLng l1 = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l1, 14.0f));
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        if(null != mLocationClient){
            mLocationClient.onDestroy();
        }
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
                        Toast.makeText(getContext(),"the name of place is wrong!",Toast.LENGTH_SHORT).show();
                        // oastUtils.show(context,"地址名出错");
                    }
                }
            }
        });
        GeocodeQuery geocodeQuery=new GeocodeQuery(cityName.trim(),"29");
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
    }

    //点击搜索按钮
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button sweepButton = (Button) getActivity().findViewById(R.id.btn_Search);
        sweepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_compass.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Search content cannot be empty!",Toast.LENGTH_SHORT).show();
                }
                else{

                    //调用百度翻译
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String s = text_compass.getText().toString();
                                s = Baidu_Translate.sentStringToBaidu(s);
                                System.out.println("字符串："+s);
                                getLatlon(s);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    aMap.clear();

                    markerOption.title(text_compass.getText().toString()).
                            snippet(latitude+","+longititude);
                    markerOption.position(new LatLng(latitude,longititude));
                    aMap.addMarker(markerOption);
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longititude), 14.0f));
                }
            }
        });
    }

    //Handler主要用于异步消息的处理，这里用于解析msg数据并在RecyclerView控件中显示
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String[] split = ((String) msg.obj).split("//");
                if(split[1].equals("True")){
                    Toast.makeText(getContext(),"right!Your group ID"+split[0],Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),"failed!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

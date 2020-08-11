package com.example.go_in_a_group_test;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Baidu_Translate {

    public static String sentStringToBaidu(String RecongnizeResult) {
        String Baidu_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
        String APP_ID = "20190325000280911";
        String APP_SECRET = "RoYNhGGmfLJQnqT98HQd";
        String from = "auto";
        String to = "zh";
        String TranslateResult;

        String salt = String.valueOf(System.currentTimeMillis());

        String src = APP_ID + RecongnizeResult + salt + APP_SECRET;

        String sign = md5(src);

        try {
            String text = URLEncoder.encode(RecongnizeResult,"UTF-8");
            String realUrl = Baidu_URL + "?" + "q=" + text + "&from=" + from + "&to=" + to + "&appid=" + APP_ID + "&salt=" + salt + "&sign=" + sign;
            URL url = new URL(realUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                String temp = buffer.toString();
                JSONObject object = new JSONObject(temp);
                JSONArray trans_result = object.getJSONArray("trans_result");
                JSONObject temp1 = trans_result.getJSONObject(0);
                TranslateResult = temp1.getString("dst");
                return TranslateResult;
            }
            return null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    /** 加密函数*/
    public static String md5(String input){
        if (input == null)
            return null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputByteArray = input.getBytes("utf-8");
            messageDigest.update(inputByteArray);
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        catch (UnsupportedEncodingException e){
            return null;
        }
    }
    /** 辅助加密*/
    private static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

}

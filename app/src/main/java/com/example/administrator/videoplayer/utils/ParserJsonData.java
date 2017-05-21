package com.example.administrator.videoplayer.utils;

import android.util.Log;

import com.example.administrator.videoplayer.beans.VideoBeans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/2/13.
 */

public class ParserJsonData {

    public List<VideoBeans> getJsonData(String url){
        List<VideoBeans> beanList = new ArrayList<>();
            String jsonString = url;
        String a;
            JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(jsonString);
                  String  showapi_res_body = jsonObject.getString("showapi_res_body");
                    JSONObject jsonObject1 = new JSONObject(showapi_res_body);
                    String pagebean = jsonObject1.getString("pagebean");
                    JSONObject jsonObject2 = new JSONObject(pagebean);
                    JSONArray jsonArray = jsonObject2.getJSONArray("contentlist");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        VideoBeans jsonBean = new VideoBeans();
                        jsonObject = jsonArray.getJSONObject(i);
                        jsonBean.setData(jsonObject.getString("create_time"));
                        jsonBean.setLove(jsonObject.getString("love"));
                        jsonBean.setName(jsonObject.getString("name"));
                         jsonBean.setHate(jsonObject.getString("hate"));
                        jsonBean.setTitle(jsonObject.getString("text").replaceAll("\\n","").trim());
                        jsonBean.setVideoUrl(jsonObject.getString("video_uri"));
                        jsonBean.setIconUrl(jsonObject.getString("profile_image"));
                        Log.d("成功解析？？？",jsonObject.getString("video_uri"));
                        beanList.add(jsonBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


        return beanList;
    }
}

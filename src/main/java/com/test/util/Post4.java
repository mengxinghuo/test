package com.test.util;

import com.google.common.collect.Maps;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class Post4 {
    public static String connectionUrl(String url,StringBuffer str,String sessionId){

        String responseMessage = "";
        StringBuffer response = new StringBuffer();
        HttpURLConnection httpConnection = null;
        OutputStreamWriter out = null;
        BufferedReader reader = null;
        try {
            URL urlPost = new URL(url);
            httpConnection = (HttpURLConnection) urlPost.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            // 参数长度太大，不能用get方式
            httpConnection.setRequestMethod("POST");
            // 不使用缓存
            httpConnection.setUseCaches(false);
            // URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
            httpConnection.setInstanceFollowRedirects(true);
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            // 意思是正文是urlencoded编码过的form参数
            httpConnection.setRequestProperty("Connection", "Keep-Alive");
            // 设置请求头信息
            httpConnection.setRequestProperty("Charset", "UTF-8");
            httpConnection.setRequestProperty("Cookie", "JSESSIONID="+sessionId);
            // 设置边界
            String BOUNDARY = "----------" + System.currentTimeMillis();
//            httpConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行connect。
            // 实际上只是建立了一个与服务器的tcp连接，并没有实际发送http请求。

            httpConnection.connect();
            out = new OutputStreamWriter(httpConnection.getOutputStream(),"UTF-8");

            // 正文，正文内容其实跟get的URL中'?'后的参数字符串一致

          /*JSONObject jsonObject=new JSONObject();
          try{
              jsonObject.put("username", username);
              jsonObject.put("password", password);
              jsonObject.put("captcha", captcha);
          }catch(Exception e){
              e.printStackTrace();
          }*/
          /*
          Map parames = new HashMap<String, String>();
            parames.put("username", "username");
            parames.put("username", "username");
            parames.put("captcha", "captcha");
          */
            // 构建请求参数
            StringBuffer sb = new StringBuffer();
            sb.append(str);
            //写入参数,DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写道流里面
            out.write(sb.toString());
            System.out.println("send_url:" + url);
            System.out.println("send_data:" + sb.toString());
            // flush and close
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
                if (null != reader) {
                    reader.close();
                }
                if (null != httpConnection) {
                    httpConnection.disconnect();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        try {
            reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(),"UTF-8"));
            while ((responseMessage = reader.readLine()) != null) {
                response.append(responseMessage);
                response.append("\n");
            }

            if (!"failure".equals(response.toString())) {
                System.out.println("success");
            } else {
                System.out.println("failue");
            }
            // 将该url的配置信息缓存起来
            System.out.println(response.toString());
            System.out.println(httpConnection.getResponseCode());
            System.out.println(httpConnection.getResponseMessage());
//            JSONObject jsonObject = JSONObject.fromObject(response.toString());
//            System.out.println(jsonObject);
//            System.out.println(jsonObject.get("status"));
//            System.out.println(jsonObject.get("msg"));
//            System.out.println(jsonObject.get("data"));
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error"; // 自定义错误信息
    }
    public static void main(String[] args){
//        String url = "http://39.104.61.50:8089/manage/employee/get_employee_list.do";
//        String url = "http://101.132.172.240:8094/user/login.do";
        String url = "http://101.132.172.240:8094/user/loginSession.do";
        StringBuffer sb = new StringBuffer();
        Map<String,String> map = Maps.newHashMap();
        map.put("userName","admin234");
        map.put("password","123456789");
        int count =0;
        for(String key :map.keySet()){
        if(count==map.size()-1){
            sb.append(key).append("=").append(map.get(key));
        }else{
            sb.append(key).append("=").append(map.get(key)).append("&");
        }
            count =count+1;
        }
//        System.out.println(sb.toString());
        String str = connectionUrl(url,sb,null);

        JSONObject jsonObject = JSONObject.fromObject(str);
        String userStr = jsonObject.get("data").toString();
        Map map1 = JsonUtil.string2Obj(userStr,Map.class,String.class,Object.class);
        String strs = JsonUtil.string2Obj(map1.get("sessionId").toString(),String.class);
        String userstrs = JsonUtil.string2Obj(map1.get("user").toString(),String.class);
//        User user = JsonUtil.string2Obj(userstrs,User.class);
        System.out.println(map1.toString());
        System.out.println(strs);
        System.out.println(userstrs);
//        System.out.println(user.toString());

        String url2 = "http://101.132.172.240:8094/product/search.do";
        String str2 = connectionUrl(url2,null,strs);
        System.out.println(str2);
    }
}

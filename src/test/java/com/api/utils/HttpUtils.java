package com.api.utils;

import com.alibaba.fastjson.JSONObject;
import com.api.cases.BaseCase;
import com.api.pojo.CaseInfo;
import io.qameta.allure.Step;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class HttpUtils {
    private static Logger log=Logger.getLogger(HttpUtils.class);
    /**
     * 发送http请求
     *
     * @param method      请求方式
     * @param contentType 参数提交方式
     * @param url         url
     * @param params      参数
     * @return
     */
    @Step("发送http请求 call方法")
    public static HttpResponse call(String method, String contentType, String url, String params,Map<String,String>headers) {
        //如果请求是post
        try {
            if ("post".equalsIgnoreCase(method)) {
                //如果提交参数类型是json
                if ("json".equalsIgnoreCase(contentType)) {
                    return HttpUtils.jsonPost(url, params,headers);
                    //吐过提交参数类型是from
                } else if ("from".equalsIgnoreCase("contentType")) {
                    //将key:value,key:value转成key=value&key=value
                    params = jsonToFrom(params);
                    return HttpUtils.fromPost(url, params,headers);
                } else {
                    System.out.println("method = " + method + ", contentType = " + contentType + ", url = " + url + ", params = " + params);
                }
                //如果请求是get
            } else if ("get".equalsIgnoreCase(method)) {
                //处理url
                return HttpUtils.get(url,headers);
                //如果请求是patch
            } else if ("patch".equalsIgnoreCase(method)) {
                return HttpUtils.patch(url, params,headers);
            } else {
                System.out.println("method = " + method + ", contentType = " + contentType + ", url = " + url + ", params = " + params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把json参数转成from参数
     * @param jsonstr json参数
     * @return
     */
    public static String jsonToFrom(String jsonstr) {
        //把json转成map
        Map<String, String> map = JSONObject.parseObject(jsonstr, Map.class);
        //获取所有的key
        Set<String> keySet = map.keySet();
        String result = "";
        //遍历key
        for (String key : keySet) {
            //获取key对应的value值
            String value = map.get(key);
            //拼接参数  key=value&
            result += key + "=" + value + "&";
        }
        //去掉最后一个多余的&
        result = result.substring(0, result.length() - 1);
        return result;
    }

    /**
     * 发送http get请求
     * @param url           必须带参数  url?key=value&key2=value2
     * @param headers       请求头
     * @throws Exception
     */
    public static HttpResponse get(String url,Map<String,String>headers) throws Exception {
        //1、创建get请求并写入接口地址
        //http://apis.juhe.cn/simpleWeather/query?city=上海&key=d295d7a7dcfed4e166cd93bc6499982f
        HttpGet get = new HttpGet(url);
        //2、在get请求上添加请求头
        addHeaders(headers,get);
        //3、创建一个客户端
        HttpClient client = HttpClients.createDefault();
        //4、客户端发送请求，并返回响应对象（响应头、响应体、响应状态码）
        HttpResponse response = client.execute(get);
        //获取响应数据
//        printResponse(response);
        return response;
    }

    /**
     * 发送http fromPost请求
     *
     * @param url
     * @param params        from格式参数
     * @param headers       请求头
     * @throws Exception
     */
    public static HttpResponse fromPost(String url, String params,Map<String,String>headers) throws Exception {
        //1、创建post请求并写入地址
        HttpPost post = new HttpPost(url);
        //2、在请求上添加请求头
        addHeaders(headers,post);
//        post.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
//        post.addHeader("Content-Type", "application/x-www-from-urlencoded");
        //3、请求参数  加载请求体里面
//        String json="{\"mobile_phone\":\"13611111111\",\"pwd\":\"12345678\"}";
        StringEntity stringEntity = new StringEntity(params, "utf-8");
        post.setEntity(stringEntity);
        //4、创建客户端
        HttpClient client = HttpClients.createDefault();
        //5、发送请求并接受响应数据
        HttpResponse response = client.execute(post);
        //获取响应数据
//        printResponse(response);
        return response;
    }

    /**
     * 发送http jsonPost请求
     *
     * @param url       接口地址
     * @param params    key=value格式参数
     * @param headers   请求头
     * @throws Exception
     */
    public static HttpResponse jsonPost(String url, String params,Map<String,String> headers) throws Exception {
        //1、创建post请求并写入地址
        HttpPost post = new HttpPost(url);
        //2、在请求上添加请求头
        addHeaders(headers,post);
//        post.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
//        post.addHeader("Content-Type", "application/json");
        //3、请求参数  加载请求体里面
//        String json="{\"mobile_phone\":\"13611111111\",\"pwd\":\"12345678\"}";
        StringEntity stringEntity = new StringEntity(params, "utf-8");
        post.setEntity(stringEntity);
        //4、创建客户端
        HttpClient client = HttpClients.createDefault();
        //5、发送请求并接受响应数据
        HttpResponse response = client.execute(post);
        //获取响应数据
//        printResponse(response);
        return response;
    }

    /**
     * 发送http patch请求
     *
     * @param url
     * @param params key=value格式参数
     * @throws Exception
     */
    public static HttpResponse patch(String url, String params,Map<String,String>headers) throws Exception {
        //1、创建patch请求并写入地址
        HttpPatch patch = new HttpPatch(url);
        //2、在请求上添加请求头
        addHeaders(headers,patch);
//        post.addHeader("X-Lemonban-Media-Type", "lemonban.v2");
//        post.addHeader("Content-Type", "application/json");
        //3、请求参数  加载请求体里面
//        String json="{\"mobile_phone\":\"13611111111\",\"pwd\":\"12345678\"}";
        StringEntity stringEntity = new StringEntity(params, "utf-8");
        patch.setEntity(stringEntity);
        //4、创建客户端
        HttpClient client = HttpClients.createDefault();
        //5、发送请求并接受响应数据
        HttpResponse response = client.execute(patch);
        //获取响应数据
//        printResponse(response);
        return response;
    }

    /**
     * 获取响应头、响应体、响应状态码（方法只返回了响应体body）
     * @param response
     */
    public static String printResponse(HttpResponse response) {
        try {
            //获取所有响应头
            Header[] headers = response.getAllHeaders();
            log.info("headers :" + Arrays.toString(headers));
            //获取指定响应头信息
            Header[] header = response.getHeaders("Content-Type");
            String contentType = Arrays.toString(header);
            log.info("content-Type :" + contentType);
            //获取响应体
            HttpEntity entity = response.getEntity();
            String body = null;
            body = EntityUtils.toString(entity);
            log.info("body :"+body);
            //获取响应状态码
            int code = response.getStatusLine().getStatusCode();
            log.info("status :" + code);
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *添加请求头
     * @param headers       请求头map
     * @param request       请求对象
     */
    public static void addHeaders(Map<String,String>headers, HttpRequest request){
        Set<String> keySet = headers.keySet();
        for (String name : keySet) {
            String value=headers.get(name);
            request.addHeader(name,value);
        }
    }


}

package com.api.utils;

import com.alibaba.fastjson.JSONPath;
import com.api.constants.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口鉴权类
 */
public class AuthenticationUtils {
    //类似jmeter用户变量  vars
    public static Map<String,Object> VARS=new HashMap<String,Object>();



    /**
     * 使用jsonpath获取内容存储到VARS变量，给其他接口使用
     * @param json                  json字符串（响应体）
     * @param expression            jsonpath表达式
     * @param key                   存储到VARS中的key
     */
    public static void jsonToVars(String json,String expression,String key){
        //如果json不为空，则继续操作
        if(StringUtils.isNotBlank(json)){
            //使用jsonPath获取内容
            Object obj = JSONPath.read(json,expression);
            System.out.println(key+":" + obj);
            //如果获取内容不为空，存入VARS变量，给其他接口使用
            if(obj !=null){
                AuthenticationUtils.VARS.put(key,obj);
            }
        }

    }

    /**
     * 获取带token的请求头Map集合
     * @return
     */
    public static Map<String, String> getTokenHeader() {
        //3.1、从VARS中获取token
        Object token = AuthenticationUtils.VARS.get("${token}");
        System.out.println("Recharge token:"+token);
        //3.2、将获取到的token添加到请求头
        //3.3、改造call支持传递请求头
        Map<String,String>headers=new HashMap<String, String>();
        headers.put("Authorization","Bearer "+token);
        headers.putAll(Constants.HEADERS);
        return headers;
    }
}

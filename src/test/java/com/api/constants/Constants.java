package com.api.constants;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    //数据驱动excel路径
//    public static final String EXCEL_PATH=Constants.class.getClassLoader().getResource("./cases_v3.xlsx").getPath();
    public static final String EXCEL_PATH="src/test/resources/cases_v3.xlsx";
    //参数化properties文件路径
    public static final String PROPERTIES_PATH="src/test/resources/params.properties";
    //默认请求头
    public static final Map<String ,String> HEADERS=new HashMap<String,String>();
    //excel响应回写列
    public static final int RESPONSE_WRITE_BACK_CELLNUM=8;
    //excel断言回写列
    public static final int ASSERT_WRITE_BACK_CELLNUM=10;
    //数据库连接url
    public static final String JDBC_URL="jdbc:mysql://api.lemonban.com:3306/futureloan?useUnicode=true&characterEncoding=utf-8";
    //数据库用户名
    public static final String JDBC_USERNAME="future";
    //数据库密码
    public static final String JDBC_PASSWORD="123456";
}

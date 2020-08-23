package com.api.utils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class SQLUtils {
    public static void main(String[] args) {
        //DBUtils
//        mapHander();
//        String sql="select leave_amount from member where id='2102557';";
//        Object result=getSingleResult(sql);
//        System.out.println(result.getClass());
    }

    /**
     * 查询数据库单行单列结果集
     * @param sql       sql语句
     * @return          返回查询结果
     */
    public static Object getSingleResult(String sql) {
        //如果sql语句为空返回空值
        if(StringUtils.isBlank(sql)){
            System.out.println("sql语句为空");
            return null;
        }
        Object result=null;
        //创建QueryRunner对象
        QueryRunner rqunner = new QueryRunner();
        //连接数据库
        Connection conn=JDBCUtils.getConnection();
        try {
            //创建处理结果集对象
            ScalarHandler handler=new ScalarHandler();
            //执行查询语句
            result = rqunner.query(conn, sql, handler);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(conn);
        }
        return result;
    }

    public static void mapHander() {
        QueryRunner rqunner = new QueryRunner();
        Connection conn=JDBCUtils.getConnection();
        try {
            String sql="select *from member where mobile_phone='13222222222';";
            MapHandler handler=new MapHandler();
            Map<String, Object> map = rqunner.query(conn, sql, handler);
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(conn);
        }
    }
}

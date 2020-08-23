package auto.api.lemon;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;

public class GetDemo {
    public static void main(String[] args) throws IOException {
        //1、创建get请求并写入接口地址
        HttpGet get=new HttpGet("http://apis.juhe.cn/simpleWeather/query?city=上海&key=d295d7a7dcfed4e166cd93bc6499982f");
        //2、在get请求上添加请求头
//        get.addHeader();
        //3、创建一个客户端
        HttpClient client=HttpClients.createDefault();
        //4、客户端发送请求，并返回响应对象（响应头、响应体、响应状态码）
            HttpResponse response=client.execute(get);
        //获取响应头
        Header[] allHeaders = response.getAllHeaders();
        String allheaders = Arrays.toString(allHeaders);
        System.out.println(allheaders);
        //获取指定响应头信息
        Header[] contentType=response.getHeaders("Content-Type");
        System.out.println(Arrays.toString(contentType));
        //获取响应体
        HttpEntity entity=response.getEntity();
        String body = EntityUtils.toString(entity);
        System.out.println(body);
        //获取响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);
    }
}

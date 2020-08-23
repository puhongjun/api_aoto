package auto.api.lemon;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Arrays;

public class PostDemo {
    public static void main(String[] args) throws Exception {
        //1.创建post请求并写入接口地址
        //2.请求类型
        //3.请求头
        //4.发送请求
        //5.接收请求

        //1、创建post请求并写入地址
        HttpPost post=new HttpPost("http://api.lemonban.com/futureloan/member/register");
        //2、在请求上添加请求头
        post.addHeader("X-Lemonban-Media-Type","lemonban.v1");
        post.addHeader("Content-Type","application/json");
        //3、请求参数  加载请求体里面
        String json="{\"mobile_phone\":\"13611111111\",\"pwd\":\"12345678\"}";
        StringEntity stringEntity=new StringEntity(json,"utf-8");
        post.setEntity(stringEntity);
        //4、创建客户端
        HttpClient client=HttpClients.createDefault();
        //5、发送请求并接受响应数据
        HttpResponse response=client.execute(post);
        //获取所有响应头
        Header[] headers=response.getAllHeaders();
        System.out.println("headers :"+Arrays.toString(headers));
        //获取指定响应头信息
        Header[] header=response.getHeaders("Content-Type");
        String contentType=Arrays.toString(header);
        System.out.println("content-Type :"+contentType);
        //获取响应体
        HttpEntity entity=response.getEntity();
        String body= EntityUtils.toString(entity);
        System.out.println("body :"+body);
        //获取响应状态码
        int code=response.getStatusLine().getStatusCode();
        System.out.println("code :"+code);
    }
}

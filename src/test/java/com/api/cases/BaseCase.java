package com.api.cases;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.api.constants.Constants;
import com.api.pojo.CaseInfo;
import com.api.pojo.WriteBackData;
import com.api.utils.AuthenticationUtils;
import com.api.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class BaseCase {
    private static Logger log=Logger.getLogger(BaseCase.class);

    //读取testng.xml sheetIndex参数
    public int sheetIndex;
    @BeforeSuite
    public void init() throws Exception {
        log.info("===============================init======================================");
        Constants.HEADERS.put("X-Lemonban-Media-Type", "lemonban.v2");
        Constants.HEADERS.put("Content-Type", "application/json");
        //存入参数的变量
        //可放入随机值
//        AuthenticationUtils.VARS.put("${register_mb}","13222222222");
//        AuthenticationUtils.VARS.put("${register_pwd}","12345678");
//        AuthenticationUtils.VARS.put("${member_id}","2060176");
//        AuthenticationUtils.VARS.put("${amount}","1000");
        //只能放常量
        Properties properties=new Properties();
        String path=Constants.PROPERTIES_PATH;
        FileInputStream fis=new FileInputStream(path);
        properties.load(fis);
        //baproperties中的内容一次性放入VARS中，需要强转为map类型
        AuthenticationUtils.VARS.putAll((Map)properties);
        log.info("AuthenticationUtils.VARS======================"+AuthenticationUtils.VARS);
    }

    @AfterSuite
    public void finish(){
        log.info("===============================finish======================================");
        //执行批量回写
        ExcelUtils.batchWrite();
    }

    @BeforeClass
    @Parameters({"sheetIndex"})
    public void beforeClass(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    /**
     * 创建回写对象，添加到批量回写集合中
     * @param rownum    行号
     * @param cellnum   列号
     * @param content   内容
     */
    public void addWriteBackData(int rownum, int cellnum,String content) {
        //创建回写对象
        WriteBackData wbd=new WriteBackData(sheetIndex,rownum,cellnum,content);
        //添加到回写集合
        ExcelUtils.wbdList.add(wbd);
    }

    /**
     * 接口响应断言
     * @param body              接口响应字符串
     * @param expectResult      excel中响应期望值
     * @return                  断言结果
     */
    public boolean assertResponse(String body, String expectResult) {
        //json转map
        Map<String,Object> map = JSONObject.parseObject(expectResult, Map.class);
        Set<String> keySet=map.keySet();
        boolean assertResponseFlag=true;
        for (String expression : keySet) {
            //1.获取期望值
            Object expectValue = map.get(expression);
            //2.通过传入的jsonpath表达式：expression找到实际值
            Object actualValue = JSONPath.read(body, expression);
            //3.比较期望值和实际值
            if(expectValue==null&&actualValue!=null){
                assertResponseFlag=false;
                break;
            }
            if(expectValue==null&&actualValue==null){
                continue;
            }
            if(!expectValue.equals(actualValue)){
                assertResponseFlag=false;
                break;
            }
        }
        System.out.println("响应断言结果："+assertResponseFlag);
        return assertResponseFlag;
    }

    /**
     * 参数化替换方法
     * 1、在excel中填写占位符
     * 2、Base Case类中准备占位符和实际值的对应值，并保存在VARS中  ${register_pwd}=1234567
     * 3、每个Case第一行调用paramsReplase方法执行参数化替换
     * @param caseInfo      caseInfo对象
     */
    public static void paramsReplace(CaseInfo caseInfo) {
        //sql:select leave_amount from member where mobile_phone='${register_pwd}';
        //params:{"$.code":0,"$.msg":"OK","$.data.mobile_phone":"${register_mb}"}
        //exceptResult:{"$.code":0,"$.msg":"OK","$.data.mobile_phone":"${register_mb}"}
        //获取VARS中所有的key
        Set<String> keySet= AuthenticationUtils.VARS.keySet();
        //参数化替换遍历
        for (String key : keySet) {
            //key=${register}  value=13222222222
            //key是占位符  value是实际值

            String value=AuthenticationUtils.VARS.get(key).toString();

            //替换sql
            if(StringUtils.isNotBlank(caseInfo.getSql())){
                //replace(old,new) 方法通过用 new字符替换字符串中出现的所有 old字符，并返回替换后的新字符串
                String sql= caseInfo.getSql().replace(key,value);
                //把替换之后的sql重新设置到caseInfo中
                caseInfo.setSql(sql);
            }
            //替换参数
            if(StringUtils.isNotBlank(caseInfo.getParams())){
                String params= caseInfo.getParams().replace(key,value);
                caseInfo.setParams(params);
            }
            //替换期望值
            if(StringUtils.isNotBlank(caseInfo.getExpectResult())){
                String expectResult= caseInfo.getExpectResult().replace(key,value);
                caseInfo.setExpectResult(expectResult);
            }
            //替换url
            if(StringUtils.isNotBlank(caseInfo.getUrl())){
                String url= caseInfo.getUrl().replace(key,value);
                caseInfo.setUrl(url);
            }
        }
    }

}

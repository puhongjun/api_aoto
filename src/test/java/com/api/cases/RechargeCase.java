package com.api.cases;

import com.alibaba.fastjson.JSONPath;
import com.api.constants.Constants;
import com.api.pojo.CaseInfo;
import com.api.utils.AuthenticationUtils;
import com.api.utils.ExcelUtils;
import com.api.utils.HttpUtils;
import com.api.utils.SQLUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.*;

import java.math.BigDecimal;
import java.util.Map;

public class RechargeCase extends BaseCase{
    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) throws Exception {
        //1、参数化替换
        paramsReplace(caseInfo);
        //2、数据库前置查询结果（数据断言必须在接口执行前后都查询）
        Object beforeSqlResult = SQLUtils.getSingleResult(caseInfo.getSql());
        //2.1获取带token的请求头
        Map<String, String> headers = AuthenticationUtils.getTokenHeader();
        //3、调用接口
        HttpResponse response= HttpUtils.call(caseInfo.getType(),caseInfo.getContentType(),
                caseInfo.getUrl(),caseInfo.getParams(),headers);
        //打印响应
        String body=HttpUtils.printResponse(response);
        //4、断言响应结果
        //{"$.code":0,"$.msg":"OK","$.data.mobile_phone":"13222222222"}
        String expectResult=caseInfo.getExpectResult();
        boolean assertResponseFlag = assertResponse(body, expectResult);
        //5、添加接口响应回写内容
        addWriteBackData(caseInfo.getCaseId(), Constants.RESPONSE_WRITE_BACK_CELLNUM,body);
        //6、数据库后置查询结果
        Object afterSqlResult = SQLUtils.getSingleResult(caseInfo.getSql());
        //7、数据库断言
        boolean assertSqlFlag = sqlAssert(caseInfo, beforeSqlResult, afterSqlResult);
        //8、添加断言回写内容
        String assertResult=assertResponseFlag && assertSqlFlag ? "passed":"failed";
        addWriteBackData(caseInfo.getCaseId(),Constants.ASSERT_WRITE_BACK_CELLNUM,assertResult);
        //9、添加日志
        //10、报表断言
        Assert.assertEquals(assertResult,"passed");

    }

    /**
     * 数据库断言
     * @param caseInfo              caseInfo对象
     * @param beforeSqlResult       sql前置查询结果
     * @param afterSqlResult        sql后置查询结果
     * @return
     */
    public boolean sqlAssert(CaseInfo caseInfo, Object beforeSqlResult, Object afterSqlResult) {
        boolean flag=false;
        if(StringUtils.isNotBlank(caseInfo.getSql())){
            if(beforeSqlResult ==null|| afterSqlResult ==null){
                System.out.println("数据库断言失败");
            }else{
                //输出结果集
                BigDecimal b1=(BigDecimal) beforeSqlResult;
                BigDecimal b2=(BigDecimal) afterSqlResult;
                System.out.println("b1="+b1+" ,b2="+b2);
                //充值后 - 充值前  b2 - b1
                BigDecimal result1 = b2.subtract(b1);
                System.out.println("result1="+result1);
                //jsonpath获取参数
                Object obj = JSONPath.read(caseInfo.getParams(), "$.amount");
                if(obj==null){
                    System.out.println("断言失败");
                }else{
                    //参数amount
                    BigDecimal result2=new BigDecimal(obj.toString());
                    System.out.println("result2="+result2);
                    //结果==参数amount  compareto()：返回值-1表示小于  1表示大于
                    if(result1.compareTo(result2)==0){
                        System.out.println("数据库断言成功");
                        flag=true;
                    }else{
                        System.out.println("数据库断言失败");
                    }
                }
            }
        }else{
            System.out.println("sql为空，不需要数据库断言");
        }
        return flag;
    }


    @DataProvider
    public Object[] datas(){
        Object[] datas= ExcelUtils.getDatas(sheetIndex,1, CaseInfo.class);
        return datas;
    }
}

package com.api.cases;

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

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class RegisterCase extends BaseCase{

    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) throws Exception {
        //1、参数化替换遍历
        paramsReplace(caseInfo);
        //2、数据库前置查询结果（数据断言必须在接口执行前后都查询）
        Object beforeSqlResult = SQLUtils.getSingleResult(caseInfo.getSql());
        //3、调用接口
       HttpResponse response=HttpUtils.call(caseInfo.getType(),caseInfo.getContentType(),
               caseInfo.getUrl(),caseInfo.getParams(), Constants.HEADERS);
       //响应体
       String body=HttpUtils.printResponse(response);
        //4、断言响应结果
        //{"$.code":0,"$.msg":"OK","$.data.mobile_phone":"13222222222"}
        String expectResult=caseInfo.getExpectResult();
        boolean assertResponseFlag = assertResponse(body, expectResult);
        //5、响应回写
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
                Long l1=(Long) beforeSqlResult;
                Long l2=(Long) afterSqlResult;
                System.out.println("l1="+l1+",l2="+l2);
                //接口执行之前查询结果为0，接口执行之后查询结果为1
                if(l1==0&&l2==1){
                    System.out.println("数据库断言成功");
                    flag=true;
                }else {
                    System.out.println("数据库断言失败");
                }
            }
        }else{
            System.out.println("sql为空，不需要数据库断言");
        }
        return flag;
    }

    @DataProvider
    public Object[] datas(){
            Object[] datas=ExcelUtils.getDatas(sheetIndex,1, CaseInfo.class);
            return datas;
    }

}

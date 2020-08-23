package com.api.cases;

import com.api.constants.Constants;
import com.api.pojo.CaseInfo;
import com.api.utils.AuthenticationUtils;
import com.api.utils.ExcelUtils;
import com.api.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class AddCase extends BaseCase{
    @Test(dataProvider = "datas")
    public void test(CaseInfo caseInfo) throws Exception {
        //1、参数化替换
        paramsReplace(caseInfo);
        //2、数据库前置查询结果（数据断言必须在接口执行前后都查询）
        //2.1获取带token的请求头
        Map<String, String> headers = AuthenticationUtils.getTokenHeader();
        //3、调用接口
        HttpResponse response = HttpUtils.call(caseInfo.getType(), caseInfo.getContentType(),
        caseInfo.getUrl(), caseInfo.getParams(), headers);
        //响应数据
        String body=HttpUtils.printResponse(response);
        //3.1、获取项目loan_id,并存放到map中
        AuthenticationUtils.jsonToVars(body,"$.data.id","${loan_id}");
        //4、断言响应结果
        //{"$.code":0,"$.msg":"OK"}
        String expectResult=caseInfo.getExpectResult();
        boolean assertResponseFlag = assertResponse(body, expectResult);

        //5、响应回写
        addWriteBackData(caseInfo.getCaseId(), Constants.RESPONSE_WRITE_BACK_CELLNUM,body);
        //6、数据库后置查询结果
        //7、数据库断言
        //8、添加断言回写内容
        String assertResult=assertResponseFlag ? "passed":"failed";
        addWriteBackData(caseInfo.getCaseId(),Constants.ASSERT_WRITE_BACK_CELLNUM,assertResult);
        //9、添加日志
        //10、报表断言
        Assert.assertEquals(assertResult,"passed");

    }



    @DataProvider
    public Object[] datas() {
        Object[] datas = ExcelUtils.getDatas(sheetIndex, 1, CaseInfo.class);
        return datas;
    }

}

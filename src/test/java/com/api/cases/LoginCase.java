package com.api.cases;

import com.api.constants.Constants;
import com.api.pojo.CaseInfo;
import com.api.utils.AuthenticationUtils;
import com.api.utils.ExcelUtils;
import com.api.utils.HttpUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.apache.http.HttpResponse;
import org.testng.Assert;
import org.testng.annotations.*;

public class LoginCase extends BaseCase{
    @Test(dataProvider = "datas",description = "普通会员登录")
    @Description("description")
    @Step("普通会员登录方法")
    public void test(CaseInfo caseInfo) throws Exception {
        //1、参数化替换
        paramsReplace(caseInfo);
        //2、数据库前置查询结果（数据断言必须在接口执行前后都查询）
        //3、调用接口
        HttpResponse response = HttpUtils.call(caseInfo.getType(), caseInfo.getContentType(),
                caseInfo.getUrl(), caseInfo.getParams(), Constants.HEADERS);
        //响应数据
        String body=HttpUtils.printResponse(response);
        //3.1、从响应体里面获取token,并存放到map中
        AuthenticationUtils.jsonToVars(body,"$.data.token_info.token","${token}");
        //3.2、获取member_id,并存放到map中
        AuthenticationUtils.jsonToVars(body,"$.data.id","${member_id}");
        //4、断言响应结果
        //{"$.code":0,"$.msg":"OK","$.data.mobile_phone":"13222222222"}
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

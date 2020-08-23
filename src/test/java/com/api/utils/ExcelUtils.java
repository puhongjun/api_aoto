package com.api.utils;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.api.constants.Constants;
import com.api.pojo.CaseInfo;
import com.api.pojo.WriteBackData;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel工具类
 */
public class ExcelUtils {
    //批量回写集合
    public static List<WriteBackData> wbdList=new ArrayList<WriteBackData>();


    /**
     *获取testng测试方法  数据驱动
     * @param sheetIndex    sheet开始读取位置
     * @param sheetNum      sheet读取个数
     * @param clazz         映射关系字节码
     * @return
     */
    public static Object[] getDatas(int sheetIndex,int sheetNum,Class clazz){
        try{
            List<CaseInfo> list=ExcelUtils.read(sheetIndex,sheetNum, clazz);
            //集合转成数组
            Object[] datas=list.toArray();
            return datas;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取Excel并返回映射关系集合
     * @param sheetIndex    sheet开始读取位置
     * @param sheetNum      sheet读取个数
     * @param clazz         映射关系字节码
     * @return
     * @throws Exception
     */
    public static List read(int sheetIndex,int sheetNum,Class clazz) throws Exception {
        //加载excel文件
        //String path=ExcelUtils.class.getClassLoader().getResource("./cases_v1.xlsx").getPath();
        FileInputStream filePath=new FileInputStream(Constants.EXCEL_PATH);
        //esaypoi
        ImportParams params=new ImportParams();
        //从第一个sheet开始读取
        params.setStartSheetIndex(sheetIndex);
        //每次读取一个sheet
        params.setSheetNum(sheetNum);
        //importExcel(Excel文件流，映射关系字节码对象，导入参数)
        List caseInfoList=ExcelImportUtil.importExcel(filePath, clazz,params);

        return caseInfoList;
    }

    /**
     * 批量回写
     */
    public static void batchWrite(){

        FileInputStream fis=null;
        FileOutputStream fos=null;
        try{
            fis=new FileInputStream(Constants.EXCEL_PATH);
            //解析数据用poi提供对象
            Workbook excel= WorkbookFactory.create(fis);

            for(WriteBackData writeBackData:wbdList){
                //获取sheetIndex
                int sheetIndex = writeBackData.getSheetIndex();
                //获取行号
                int rownum = writeBackData.getRownum();
                //获取列号
                int cellnum = writeBackData.getCellnum();
                //获取回写内容
                String content = writeBackData.getContent();
                //选择sheet
                Sheet sheet=excel.getSheetAt(sheetIndex);
                //读取每一行
                Row row=sheet.getRow(rownum);
                //读取每一个单元格Missing Cell Policy 当cell为null时，返回一个空的cell对象
                Cell cell=row.getCell(cellnum,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                //修改
                cell.setCellValue(content);
            }
            fos=new FileOutputStream(Constants.EXCEL_PATH);
            //写入
            excel.write(fos);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(fis!=null){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(fos!=null){
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

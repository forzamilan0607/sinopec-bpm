package com.dstz.demo.utils;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @Author: xiangzhi.liu
 * @Date: 2019/11/1 17:17
 */
@Slf4j
public class EasyPoiUtil {
    /**
          * 功能描述：根据文件路径来导入Excel
          * @param filePath 文件路径
          * @param titleRows 表标题的行数
          * @param headerRows 表头行数
          * @param pojoClass Excel实体类
          * @return
         */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        //判断文件是否存在
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("模板不能为空");
        } catch (Exception e) {
            log.error("系统错误:",e);
 
        }
        return list;
    }
 
            /**
      * 功能描述：根据接收的Excel文件来导入Excel,并封装成实体类
      * @param file 上传的文件
      * @param titleRows 表标题的行数
      * @param headerRows 表头行数
      * @param pojoClass Excel实体类
      * @return
      */
    public static <T> List<T> importExcel(MultipartFile file, Integer titleRows, Integer headerRows, Class<T> pojoClass) {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new RuntimeException("excel文件不能为空");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
 
        }
        return list;
    }
}

package com.dstz.demo.core.model.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class DelayTaskCountDTO implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * 物料编码
     */
    private String materialNo;
    /**
     * 流程标题
     */
    private String processTitle;
    /**
     * 延期任务数
     */
    private String delayTaskNums;
    /**
     * 当前任务名称
     */
    private String curTaskName;
    /**
     * 当前任务待处理人
     */
    private String curTaskHandler;
    /**
     * 当前任务创建时间
     */
    private String taskStartTime;
    /**
     * 当前任务截止时间
     */
    private String taskEndTime;


}

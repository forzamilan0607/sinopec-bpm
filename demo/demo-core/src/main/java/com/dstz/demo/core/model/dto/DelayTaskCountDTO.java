package com.dstz.demo.core.model.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class DelayTaskCountDTO implements Serializable, IExcelModel, IExcelDataModel {
    private static final long serialVersionUID = 1L;
    /**
     * 物料编码
     */
    @Excel(name = "需求计划号", width = 20)
    private String materialNo;
    /**
     * 流程标题
     */
    @Excel(name = "流程标题", width = 50)
    private String processTitle;
    /**
     * 延期任务数
     */
    @Excel(name = "延期任务数", width = 20)
    private String delayTaskNums;
    /**
     * 当前任务名称
     */
    @Excel(name = "任务名称", width = 20)
    private String curTaskName;
    /**
     * 当前任务待处理人
     */
    @Excel(name = "待处理人", width = 20)
    private String curTaskHandler;
    /**
     * 当前任务创建时间
     */
    @Excel(name = "当前任务创建时间", width = 30)
    private String taskStartTime;
    /**
     * 当前任务截止时间
     */
    @Excel(name = "当前任务截止时间", width = 30)
    private String taskEndTime;

    @Excel(name = "失败原因", width = 80)
    private String errorMsg;


    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    private int rowNum;

    @Override
    public int getRowNum() {
        return this.rowNum;
    }

    @Override
    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

}

package com.dstz.demo.core.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import com.dstz.base.core.model.BaseModel;

import java.util.Date;
import java.util.List;


/**
 * 案例 实体对象 (该对象与实体 Demo业务对象的json 直接转化)
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-08-24 18:06:04
 */
public class TimeLimit extends BaseModel implements IExcelModel, IExcelDataModel {
	/**
	* 主键
	*/
	protected  String id;
	/**
	* 关联流程实例ID
	*/
	protected  String instId;
	/**
	* 关联的任务ID
	*/
	protected  String taskId;

	/**
	 * 物料编码
	 */
	@Excel(name = "物料编码", width = 20)
	protected String materialNo;

	/**
	 * 关联的任务ID
	 */
	@Excel(name = "流程标题", width = 30)
	protected  String subject;

	@Excel(name = "任务名称", width = 20)
	protected  String name;

	/**
	 * 待处理人
	 */
	@Excel(name = "待处理人", width = 20)
	protected String assigneeNames;

	@Excel(name = "处理时限(天)", width = 15)
	protected  String timeLimit;

	@Excel(name = "任务创建时间", width = 20, exportFormat = "yyyy-MM-dd hh:mm:ss")
	protected  Date taskStartTime;

	@Excel(name = "任务截止时间", width = 20, exportFormat = "yyyy-MM-dd hh:mm:ss")
	protected Date expectDealTime;

	protected  Date taskDealTime;

	@Excel(name = "延期处理时间", width = 20, exportFormat = "yyyy-MM-dd hh:mm:ss")
	protected  Date taskEndTime;

	@Excel(name = "延期处理时长", width = 20)
	protected  String delayTimePeriod;

	/**
	 * 延期申请人
	 */
	@Excel(name = "延期申请人", width = 15)
	protected String applyUser;

	/**
	 * 延期原因
	 */
	@Excel(name = "延期原因", width = 30)
	protected  String delayReason;

	/**
	 * 延期申请时间
	 */
	protected Date applyDate;


	/**
	 * 是否延期任务
	 */
	protected  int isDelay;
	/**
	 * 延期时间
	 */
	protected  String delayTime;

	protected  Date createTime;

	protected  String creatBy;

	protected  String remark;

	protected boolean delayFlag;

//	@Excel(name = "失败原因", width = 80)
	private String errorMsg;

	public TimeLimit(String taskId) {
		this.taskId = taskId;
	}

	public TimeLimit() {
	}

	public boolean getDelayFlag() {
		return delayFlag;
	}

	public void setDelayFlag(boolean delayFlag) {
		this.delayFlag = delayFlag;
	}

	public String getDelayTimePeriod() {
		return delayTimePeriod;
	}

	public void setDelayTimePeriod(String delayTimePeriod) {
		this.delayTimePeriod = delayTimePeriod;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public int getIsDelay() {
		return isDelay;
	}

	public void setIsDelay(int isDelay) {
		this.isDelay = isDelay;
	}

	public String getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(String delayTime) {
		this.delayTime = delayTime;
	}

	public String getDelayReason() {
		return delayReason;
	}

	public void setDelayReason(String delayReason) {
		this.delayReason = delayReason;
	}

	public String getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}

	public Date getTaskStartTime() {
		return taskStartTime;
	}

	public void setTaskStartTime(Date taskStartTime) {
		this.taskStartTime = taskStartTime;
	}

	public Date getTaskDealTime() {
		return taskDealTime;
	}

	public void setTaskDealTime(Date taskDealTime) {
		this.taskDealTime = taskDealTime;
	}

	public Date getTaskEndTime() {
		return taskEndTime;
	}

	public void setTaskEndTime(Date taskEndTime) {
		this.taskEndTime = taskEndTime;
	}

	@Override
	public Date getCreateTime() {
		return createTime;
	}

	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreatBy() {
		return creatBy;
	}

	public void setCreatBy(String creatBy) {
		this.creatBy = creatBy;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getAssigneeNames() {
		return assigneeNames;
	}

	public void setAssigneeNames(String assigneeNames) {
		this.assigneeNames = assigneeNames;
	}

	public Date getExpectDealTime() {
		return expectDealTime;
	}

	public void setExpectDealTime(Date expectDealTime) {
		this.expectDealTime = expectDealTime;
	}

	public String getApplyUser() {
		return applyUser;
	}

	public void setApplyUser(String applyUser) {
		this.applyUser = applyUser;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public String getMaterialNo() {
		return materialNo;
	}

	public void setMaterialNo(String materialNo) {
		this.materialNo = materialNo;
	}


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
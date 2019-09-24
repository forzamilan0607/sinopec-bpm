package com.dstz.demo.core.model;

import com.dstz.base.core.model.BaseModel;

import java.util.Date;
import java.util.List;


/**
 * 案例 实体对象 (该对象与实体 Demo业务对象的json 直接转化)
 * @author aschs
 * @email aschs@qq.com
 * @time 2018-08-24 18:06:04
 */
public class TimeLimit extends BaseModel{
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

	protected  String name;
	/**
	 * 关联的任务ID
	 */
	protected  String subject;
	/**
	* 是否延期任务
	*/
	protected  int isDelay;
	/**
	* 延期时间
	*/
	protected  String delayTime;
	/**
	* 延期原因
	*/
	protected  String delayReason;
	/**
	* 字段1
	*/
	protected  String timeLimit;
	/**
	 * 字段1
	 */
	protected  Date taskStartTime;
	/**
	 * 字段1
	 */
	protected  Date taskDealTime;
	/**
	 * 字段1
	 */
	protected  Date taskEndTime;
	/**
	 * 字段1
	 */
	protected  Date createTime;
	/**
	 * 字段1
	 */
	protected  String creatBy;
	/**
	 * 字段1
	 */
	protected  String remark;

	protected boolean delayFlag;


	protected  String delayTimePeriod;

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
}
package com.dstz.demo.core.model;

import com.dstz.base.core.model.BaseModel;

import java.util.Date;

/**
 * Created by lilx on 2019/9/22.
 */
public class BpmTaskNew extends BaseModel {
    protected String id;
    protected String name;
    protected String subject;
    protected String v;
    protected String taskId;
    protected String nodeId;
    protected String defId;
    protected String Y;
    protected String Z;
    protected String status;
    protected Integer priority;
    protected Date aa;
    protected String ab;
    protected String parentId;
    protected String I;
    protected String ac;
    protected String x;
    protected Date createTime;
    protected String createBy;
    protected Integer supportMobile;
    protected String ad;

    protected boolean  delayFlag;

    public boolean isDelayFlag() {
        return delayFlag;
    }

    public void setDelayFlag(boolean delayFlag) {
        this.delayFlag = delayFlag;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getDefId() {
        return defId;
    }

    public void setDefId(String defId) {
        this.defId = defId;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }

    public String getZ() {
        return Z;
    }

    public void setZ(String z) {
        Z = z;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getAa() {
        return aa;
    }

    public void setAa(Date aa) {
        this.aa = aa;
    }

    public String getAb() {
        return ab;
    }

    public void setAb(String ab) {
        this.ab = ab;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getI() {
        return I;
    }

    public void setI(String i) {
        I = i;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Integer getSupportMobile() {
        return supportMobile;
    }

    public void setSupportMobile(Integer supportMobile) {
        this.supportMobile = supportMobile;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }
}

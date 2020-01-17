package com.dstz.demo.core.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 物流流程表
 * @Author: xiangzhi.liu
 * @Date: 2019/11/1 16:41
 */
@Data
public class PurchasePlanHisRec implements Serializable{
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String materialProcessId;

    private String content;

    private String updateUser;

    private Date updateTime;

}

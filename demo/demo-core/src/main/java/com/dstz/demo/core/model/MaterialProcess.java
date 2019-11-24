package com.dstz.demo.core.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.dstz.base.api.model.IBaseModel;
import com.dstz.base.core.model.BaseModel;
import com.dstz.bpm.api.model.inst.IBpmInstance;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 物流流程表
 * @Author: xiangzhi.liu
 * @Date: 2019/11/1 16:41
 */
@Data
public class MaterialProcess extends BaseModel {
    /** 询价名称 */
    @Excel(name = "询价名称")
    @NotBlank(message = "询价名称不能为空")
    private String enquiryName;
    /** 采购申请 */
    @Excel(name = "采购申请")
    @NotBlank(message = "采购申请不能为空")
    private String purchaseAply;
    /** 物料编码 */
    @Excel(name = "物料编码")
    @NotBlank(message = "物料编码不能为空")
    private String materialNo;
    /** 物料描述 */
    @Excel(name = "物料描述")
    @NotBlank(message = "物料描述不能为空")
    private String materialDesc;
    /** 单位 */
    @Excel(name = "单位")
    @NotBlank(message = "单位不能为空")
    private String unit;
    /** 数量 */
    @Excel(name = "数量")
    @NotNull(message = "数量不能为空")
    private Double number;
    /** 车间 */
    @Excel(name = "车间")
    @NotBlank(message = "车间不能为空")
    private String plant;
    /** 预留单号 */
    @Excel(name = "预留单号")
    @NotBlank(message = "预留单号不能为空")
    private String reservedNumber;
    /** 备注 */
    @Excel(name = "备注")
    private String remark;
    /** 预留 */
    @Excel(name = "预留单号")
    private String reserved;
    /** 流程ID */
    private String processId;

    private String instId;

    private boolean hasInst;
    private boolean disabled;
    public boolean isHasInst() {
        return this.instId != null;
    }
}

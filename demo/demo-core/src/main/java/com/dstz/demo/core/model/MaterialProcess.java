package com.dstz.demo.core.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.handler.inter.IExcelDataModel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import com.dstz.base.api.model.IBaseModel;
import com.dstz.base.core.model.BaseModel;
import com.dstz.bpm.api.model.inst.IBpmInstance;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 物流流程表
 * @Author: xiangzhi.liu
 * @Date: 2019/11/1 16:41
 */
@Data
public class MaterialProcess extends BaseModel implements IExcelModel, IExcelDataModel {

    @Excel(name = "需求计划号", width = 20)
    @Length(max = 40, message = "需求计划号长度不能大于40")
    private String reqPlanNo;

    /** 物料编码 */
    @Excel(name = "物料编码", width = 20)
    @NotBlank(message = "物料编码不能为空")
    @Length(max = 50, message = "物料编码长度不能大于50")
    private String materialNo;

    /** 物料描述 */
    @Excel(name = "物料描述", width = 35)
    @NotBlank(message = "物料描述不能为空")
    @Length(max = 255, message = "物料描述长度不能大于255")
    private String materialDesc;

    /** 单位 */
    @Excel(name = "单位", width = 10)
    //@NotBlank(message = "单位不能为空")
    @Length(max = 10, message = "单位长度不能大于10")
    private String unit;
    /** 数量 */
    @Excel(name = "数量", width = 12, numFormat = "#.##")
    //@NotNull(message = "数量不能为空")
    @Pattern(regexp = "^(([1-9]{1}\\d{0,9})|([0]{1}))(\\.(\\d){0,2})?$", message = "数量只能输入1至10位和1至2位小数")
    private String number;

    /** 车间 */
    @Excel(name = "车间", width = 20)
    @Length(max = 50, message = "车间长度不能大于50")
    private String plant;

    @Excel(name = "车间计划员备注", width = 60)
    @Length(max = 255, message = "车间计划员备注长度不能大于255")
    private String workshopPlannerRemark;

    /** 预留单号 */
    @Excel(name = "预留单号", width = 20)
    @Length(max = 30, message = "预留单号长度不能大于30")
    private String reservedNumber;

    /** 采购申请号 */
    @Excel(name = "采购申请号", width = 20)
    @Length(max = 30, message = "采购申请号长度不能大于30")
    private String purchaseAply;

    /** 询价书名称 */
    @Excel(name = "询价书名称", width = 20)
    //@NotBlank(message = "询价名称不能为空")
    @Length(max = 50, message = "询价书名称长度不能大于50")
    private String enquiryName;


    @Excel(name = "供应部计划岗位备注", width = 60)
    @Length(max = 255, message = "供应部计划岗位备注长度不能大于255")
    private String supplyPlanPostRemark;

    @Excel(name = "采购订单号", width = 30)
    @Length(max = 50, message = "采购订单号长度不能大于50")
    private String purchaseOrderNo;

    @Excel(name = "ERP订单号", width = 30)
    @Length(max = 50, message = "ERP订单号长度不能大于50")
    private String erpOrderNo;

    @Excel(name = "合同号", width = 30)
    @Length(max = 50, message = "合同号长度不能大于50")
    private String contractNo;

    /** 备注 */
    @Excel(name = "备注", width = 60)
    @Length(max = 255, message = "备注信息长度不能大于30")
    private String remark;

    @Excel(name = "库存凭证号", width = 30)
    @Length(max = 50, message = "库存凭证号长度不能大于50")
    private String stockVoucherNo;

    @Excel(name = "入库时间", width = 20, importFormat = "yyyy-MM-dd")
    @Pattern(regexp = "\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}", message = "入库时间格式不正确！必须是[年-月-日]，示例：2020-03-01")
    private String inStockDate;

    @Excel(name = "出库时间", width = 20, importFormat = "yyyy-MM-dd")
    @Pattern(regexp = "\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}", message = "出库时间格式不正确！必须是[年-月-日]，示例：2020-03-01")
    private String outStockDate;

    /** 流程ID */
    private String processId;

    private String instId;

    private boolean hasInst;
    private boolean disabled;

    public MaterialProcess() {
    }

    public MaterialProcess(String materialNo, String purchaseAply) {
        this.materialNo = materialNo;
        this.purchaseAply = purchaseAply;
    }

    @Excel(name = "失败原因", width = 80)
    private String errorMsg;

    public boolean isHasInst() {
        return this.instId != null;
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

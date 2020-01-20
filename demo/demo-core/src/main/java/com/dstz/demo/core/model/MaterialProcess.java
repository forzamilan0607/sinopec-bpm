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

    /** 询价名称 */
    @Excel(name = "询价名称", width = 20)
    //@NotBlank(message = "询价名称不能为空")
    @Length(max = 50, message = "询价名称长度不能大于50")
    private String enquiryName;
    /** 采购申请 */
    @Excel(name = "采购申请", width = 20)
    //@NotBlank(message = "采购申请编号不能为空")
    @Length(max = 30, message = "采购申请编号长度不能大于30")
    private String purchaseAply;

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

    /** 物料编码 */
    @Excel(name = "ERP物料编码", width = 20)
    @Length(max = 50, message = "ERP物料编码长度不能大于50")
    private String erpMaterialNo;
    /** 物料描述 */
    @Excel(name = "ERP物料描述", width = 35)
    @Length(max = 255, message = "ERP物料描述长度不能大于255")
    private String erpMaterialDesc;

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
    //@NotBlank(message = "车间不能为空")
    @Length(max = 50, message = "车间长度不能大于50")
    private String plant;
    /** 预留单号 */
    @Excel(name = "预留单号", width = 20)
    //@NotBlank(message = "预留单号不能为空")
    @Length(max = 30, message = "预留单号长度不能大于30")
    private String reservedNumber;
    /** 备注 */
    @Excel(name = "备注", width = 60)
    @Length(max = 255, message = "备注信息长度不能大于30")
    private String remark;
    /** 预留 */
    @Excel(name = "预留", width = 20)
    @Length(max = 30, message = "预留长度不能大于30")
    private String reserved;
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

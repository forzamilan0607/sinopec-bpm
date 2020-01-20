/** 创建采购计划历史纪录表 **/
CREATE TABLE `t_purchase_plan_his_rec` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `material_process_id` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '物料流程ID',
  `content` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '内容',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='采购计划历史记录表';
-- 物料流程表新增列和索引
alter table t_process_material add column erp_material_no varchar(50) DEFAULT null comment 'ERP物料编码';
alter table t_process_material add column erp_material_desc varchar(255) DEFAULT null comment 'ERP物料描述';
alter table t_process_material add index idx_material_no ('material_no');
package com.sunac.entity;

import com.sunac.domain.CommonDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/2 11:59 上午
 * @Version 1.0
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EsInfoObject extends CommonDomain {
    private String fld_batch;	//varchar(100)	批次名称 es_info_object
    private String fld_building;	//varchar(100)	楼栋名称 es_info_object
    private String fld_cell;	//varchar(100)	单元名称 es_info_object
    private BigDecimal fld_charged_area;	//decimal(18,2)	收费面积 es_info_object
    private Integer fld_status; //as fld_obj_status	int	资源基本状态 (0未售空置 1已售 2已接房 3已入住 4已售空置,99 关联后null值替换为99)es_info_object.fld_status
    private String fld_start_fee_date;	//string	起费日期
    private Integer fld_order; // int	资源总表es_info_object.fld_order，排序，未关联取 -1
    private String fld_owner_fee_date;	//date	业主起费日期
    private String fld_class_guid;
}

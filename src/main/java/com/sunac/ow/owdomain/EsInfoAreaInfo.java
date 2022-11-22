package com.sunac.ow.owdomain;

import com.sunac.domain.CommonDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description: TODO
 * @Author xiyang
 * @Date 2022/10/2 11:55 上午
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@AllArgsConstructor
public class EsInfoAreaInfo extends CommonDomain {
    private String fld_dq;	//varchar(100)	大区
    private String fld_ywdy;	//varchar(100)	业务单元（公司）
    private String fld_xm;	//varchar(100)	项目
    private String fld_company;	//varchar(50)	城市公司（账套同步，报表专用）
    private String fld_confirm_date;	//varchar(20)	确权日期
    private String fld_fee_type;	//varchar(50)	收费方式：酬金制、包干制
    private String fld_yt;	//varchar(50)	围合主业态

}

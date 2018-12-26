package com.sensoro.smartcity.model;

import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.util.ArrayList;
import java.util.List;

public class ContractInfoModel {
    //1 是企业合同 2 是个人合同
    public Integer contractType;
    //业主姓名，法定代表人
    public String customerName;
    //甲方名称,企业名称
    public String customerEnterpriseName;

    public String idCardNumber;

    public String customerAddress;

    public String customerPhone;

    public String placeType;

    public List<ContractsTemplateInfo> devicesList;
    //续费周期
    public Integer periodAge;
    //服务年限
    public Integer serverAge;
    //第一次缴费年限
    public Integer firstAge;
    //统一社会信用代码
    public String enterpriseCardId;
}

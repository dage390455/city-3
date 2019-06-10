package com.sensoro.common.constant;

/**
 * 合同交易状态常量
 */
public interface ContractOrderInfo {
    String SUCCESS = "SUCCESS";//支付成功
    String REFUND = "REFUND";//转入退款
    String NOTPAY = "NOTPAY";//未支付
    String CLOSED = "CLOSED";//已关闭
    String  REVOKED = "REVOKED";//已撤销（刷卡支付）
    String  USERPAYING = "USERPAYING";//用户支付中
    String  PAYERROR = "PAYERROR";//支付失败(其他原因，如银行返回失败)
}

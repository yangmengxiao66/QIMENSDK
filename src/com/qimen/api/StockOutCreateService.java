package com.qimen.api;

import com.bean.Constants;
import com.qimen.api.request.StockoutCreateRequest;
import com.qimen.api.response.StockoutCreateResponse;
import com.taobao.api.ApiException;
import com.util.JDBCUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description :出库单创建接口 ERP将新增的出库单信息传到奇门
 * @Reference :
 * @Author : zhang.bw
 * @CreateDate : 2019-07-05 10:00
 * @Modify:
 */
public class StockOutCreateService {

    /**
     * 出库单创建接口
     * 查找[业务_出库通知  WMS_OUT_NOTICES]表中[接收状态 RECIVESTATUS]为80或90的记录：
     * 如果[单据类型]=采购退货出库、调拨出库，则：将这些记录调用奇门接口传入奇门，见附件[奇门白皮书V2.6（2016-06-06）.docx]，对应接口地址：taobao.qimen.stockout.create。
     * 如果[单据类型]=销售出库，则：将这些记录调用奇门接口传入奇门，见附件[奇门白皮书V2.6（2016-06-06）.docx]，对应接口地址：taobao.qimen.deliveryorder.create。
     * 如果传入成功，则更新[业务_出库通知  WMS_OUT_NOTICES]的[接收状态 RECIVESTATUS]为99；
     * 如果传入失败，则更新[业务_出库通知  WMS_OUT_NOTICES]的[接收状态 RECIVESTATUS]为90，并更新错误信息到字段[接收错误原因  ERRMSG]上。
     */
    public String stockOutCreateService(Constants constants) throws ClassNotFoundException, SQLException, ApiException {
        String result = "";
        DeliveryOrderCreateService deliveryOrderCreateService = new DeliveryOrderCreateService();
        JDBCUtil util = new JDBCUtil(constants.getDbUrl(), constants.getDbUserName(), constants.getDbPassWord());
        //select id, BILLTYPE, DOCNO, WMS_STORECODE, to_char(CREATIONDATE, 'YYYY-MM-DD HH:MM:SS'), RECEIVER_NAME, RECEIVER_POSTAL, RECEIVER_PHONE, RECEIVER_MOBILE, RECEIVER_ADDRESS, DESCRIPTION, RECEIVER_PROVINCE, RECEIVER_CITY, RECEIVER_DISTRICT  from WMS_OUT_NOTICES where RECIVESTATUS <> 99
        List<String[]> list = util.search("select id, BILLTYPE, DOCNO, WMS_STORECODE, to_char(CREATIONDATE, 'YYYY-MM-DD HH:MM:SS'), RECEIVER_NAME, RECEIVER_POSTAL, RECEIVER_PHONE, RECEIVER_MOBILE, RECEIVER_ADDRESS, DESCRIPTION, RECEIVER_PROVINCE, RECEIVER_CITY, RECEIVER_DISTRICT  from WMS_OUT_NOTICES where RECIVESTATUS <> 99", null);
        String[] list2 = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                list2 = list.get(i);
                QimenClient client = new DefaultQimenClient(constants.getUrl(), constants.getAppKey(), constants.getSecret());
                StockoutCreateRequest req = new StockoutCreateRequest();
                req.setCustomerId(constants.getCustomerId());
                req.setVersion(constants.getVersion());
                StockoutCreateRequest.DeliveryOrder obj1 = new StockoutCreateRequest.DeliveryOrder();
                //出库单号
                obj1.setDeliveryOrderCode(String.valueOf(list2[2]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[2]));
                String BILLTYPE = String.valueOf(list2[1]);
                if (BILLTYPE.equalsIgnoreCase("SA")) {
                    //销售出库 JYCK
                    /*deliveryOrderCreateService.deliveryOrderCreate(constants);
                    continue;*/
                    //2019-07-25 zhang.bw 销售出库也使用出库单创建接口，不再使用之前约定的taobao.qimen.deliveryorder.create
                    obj1.setOrderType("PTCK");//普通出库
                } else if (BILLTYPE.equalsIgnoreCase("TF")) {
                    obj1.setOrderType("DBCK");//调拨出库
                } else if (BILLTYPE.equalsIgnoreCase("PR")) {
                    obj1.setOrderType("CGTH");//采购退货出库
                } else {
                    obj1.setOrderType("");
                }
                //仓库编码
                obj1.setWarehouseCode(String.valueOf(list2[3]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[3]));
                //出库单创建时间
                obj1.setCreateTime(String.valueOf(list2[4]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[4]));
                StockoutCreateRequest.ReceiverInfo obj15 = new StockoutCreateRequest.ReceiverInfo();
                //收件人姓名
                obj15.setName(String.valueOf(list2[5]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[5]));
                //邮编
                obj15.setZipCode(String.valueOf(list2[6]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[6]));
                //固定电话
                obj15.setTel(String.valueOf(list2[7]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[7]));
                //移动电话
                obj15.setMobile(String.valueOf(list2[8]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[8]));
                //详细地址
                obj15.setDetailAddress(String.valueOf(list2[9]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[9]));
                //省份
                obj15.setProvince(String.valueOf(list2[11]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[11]));
                //城市
                obj15.setCity(String.valueOf(list2[12]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[12]));
                //区域
                obj15.setArea(String.valueOf(list2[13]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[13]));
                obj1.setReceiverInfo(obj15);
                //备注
                obj1.setRemark(String.valueOf(list2[10]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[10]));
                //select PRODUCTALIAS_NO,QTY from WMS_OUT_NOTICESITEM where DOCNO=
                List<String[]> itemList = util.search("select PRODUCTALIAS_NO,QTY from WMS_OUT_NOTICESITEM where DOCNO= '" + String.valueOf(list2[2]) + "'", null);
                if (itemList.size() > 0 && itemList != null) {
                    String[] item = null;
                    List<StockoutCreateRequest.OrderLine> obj20 = new ArrayList<StockoutCreateRequest.OrderLine>();
                    for (int j = 0; j < itemList.size(); j++) {
                        item = itemList.get(j);
                        StockoutCreateRequest.OrderLine orderLine = new StockoutCreateRequest.OrderLine();
                        orderLine.setOwnerCode(util.getOwnerCode());
                        orderLine.setItemCode(String.valueOf(item[0]).equalsIgnoreCase("null") ? "" : String.valueOf(item[0]));
                        orderLine.setPlanQty(String.valueOf(item[1]).equalsIgnoreCase("null") ? "0" : String.valueOf(item[1]));
                        obj20.add(orderLine);
                    }
                    req.setDeliveryOrder(obj1);
                    req.setOrderLines(obj20);
                    StockoutCreateResponse rsp = client.execute(req);
                    System.out.println(rsp.getBody());
                    result = "----出库通知接口返回-----code：" + rsp.getCode() + "---msg:" + rsp.getMessage() + "----flag:" + rsp.getFlag() + "----deliveryOrderId:" + rsp.getDeliveryOrderId();
                    String xml = rsp.getMessage();
                    if (rsp.getFlag().equalsIgnoreCase("success")) {
                        util.updateSql("", "WMS_OUT_NOTICES", 99, new BigDecimal(list2[0]), "");
                    } else {
                        String msg = rsp.getMessage().replace("'", "\"");
                        util.updateSql(msg, "WMS_OUT_NOTICES", 90, new BigDecimal(list2[0]), "");
                    }
                } else {
                    result = "--------出库单没有明细不推送------over";
                }
            }
        } else {
            result = "-----出库通知没有要同步的数据----------";
        }
        return result;
    }
}

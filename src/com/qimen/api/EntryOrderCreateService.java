package com.qimen.api;

import com.bean.Constants;
import com.qimen.api.request.EntryorderCreateRequest;
import com.qimen.api.response.EntryorderCreateResponse;
import com.taobao.api.ApiException;
import com.util.JDBCUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description : 入库通知  定时任务调用入库单同步到奇门
 * @Reference :
 * @Author : yihang.lv
 * @CreateDate : 2019-06-28 14:14
 * @Modify:
 **/
public class EntryOrderCreateService {

    public String entryOrderCreate(Constants constants) throws SQLException, ClassNotFoundException, ApiException {
        String result = "";
        JDBCUtil util = new JDBCUtil(constants.getDbUrl(), constants.getDbUserName(), constants.getDbPassWord());
        String ownerCode = util.getOwnerCode();
        List<String[]> list = util.search("select id,BILLTYPE,DOCNO,WMS_STORECODE,DESCRIPTION,WMS_STORECODE,ORIGCODE,PREDATEIN from WMS_IN_NOTICES where RECIVESTATUS in(80,90)", null);
        if (list != null && list.size() > 0) {
            QimenClient client = new DefaultQimenClient(constants.getUrl(), constants.getAppKey(), constants.getSecret());
            EntryorderCreateRequest req = null;
            String[] list2 = null;
            for (int i = 0; i < list.size(); i++) {
                list2 = list.get(i);
                req = new EntryorderCreateRequest();
                req.setCustomerId(constants.getCustomerId());
                req.setVersion(constants.getVersion());
                EntryorderCreateRequest.EntryOrder obj1 = new EntryorderCreateRequest.EntryOrder();
                String BILLTYPE = String.valueOf(list2[1]);
                if (BILLTYPE.equalsIgnoreCase("PU")) {
                    obj1.setOrderType("CGRK");
                }else if (BILLTYPE.equalsIgnoreCase("SR")) {
                    obj1.setOrderType("QTRK");
                }else if (BILLTYPE.equalsIgnoreCase("TF")) {
                    obj1.setOrderType("DBRK");
                }else {
                    obj1.setOrderType("");
                }
                obj1.setEntryOrderCode(String.valueOf(list2[2]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[2]));
                obj1.setWarehouseCode(String.valueOf(list2[5]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[5]));
                obj1.setOwnerCode(ownerCode);
                obj1.setRemark(String.valueOf(list2[4]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[4]));
                obj1.setSupplierCode(String.valueOf(list2[6]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[6]));
                String date = String.valueOf(list2[7]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[7]);
                obj1.setExpectStartTime(util.paseDate(date));
                obj1.setPurchaseOrderCode(String.valueOf(list2[2]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[2]));
                req.setEntryOrder(obj1);
                List<String[]> itemList = util.search("select PRODUCTALIAS_NO,QTY from WMS_IN_NOTICESITEM where DOCNO='" + String.valueOf(list2[2]) + "'", null);
                String[] item = null;
                if (itemList != null && itemList.size() > 0) {
                    List<EntryorderCreateRequest.OrderLine> obj5 = new ArrayList<EntryorderCreateRequest.OrderLine>();
                    for (int j = 0; j < itemList.size(); j++) {
                        item = itemList.get(j);
                        EntryorderCreateRequest.OrderLine orderLine = new EntryorderCreateRequest.OrderLine();
                        orderLine.setOwnerCode(ownerCode);
                        orderLine.setItemCode(String.valueOf(item[0]).equalsIgnoreCase("null") ? "" : String.valueOf(item[0]));
                        orderLine.setPlanQty(new BigDecimal(item[1]).longValue());
                        obj5.add(orderLine);
                    }
                    req.setOrderLines(obj5);
                    EntryorderCreateResponse rsp;
                    rsp = client.execute(req);
                    String xml = rsp.getMessage();
                    result = "----入库通知接口返回-----code：" + rsp.getCode() + "---msg:" + rsp.getMessage() + "----flag:" + rsp.getFlag() + "----itemId:" + rsp.getEntryOrderId();
                    if (rsp.getFlag().equalsIgnoreCase("success")) {
                        util.updateSql("", "WMS_IN_NOTICES", 99, new BigDecimal(list2[0]), rsp.getEntryOrderId());
                    } else {
                        result = rsp.getMessage().replace("'", "\"");
                        util.updateSql(result, "WMS_IN_NOTICES", 90, new BigDecimal(list2[0]), "");
                    }
                }
            }
        }
        return result;
    }
}

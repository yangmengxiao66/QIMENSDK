package com.qimen.api;

import com.bean.Constants;
import com.bean.SPResult;
import com.qimen.api.request.OrderCancelRequest;
import com.qimen.api.response.OrderCancelResponse;
import com.taobao.api.ApiException;
import com.util.JDBCUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderCancelService {

    public String orderCancel(Constants constants, Long orderId) throws SQLException, ClassNotFoundException, ApiException {
        String result = "";
        JDBCUtil util = new JDBCUtil(constants.getDbUrl(), constants.getDbUserName(), constants.getDbPassWord());
        //select DOCNO,BILLTYPE,CANCEL_REASON,ORDERID,WMS_STORECODE from M_WMSINOUT_CANCEL where id=
        String sql = "select m.drp_docno,m.billtype,m.cancel_reason from M_WMSINOUT_CANCEL m where id=" + Integer.parseInt((orderId.toString()));
        List<String[]> list = util.search(sql, null);
        String[] list2 = null;
        //List list=new test().getList();
        if (list != null && list.size() > 0) {
            list2 = list.get(0);
            QimenClient client = new DefaultQimenClient(constants.getUrl(), constants.getAppKey(), constants.getSecret());
            OrderCancelRequest req = new OrderCancelRequest();
            req.setWarehouseCode(constants.getWareHouseCode());
            req.setOwnerCode(util.getOwnerCode());
            req.setCustomerId(constants.getCustomerId());
            req.setOrderCode(String.valueOf(list2[0]));
            String type = String.valueOf(list2[1]);
            String orderType = "";
            /**
             * 采购入库        PU
             调拨入            TF
             销售退货入     SR
             销售出 SA
             调拨出 TF
             采购退货出 PR
             *
             *
             *
             * */
            List<String[]> typeList = null;
            if (type.equalsIgnoreCase("IN")) {
                typeList = util.search("select billtype,WMS_STORECODE from WMS_IN_NOTICES where docno = '"+String.valueOf(list2[0])+"'", null);
            }
            if (type.equalsIgnoreCase("OUT")) {
                typeList = util.search("select billtype,WMS_STORECODE from WMS_OUT_NOTICES where docno = '"+String.valueOf(list2[0])+"'", null);
//                orderType = "JYCK";
            }
            orderType = typeList.get(0)[0];
            if (orderType.equalsIgnoreCase("PU")) {
                req.setOrderType("CGRK");
            }else if (orderType.equalsIgnoreCase("SR")) {
                req.setOrderType("QTRK");
            }else if (orderType.equalsIgnoreCase("TF")) {
                req.setOrderType("DBCK");
            }else if (orderType.equalsIgnoreCase("SA")) {
                req.setOrderType("PTCK");//普通出库
            }else if (orderType.equalsIgnoreCase("PR")) {
                req.setOrderType("CGTH");//采购退货出库
            } else {
                req.setOrderType("");
            }
            req.setWarehouseCode(typeList.get(0)[1]);
            req.setCancelReason(String.valueOf(list2[2]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[2]));
            OrderCancelResponse rsp = client.execute(req);
            System.out.println(rsp.getBody());
            if (rsp.getFlag().equalsIgnoreCase("success")) {
                List p = new ArrayList();
                p.add(Integer.parseInt(orderId.toString()));
                result = util.executeStoredProcedure("M_WMSINOUT_CANCEL_SUBMIT", p, true);
            } else {
                result = rsp.getMessage();
            }
        } else {
            result = "订单不存在！";
        }
        return result;
    }
}

package com.qimen.api;

import com.bean.Constants;
import com.util.JDBCUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * @Description :出库单确认接口
 * @Reference :
 * @Author : yihang.lv
 * @CreateDate : 2019-07-01 9:29
 * @Modify:
 **/
public class DeliveryOrderConfirmService {

    JDBCUtil util = null;
    public String deliveryOrderConfirmService(Constants constants, HttpServletRequest req, HttpServletResponse resp) throws IOException, DocumentException, SQLException, ClassNotFoundException {
        String result = "";
        util = new JDBCUtil(constants.getDbUrl(), constants.getDbUserName(), constants.getDbPassWord());
        resp.setCharacterEncoding("UTF-8");
        Map<String, String[]> params = req.getParameterMap();
        String method = util.getMethod(req);
        //taobao.qimen.deliveryorder.confirm
        if ("stockout.confirm".equalsIgnoreCase(method)||("deliveryorder.confirm".equalsIgnoreCase(method))) {
            result = stockOutConfirmAction(req, resp);
        } else {
            util.returnJson(req, resp, "请求方法不存在或不正确", 2, "failure");
            result = "请求方法不存在或不正确";
        }
        return result;
    }

    private String stockOutConfirmAction(HttpServletRequest req,
                                       HttpServletResponse resp) throws DocumentException, SQLException, ClassNotFoundException, IOException {
        String data = getParam(req);
        String result = "";
        int WMS_RESULT_OUT_ID = 0;
        String deliveryOrderCode = "";
        boolean flag = false;
        Document document = DocumentHelper.parseText(data);
        Element rootElement = document.getRootElement();
        System.out.println(("----根节点---" + rootElement.getName()));
        Element deliveryOrder = rootElement.element("deliveryOrder");
        //单据编号
        deliveryOrderCode = deliveryOrder.elementText("deliveryOrderCode");
        String confirm = util.isConfirm("OUT",deliveryOrderCode);
        if("".equals(confirm)) {
            String outBizCode = deliveryOrder.elementText("outBizCode");
            String status = deliveryOrder.elementText("status");

            String sql = " insert into WMS_RESULT_OUT(ID,DOCNO,WMS_DOCNO,DATEOUT,RECIVEDATE,CREATIONDATE,MODIFIEDDATE) "
                    + "values(?,?,?,to_number(to_char(SYSDATE, 'YYYYMMDD')),SYSDATE,SYSDATE,SYSDATE)";

            int executeUpdate = util.execute(
                    sql,
                    new Object[]{
                            util.getSequence(
                                    "WMS_RESULT_OUT"), deliveryOrderCode, outBizCode,}
            );
            if (executeUpdate == 1) {
                WMS_RESULT_OUT_ID = getId(deliveryOrderCode);
            }
            flag = true;
            System.out.println("主表执行sql返回的字段--------" + executeUpdate);


            System.out.println("-----deliveryOrderCode----" + deliveryOrderCode);
            Element orderLines = rootElement.element("orderLines");
            Iterator iter = orderLines.elementIterator("orderLine");
            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                String itemName = recordEle.elementTextTrim("itemCode");
                String actualQty = recordEle.elementTextTrim("actualQty");
                System.out.println("出库回传参数：PRODUCTALIAS_NO=" + itemName + ",QTYOUT=" + actualQty);
                if (flag) {
                    flag = insertItem(deliveryOrderCode, WMS_RESULT_OUT_ID, flag, itemName, actualQty);
                }
            }
            if (flag) {
                result = util.returnJson(req, resp, "成功", 0, "success");
            } else {
                result = util.returnJson(req, resp, "明细写入数据库失败", 4, "failure");
            }
        }else {
            //单据已存在的情况下需要返回成功，以便调用方做处理
            result = util.returnJson(req, resp, "成功", 0, "success");
        }
        return result;
    }

    //将明细插入数据库
    private boolean insertItem(String deliveryOrderCode, int WMS_RESULT_OUT_ID, boolean flag,
                               String itemName, String quantity) throws SQLException, ClassNotFoundException {
        if (flag) {
            String sql2 = "insert into WMS_RESULT_OUTITEM (ID,AD_CLIENT_ID,AD_ORG_ID,DOCNO,WMS_RESULT_OUT_ID,PRODUCTALIAS_NO,QTYOUT,CREATIONDATE,MODIFIEDDATE)"
                    + "values(?,37,27,?,?,?,?,sysdate,sysdate)";
            int executeUpdate = util.execute(sql2, new Object[]{util.getSequence("WMS_RESULT_OUTITEM"), deliveryOrderCode, WMS_RESULT_OUT_ID, itemName, quantity});
            flag = true;
        }
        return flag;
    }

    public String getParam(HttpServletRequest req) throws IOException {
        StringBuffer data = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
        while (null != (line = reader.readLine()))
            data.append(line);
        return data.toString();
    }


    public int getId(String code) throws SQLException, ClassNotFoundException {
        System.out.println("------进入查询重复方法------");
        int id = 0;
        List<String[]> doQueryList2 = util.search("select id from WMS_RESULT_OUT where DOCNO='" + code + "'", null);
        if (doQueryList2 != null && doQueryList2.size() > 0) {
            id = new BigDecimal(doQueryList2.get(0)[0]).intValue();
        }
        return id;
    }
}

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
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * @Description :库存盘点单接口 提供奇门调用
 * @Reference :
 * @Author : yihang.lv
 * @CreateDate : 2019-07-05 13:46
 * @Modify:
 **/
public class InventoryReportService {

    private static SimpleDateFormat secFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyyMMdd");

    //新增库存盘点sql
    private static String INSERT_WMS_ADJ_OUT_SQL = "insert into WMS_ADJ_OUT(ID,AD_CLIENT_ID,AD_ORG_ID,DOCNO," +
            "BILL_TYPE,BILLDATE,ORIGNAME,WMS_REASON,OWNERID,MODIFIERID,CREATIONDATE,MODIFIEDDATE,ISACTIVE) "
            + "values(?,37,27,?,?,?,?,?,null,null,sysdate,sysdate,'Y')";
    //新增库存盘点明细sql
    private static String INSERT_WMS_ADJ_OUTITEM_SQL = "insert into WMS_ADJ_OUTITEM(ID,AD_CLIENT_ID,AD_ORG_ID,WMS_ADJ_OUT_ID,DOCNO," +
            "PRODUCTALIAS_NO,QTYBOOK,QTYCOUNT,QTYDIFF,WMSITEM_REASON,OWNERID,MODIFIERID,CREATIONDATE,MODIFIEDDATE,ISACTIVE) "
            + "values(?,37,27,?,?,?,null,null,?,null,null,null,sysdate,sysdate,'Y')";

    JDBCUtil util = null;
    public String inventoryReportService(Constants constants, HttpServletRequest req, HttpServletResponse resp) throws IOException, DocumentException, SQLException, ClassNotFoundException, ParseException {
        String result = "";
        util = new JDBCUtil(constants.getDbUrl(), constants.getDbUserName(), constants.getDbPassWord());
        processInventoryReport(req,resp);
        return result;
    }

    //处理盘点单逻辑
    private void processInventoryReport(HttpServletRequest req,
                                        HttpServletResponse resp) throws SQLException, ClassNotFoundException, IOException, DocumentException, ParseException {
            String data = util.getParam(req);
            Document document = null;
            document = DocumentHelper.parseText(data);
            Element rootElement = document.getRootElement();
            System.out.println(("----根节点---" + rootElement.getName()));
            String checkOrderCode = rootElement.elementText("checkOrderCode");
            String adjustType = rootElement.elementText("adjustType");
            String checkTime = rootElement.elementText("checkTime");
            //在我们自己库里只存年月日
            Date parseDate = secFormatter.parse(checkTime);
            String formatDate = dayFormatter.format(parseDate);
            String warehouseCode = rootElement.elementText("warehouseCode");
            String remark = rootElement.elementText("remark")==null?"":rootElement.elementText("remark");

            Element items = rootElement.element("items");
            Iterator item = items.elementIterator();
            //写库存盘点（WMS_ADJ_OUT）
            long wmsAdjOutId = util.getSequence(
                    "WMS_ADJ_OUT");
            int executeAdjOutSql = util.execute(
                    INSERT_WMS_ADJ_OUT_SQL,
                    new Object[]{wmsAdjOutId, checkOrderCode, adjustType, formatDate
                            , warehouseCode, remark}
            );

            if (executeAdjOutSql != 1) {
                util.returnJson(req, resp, "存库盘点单失败", 3, "failure");
                System.out.println("存库盘点单新增失败,盘点单号" + checkOrderCode);
                return;
            }

            while (item.hasNext()) {
                Element recordEle = (Element) item.next();
                String itemCode = recordEle.elementTextTrim("itemCode");
                System.out.println("-------itemCode------" + itemCode);
                String quantity = recordEle.elementTextTrim("quantity");
                System.out.println("------quantity------" + quantity);
                //写库存盘点明细（WMS_ADJ_OUTITEM）
                int executeAdjOutItemSql = util.execute(
                        INSERT_WMS_ADJ_OUTITEM_SQL,
                        new Object[]{util.getSequence(
                                "WMS_ADJ_OUTITEM"), wmsAdjOutId, checkOrderCode, itemCode, quantity}
                );
                if (executeAdjOutItemSql != 1) {
                    util.returnJson(req, resp, "库存盘点明细单失败", 3, "failure");
                    System.out.println("库存盘点明细单失败,盘点单号" + checkOrderCode + ",条码" + itemCode);
                    return;
                }
            }
            util.returnJson(req, resp, "成功", 0, "success");
    }
}

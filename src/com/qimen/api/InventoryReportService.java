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
 * @Description :����̵㵥�ӿ� �ṩ���ŵ���
 * @Reference :
 * @Author : yihang.lv
 * @CreateDate : 2019-07-05 13:46
 * @Modify:
 **/
public class InventoryReportService {

    private static SimpleDateFormat secFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyyMMdd");

    //��������̵�sql
    private static String INSERT_WMS_ADJ_OUT_SQL = "insert into WMS_ADJ_OUT(ID,AD_CLIENT_ID,AD_ORG_ID,DOCNO," +
            "BILL_TYPE,BILLDATE,ORIGNAME,WMS_REASON,OWNERID,MODIFIERID,CREATIONDATE,MODIFIEDDATE,ISACTIVE) "
            + "values(?,37,27,?,?,?,?,?,null,null,sysdate,sysdate,'Y')";
    //��������̵���ϸsql
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

    //�����̵㵥�߼�
    private void processInventoryReport(HttpServletRequest req,
                                        HttpServletResponse resp) throws SQLException, ClassNotFoundException, IOException, DocumentException, ParseException {
            String data = util.getParam(req);
            Document document = null;
            document = DocumentHelper.parseText(data);
            Element rootElement = document.getRootElement();
            System.out.println(("----���ڵ�---" + rootElement.getName()));
            String checkOrderCode = rootElement.elementText("checkOrderCode");
            String adjustType = rootElement.elementText("adjustType");
            String checkTime = rootElement.elementText("checkTime");
            //�������Լ�����ֻ��������
            Date parseDate = secFormatter.parse(checkTime);
            String formatDate = dayFormatter.format(parseDate);
            String warehouseCode = rootElement.elementText("warehouseCode");
            String remark = rootElement.elementText("remark")==null?"":rootElement.elementText("remark");

            Element items = rootElement.element("items");
            Iterator item = items.elementIterator();
            //д����̵㣨WMS_ADJ_OUT��
            long wmsAdjOutId = util.getSequence(
                    "WMS_ADJ_OUT");
            int executeAdjOutSql = util.execute(
                    INSERT_WMS_ADJ_OUT_SQL,
                    new Object[]{wmsAdjOutId, checkOrderCode, adjustType, formatDate
                            , warehouseCode, remark}
            );

            if (executeAdjOutSql != 1) {
                util.returnJson(req, resp, "����̵㵥ʧ��", 3, "failure");
                System.out.println("����̵㵥����ʧ��,�̵㵥��" + checkOrderCode);
                return;
            }

            while (item.hasNext()) {
                Element recordEle = (Element) item.next();
                String itemCode = recordEle.elementTextTrim("itemCode");
                System.out.println("-------itemCode------" + itemCode);
                String quantity = recordEle.elementTextTrim("quantity");
                System.out.println("------quantity------" + quantity);
                //д����̵���ϸ��WMS_ADJ_OUTITEM��
                int executeAdjOutItemSql = util.execute(
                        INSERT_WMS_ADJ_OUTITEM_SQL,
                        new Object[]{util.getSequence(
                                "WMS_ADJ_OUTITEM"), wmsAdjOutId, checkOrderCode, itemCode, quantity}
                );
                if (executeAdjOutItemSql != 1) {
                    util.returnJson(req, resp, "����̵���ϸ��ʧ��", 3, "failure");
                    System.out.println("����̵���ϸ��ʧ��,�̵㵥��" + checkOrderCode + ",����" + itemCode);
                    return;
                }
            }
            util.returnJson(req, resp, "�ɹ�", 0, "success");
    }
}

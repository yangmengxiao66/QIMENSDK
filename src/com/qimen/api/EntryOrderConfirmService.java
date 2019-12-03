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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Description :入库单确认接口提供奇门调用
 * @Reference :
 * @Author : huwk  taobao.qimen.entryorder.confirm 接口
 * @CreateDate : 2019-07-05 13:22
 * @Modify:
 **/
public class EntryOrderConfirmService {

    JDBCUtil util = null;

    public String entryOrderConfirmService(Constants constants, HttpServletRequest req, HttpServletResponse resp) throws IOException, DocumentException, SQLException, ClassNotFoundException {
        String result = "";
        util = new JDBCUtil(constants.getDbUrl(), constants.getDbUserName(), constants.getDbPassWord());
        resp.setCharacterEncoding("UTF-8");
        //获取参数
        Map<String, String[]> params = req.getParameterMap();
        String method = util.getMethod(req);
        //taobao.qimen.entryorder.confirm
        if ("entryorder.confirm".equalsIgnoreCase(method)) {
            result = entryOrderAction(req, resp);
        } else {
            util.returnJson(req, resp, "请求方法不存在或不正确", 2, "failure");
            result = "请求方法不存在或不正确";
        }
        return result;
    }

    private String entryOrderAction(HttpServletRequest req,
                                  HttpServletResponse resp) throws IOException, DocumentException, SQLException, ClassNotFoundException {
        String data = util.getParam(req);
        String result = "";
        int WMS_RESULT_IN_ID = 0;
        boolean flag = false;
        try {
            Document document = DocumentHelper.parseText(data);
            Element rootElement = document.getRootElement();
            Element entryOrder = rootElement.element("entryOrder");
            // 入库单号
            String entryOrderCode = entryOrder.elementText("entryOrderCode");
            String confirm = util.isConfirm("IN",entryOrderCode);
            if("".equals(confirm)) {

                //仓储系统入库单ID
                String entryOrderId = entryOrder.elementText("outBizCode");
                //操作时间
                SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyyMMdd");

                /**
                 * DOCNO         入库单单据编号
                 * WMS_DOCNO     WMS单号
                 * DATEIN       入库日期
                 */
                String sql = "insert into WMS_RESULT_IN (ID,DOCNO,WMS_DOCNO,DATEIN,CREATIONDATE,MODIFIEDDATE)"
                        + "values(?,?,?,?,sysdate,sysdate)";
                int executeUpdate = util.execute(
                        sql,
                        new Object[]{util.getSequence("WMS_RESULT_IN"),
                                entryOrderCode, entryOrderId, simpleFormatter.format(new Date())}
                );
                if (executeUpdate == 1) {
                    WMS_RESULT_IN_ID = getId(entryOrderCode);
                }
                flag = true;
                System.out.println("主表执行sql返回的字段--------" + executeUpdate);

                Element orderLines = rootElement.element("orderLines");
                Iterator iter = orderLines.elementIterator("orderLine");
                while (iter.hasNext()) {
                    Element recordEle = (Element) iter.next();
                    String itemName = recordEle.elementTextTrim("itemCode");
                    String actualQty = recordEle.elementTextTrim("actualQty");
                    System.out.println("入库回传参数：PRODUCTALIAS_NO=" + itemName + ",QTYIN=" + actualQty);
                    if (flag) {
                        flag = insertItem(entryOrderCode, WMS_RESULT_IN_ID, flag, itemName, actualQty);
                    }
                }
                if (flag) {
                    result = util.returnJson(req, resp, "成功", 0, "success");
                } else {
                    result = util.returnJson(req, resp, "明细写入数据库失败", 4, "failure");
                }
            }else{
                //单据已存在的情况下需要返回成功，以便调用方做处理
                result = util.returnJson(req, resp, "成功", 0, "success");
            }
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
        return result;

    }

    //将明细插入数据库
    private boolean insertItem(String entryOrderCode, int WMS_RESULT_IN_ID, boolean flag,
                               String itemName, String quantity) throws SQLException, ClassNotFoundException {
        if (flag) {
            String sql2 = "insert into WMS_RESULT_INITEM (ID,DOCNO,PRODUCTALIAS_NO,QTYIN,WMS_RESULT_IN_ID,CREATIONDATE,MODIFIEDDATE)"
                    + "values(?,?,?,?,?,sysdate,sysdate)";
            System.out.println("进入写明细数据:******" + sql2);
            int executeUpdate = util.execute(
                    sql2,
                    new Object[]{
                            util.getSequence("WMS_RESULT_INITEM"), entryOrderCode, itemName, quantity,WMS_RESULT_IN_ID}
            );
            flag = executeUpdate > 0 ? true : false;
        }
        return flag;
    }


    public int getId(String code) throws SQLException, ClassNotFoundException {
        System.out.println("------进入查询重复方法------");
        int id = 0;
        List<String[]> doQueryList2 = util.search("select id from WMS_RESULT_IN where DOCNO= ?", new Object[]{code});
        if (doQueryList2 != null && doQueryList2.size() > 0) {
            id = Long.valueOf(doQueryList2.get(0)[0].toString()).intValue();
        }
        System.out.println("-----是否重复-------" + id);
        return id;
    }
}

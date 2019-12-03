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
 * @Description :���ⵥ�����ӿ� ERP�������ĳ��ⵥ��Ϣ��������
 * @Reference :
 * @Author : zhang.bw
 * @CreateDate : 2019-07-05 10:00
 * @Modify:
 */
public class StockOutCreateService {

    /**
     * ���ⵥ�����ӿ�
     * ����[ҵ��_����֪ͨ  WMS_OUT_NOTICES]����[����״̬ RECIVESTATUS]Ϊ80��90�ļ�¼��
     * ���[��������]=�ɹ��˻����⡢�������⣬�򣺽���Щ��¼�������Žӿڴ������ţ�������[���Ű�Ƥ��V2.6��2016-06-06��.docx]����Ӧ�ӿڵ�ַ��taobao.qimen.stockout.create��
     * ���[��������]=���۳��⣬�򣺽���Щ��¼�������Žӿڴ������ţ�������[���Ű�Ƥ��V2.6��2016-06-06��.docx]����Ӧ�ӿڵ�ַ��taobao.qimen.deliveryorder.create��
     * �������ɹ��������[ҵ��_����֪ͨ  WMS_OUT_NOTICES]��[����״̬ RECIVESTATUS]Ϊ99��
     * �������ʧ�ܣ������[ҵ��_����֪ͨ  WMS_OUT_NOTICES]��[����״̬ RECIVESTATUS]Ϊ90�������´�����Ϣ���ֶ�[���մ���ԭ��  ERRMSG]�ϡ�
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
                //���ⵥ��
                obj1.setDeliveryOrderCode(String.valueOf(list2[2]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[2]));
                String BILLTYPE = String.valueOf(list2[1]);
                if (BILLTYPE.equalsIgnoreCase("SA")) {
                    //���۳��� JYCK
                    /*deliveryOrderCreateService.deliveryOrderCreate(constants);
                    continue;*/
                    //2019-07-25 zhang.bw ���۳���Ҳʹ�ó��ⵥ�����ӿڣ�����ʹ��֮ǰԼ����taobao.qimen.deliveryorder.create
                    obj1.setOrderType("PTCK");//��ͨ����
                } else if (BILLTYPE.equalsIgnoreCase("TF")) {
                    obj1.setOrderType("DBCK");//��������
                } else if (BILLTYPE.equalsIgnoreCase("PR")) {
                    obj1.setOrderType("CGTH");//�ɹ��˻�����
                } else {
                    obj1.setOrderType("");
                }
                //�ֿ����
                obj1.setWarehouseCode(String.valueOf(list2[3]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[3]));
                //���ⵥ����ʱ��
                obj1.setCreateTime(String.valueOf(list2[4]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[4]));
                StockoutCreateRequest.ReceiverInfo obj15 = new StockoutCreateRequest.ReceiverInfo();
                //�ռ�������
                obj15.setName(String.valueOf(list2[5]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[5]));
                //�ʱ�
                obj15.setZipCode(String.valueOf(list2[6]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[6]));
                //�̶��绰
                obj15.setTel(String.valueOf(list2[7]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[7]));
                //�ƶ��绰
                obj15.setMobile(String.valueOf(list2[8]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[8]));
                //��ϸ��ַ
                obj15.setDetailAddress(String.valueOf(list2[9]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[9]));
                //ʡ��
                obj15.setProvince(String.valueOf(list2[11]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[11]));
                //����
                obj15.setCity(String.valueOf(list2[12]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[12]));
                //����
                obj15.setArea(String.valueOf(list2[13]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[13]));
                obj1.setReceiverInfo(obj15);
                //��ע
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
                    result = "----����֪ͨ�ӿڷ���-----code��" + rsp.getCode() + "---msg:" + rsp.getMessage() + "----flag:" + rsp.getFlag() + "----deliveryOrderId:" + rsp.getDeliveryOrderId();
                    String xml = rsp.getMessage();
                    if (rsp.getFlag().equalsIgnoreCase("success")) {
                        util.updateSql("", "WMS_OUT_NOTICES", 99, new BigDecimal(list2[0]), "");
                    } else {
                        String msg = rsp.getMessage().replace("'", "\"");
                        util.updateSql(msg, "WMS_OUT_NOTICES", 90, new BigDecimal(list2[0]), "");
                    }
                } else {
                    result = "--------���ⵥû����ϸ������------over";
                }
            }
        } else {
            result = "-----����֪ͨû��Ҫͬ��������----------";
        }
        return result;
    }
}

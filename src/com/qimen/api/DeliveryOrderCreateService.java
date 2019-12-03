package com.qimen.api;

import com.bean.Constants;
import com.qimen.api.request.DeliveryorderCreateRequest;
import com.qimen.api.response.DeliveryorderCreateResponse;
import com.taobao.api.ApiException;
import com.util.JDBCUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname DeliveryOrderCreateService
 * @Description �����������ӿ�
 * @Date 2019/7/5 9:50
 * @Created by hu.wk
 * @API taobao.qimen.deliveryorder.create
 */
public class DeliveryOrderCreateService {

    public String deliveryOrderCreate(Constants constants) throws ApiException, SQLException, ClassNotFoundException {
        String result = "";
        JDBCUtil util = new JDBCUtil(constants.getDbUrl(), constants.getDbUserName(), constants.getDbPassWord());
        List<String[]> list = util.search("select id,to_char(CREATIONDATE,'YYYY-MM-DD HH:MM:SS'),DOCNO,WMS_STORECODE,RECEIVER_NAME,RECEIVER_PHONE,RECEIVER_MOBILE,RECEIVER_ADDRESS,RECEIVER_PROVINCE,RECEIVER_CITY,DESCRIPTION,LOGISTICSNAME,DESTCODE,SCHEDULEDATE,to_char(MODIFIEDDATE,'YYYY-MM-DD HH:MM:SS'),to_char(CREATIONDATE,'YYYY-MM-DD HH:MM:SS'),LOGISTICSNAME,FNAME,FMOBILE,FPROVINCE,FCITY,FAREA,FDETAILADDRESS,AREA,OwnerCode from WMS_OUT_NOTICES where RECIVESTATUS<>99", null);
        String[] list2 = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                list2 = list.get(i);
                QimenClient client = new DefaultQimenClient(constants.getUrl(), constants.getAppKey(), constants.getSecret());
                DeliveryorderCreateRequest req = new DeliveryorderCreateRequest();
                req.setCustomerId(constants.getCustomerId());
                req.setVersion(constants.getVersion());
                DeliveryorderCreateRequest.DeliveryOrder obj1 = new DeliveryorderCreateRequest.DeliveryOrder();
                //���ⵥ����ʱ��
                obj1.setCreateTime(String.valueOf(list2[1]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[1]));
                //���ⵥ��
                obj1.setDeliveryOrderCode(String.valueOf(list2[2]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[2]));
                //�ֿ����
                obj1.setWarehouseCode(constants.getWareHouseCode());
                //obj1.setWarehouseCode(String.valueOf(list2[3)).equalsIgnoreCase("null")?"":String.valueOf(list2[3)));
                //-------------------------�ջ�����Ϣ------------------------
                DeliveryorderCreateRequest.ReceiverInfo obj15 = new DeliveryorderCreateRequest.ReceiverInfo();
                //�ռ�������
                obj15.setName(String.valueOf(list2[4]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[4]));
                //�̶��绰
                obj15.setTel(String.valueOf(list2[5]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[5]));
                //�ƶ��绰
                obj15.setMobile(String.valueOf(list2[6]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[6]));
                //��ϸ��ַ
                obj15.setDetailAddress(String.valueOf(list2[7]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[7]));
                //ʡ��
                obj15.setProvince(String.valueOf(list2[8]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[8]));
                //����
                obj15.setCity(String.valueOf(list2[9]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[9]));
                //����--AREA
                obj15.setArea(String.valueOf(list2[23]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[23]));
                obj1.setReceiverInfo(obj15);
                //��ע
                obj1.setRemark(String.valueOf(list2[10]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[10]));
                //������˾����-------------LOGISTICSNAME
                obj1.setLogisticsName(String.valueOf(list2[11]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[11]));
                //��������-----DESTCODE
                obj1.setShopNick(String.valueOf(list2[12]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[12]));
                //Ԥ�Ʒ���ʱ��-----SCHEDULEDATE
                obj1.setScheduleDate(String.valueOf(list2[13]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[13]));
                //����(���)ʱ��(YYYY-MM-DD HH:MM:SS)-----------MODIFIEDDATE
                obj1.setOperateTime(String.valueOf(list2[14]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[14]));
                //ǰ̨����/���̶����Ĵ���ʱ��/�µ�ʱ��
                obj1.setPlaceOrderTime(String.valueOf(list2[15]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[15]));
                //��������-------��������
                obj1.setSellerMessage(String.valueOf(list2[16]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[16]));
                //������˾����
                obj1.setLogisticsCode("001");
                //���ⵥ����----�̶�Ϊһ����⣺JYCK
                obj1.setOrderType("JYCK");
                obj1.setBuyerMessage(String.valueOf(list2[10]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[10]));
                //--------------------------��������Ϣ--------------------
                DeliveryorderCreateRequest.SenderInfo obj3 = new DeliveryorderCreateRequest.SenderInfo();
                //����������
                obj3.setName(String.valueOf(list2[17]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[17]));
                //�������ƶ��绰
                obj3.setMobile(String.valueOf(list2[18]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[18]));
                //������ʡ��
                obj3.setProvince(String.valueOf(list2[19]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[19]));
                //����������
                obj3.setCity(String.valueOf(list2[20]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[20]));
                //����������
                obj3.setArea(String.valueOf(list2[21]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[21]));
                //��������ϸ��ַ
                obj3.setDetailAddress(String.valueOf(list2[22]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[22]));
                List<String[]> itemList = util.search("select PRODUCTALIAS_NO,QTY from WMS_OUT_NOTICESITEM where DOCNO= '" + String.valueOf(list2[2]) + "'", null);
                if (itemList.size() > 0 && itemList != null) {
                    String[] item = null;
                    List<DeliveryorderCreateRequest.OrderLine> obj20 = new ArrayList<DeliveryorderCreateRequest.OrderLine>();
                    for (int j = 0; j < itemList.size(); j++) {
                        item = itemList.get(j);
                        DeliveryorderCreateRequest.OrderLine orderLine = new DeliveryorderCreateRequest.OrderLine();
                        //��������
                        orderLine.setOwnerCode(String.valueOf(list2[24]).equalsIgnoreCase("null") ? "" : String.valueOf(list2[24]));
                        //��Ʒ����
                        orderLine.setItemCode(String.valueOf(item[0]).equalsIgnoreCase("null") ? "" : String.valueOf(item[0]));
                        //Ӧ����Ʒ����
                        orderLine.setPlanQty(String.valueOf(item[1]).equalsIgnoreCase("null") ? "0" : String.valueOf(item[1]));
                        //ʵ�ʳɽ��۸�
                        orderLine.setActualPrice("");
                        obj20.add(orderLine);
                    }
                    req.setDeliveryOrder(obj1);
                    req.setOrderLines(obj20);
                    DeliveryorderCreateResponse rsp = client.execute(req);
                    result = "----�����������ӿڷ���-----code��" + rsp.getCode() + "---msg:" + rsp.getMessage() + "----flag:" + rsp.getFlag() + "----itemId:" + rsp.getDeliveryOrderId();
                    System.out.println(rsp.getBody());
                    String xml = rsp.getMessage();
                    if (rsp.getFlag().equalsIgnoreCase("success")) {
                        util.updateSql("", "WMS_OUT_NOTICES", 99, new BigDecimal(list2[0]), rsp.getDeliveryOrderId());
                    } else {
                        String msg = rsp.getMessage().replace("'", "\"");
                        util.updateSql(msg, "WMS_OUT_NOTICES", 90, new BigDecimal(list2[0]), "");
                    }
                } else {
                    result = "--------������û����ϸ������------over";
                }
            }
        }
        return result;
    }
}

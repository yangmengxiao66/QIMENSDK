package com.qimen.api;

import com.bean.Constants;
import com.qimen.api.request.SingleitemSynchronizeRequest;
import com.qimen.api.request.SingleitemSynchronizeRequest.Item;
import com.qimen.api.response.SingleitemSynchronizeResponse;
import com.taobao.api.ApiException;
import com.util.JDBCUtil;
import com.util.SystemParam;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Description :��Ʒ��Ϣͬ���ӿ� ERP����������Ʒ��Ϣ��������
 * @Reference :
 * @Author : zhang.bw
 * @CreateDate : 2019-07-04 17:00
 * @Modify:YMX/2019-12-02
 */
public class SingleItemSynchronizeService {

    /**
     * ����[��Ʒ��Ϣ WMS_PRODUCTALIAS]����[����״̬ RECIVESTATUS]Ϊ80��90�ļ�¼
     * ����Щ��¼�������Žӿڴ�������
     * �������ɹ��������[��Ʒ��Ϣ WMS_PRODUCTALIAS]��[����״̬ RECIVESTATUS]Ϊ99��
     * �������ʧ�ܣ������[��Ʒ��Ϣ WMS_PRODUCTALIAS]��[����״̬ RECIVESTATUS]Ϊ90�������´�����Ϣ���ֶ�[���մ���ԭ�� ERRMSG]�ϡ�
     */
    public String doItemSynchronize(Constants constants) throws ApiException, SQLException, ClassNotFoundException {
        String result = "";
        SystemParam param=new SystemParam();
        JDBCUtil util = new JDBCUtil(constants.getDbUrl(), constants.getDbUserName(), constants.getDbPassWord());
        ResultSet list = util.search(param.getSingleItemSynchronizeService_sql(), null);
        QimenClient client = new DefaultQimenClient(constants.getUrl(), constants.getAppKey(), constants.getSecret());
        SingleitemSynchronizeRequest req = new SingleitemSynchronizeRequest();;
        while (list.next()){
            req.setCustomerId(constants.getCustomerId());
            String type = list.getString("actionType").equalsIgnoreCase("null") ? "" :list.getString("actionType");
            //��������(�������ͣ�add|update)/����
            if("update".equals(type)){
                    req.setActionType("update");
                } else{
                    req.setActionType("add");
                }
            //�ֿ����(ͳ��ͳ�������ERPָ���ִ�����������OTHER)
            req.setWarehouseCode(list.getString("warehouseCode").equalsIgnoreCase("null")?"OTHER":list.getString("warehouseCode"));
            //��������
            req.setOwnerCode(list.getString("ownerCode").equalsIgnoreCase("null")?"":list.getString("ownerCode"));
            req.setVersion(constants.getVersion());
            //��Ӧ�̱���
            req.setSupplierCode(list.getString("supplierCode").equalsIgnoreCase("null")?"":list.getString("supplierCode"));
            //��Ӧ������
            req.setSupplierName(list.getString("supplierName").equalsIgnoreCase("null")?"":list.getString("supplierName"));
            /**----------------��Ʒ��Ϣ����������--------*/
            Item obj1 = new Item();
            //��Ʒ����
            obj1.setItemCode(list.getString("itemCode")==null?"":list.getString("itemCode"));
            //�ִ�ϵͳ��Ʒ����(���ֶ���WMS�������Ʒ���;WMS�����������Ʒ����;���������Ʒ��������Ҫ�����ֶ�;���WMS��ʹ��;WMS�� �Է���itemId=itemCode��ֵ)
            obj1.setItemId(list.getString("itemId")==null?"":list.getString("itemId"));
            //����
            obj1.setGoodsCode(list.getString("goodsCode")==null?"":list.getString("goodsCode"));
            //��Ʒ����
            obj1.setItemName(list.getString("itemName")==null?"":list.getString("itemName"));
            //��Ʒ���
            obj1.setShortName(list.getString("shortName")==null?"":list.getString("shortName"));
            //Ӣ����
            obj1.setEnglishName(list.getString("englishName")==null?"":list.getString("englishName"));
            //������(�ɶ��;�÷ֺ�;����)
            obj1.setBarCode(list.getString("barCode")==null?"":list.getString("barCode"));
            //��Ʒ����(���ɫ;XXL)
            obj1.setSkuProperty(list.getString("skuProperty")==null?"":list.getString("skuProperty"));
            //��Ʒ������λ
            obj1.setStockUnit(list.getString("stockUnit")==null?"":list.getString("stockUnit"));
            //��(��λ������)
            obj1.setLength(list.getString("length")==null?"":list.getString("length"));
            //��(��λ������)
            obj1.setWidth(list.getString("width")==null?"":list.getString("width"));
            //��(��λ������)
            obj1.setHeight(list.getString("height")==null?"":list.getString("height"));
            //���(��λ����)
            obj1.setVolume(list.getString("volume")==null?"":list.getString("volume"));
            //ë��(��λ��ǧ��)
            obj1.setGrossWeight(list.getString("grossWeight")==null?"":list.getString("grossWeight"));
            //����(��λ��ǧ��)
            obj1.setNetWeight(list.getString("netWeight")==null?"":list.getString("netWeight"));
            //��ɫ
            obj1.setColor(list.getString("color")==null?"":list.getString("color"));
            //�ߴ�
            obj1.setSize(list.getString("size")==null?"":list.getString("size"));
            //�����е���Ʒ����
            obj1.setTitle(list.getString("title")==null?"":list.getString("title"));
            //��Ʒ���ID
            obj1.setCategoryId(list.getString("categoryId")==null?"":list.getString("categoryId"));
            //��Ʒ�������
            obj1.setCategoryName(list.getString("categoryName")==null?"":list.getString("categoryName"));
            //�Ƽۻ���
            obj1.setPricingCategory(list.getString("pricingCategory")==null?"":list.getString("pricingCategory"));
            //��ȫ���
            obj1.setSafetyStock(list.getString("safetyStock")==null?0:Long.valueOf(list.getString("safetyStock")));
            //��Ʒ����(ZC=������Ʒ;FX=������Ʒ;ZH=�����Ʒ;ZP=��Ʒ;BC=����;HC=�Ĳ�;FL=����;XN=����Ʒ;FS=����Ʒ;CC=�д�Ʒ; OTHER=����;ֻ��Ӣ�ı���)
            obj1.setItemType(list.getString("itemType")==null?"":list.getString("itemType"));
            //���Ƽ�
            obj1.setTagPrice(list.getString("tagPrice")==null?"":list.getString("tagPrice"));
            //���ۼ�
            obj1.setRetailPrice(list.getString("retailPrice")==null?"":list.getString("retailPrice"));
            //�ɱ���
            obj1.setCostPrice(list.getString("costPrice")==null?"":list.getString("costPrice"));
            //�ɹ���
            obj1.setPurchasePrice(list.getString("purchasePrice")==null?"":list.getString("purchasePrice"));
            //���ڱ���
            obj1.setSeasonCode(list.getString("seasonCode")==null?"":list.getString("seasonCode"));
            //��������
            obj1.setSeasonName(list.getString("seasonName")==null?"":list.getString("seasonName"));
            //Ʒ�ƴ���
            obj1.setBrandCode(list.getString("brandCode")==null?"":list.getString("brandCode"));
            //Ʒ������
            obj1.setBrandName(list.getString("brandName")==null?"":list.getString("brandName"));
            //�Ƿ���Ҫ���Ź���(Y/N ;Ĭ��ΪN)
            obj1.setIsSNMgmt(list.getString("isSNMgmt")==null?"":list.getString("isSNMgmt"));
            //��������(YYYY-MM-DD)
            obj1.setProductDate(list.getString("productDate")==null?"":list.getString("productDate"));
            //��������(YYYY-MM-DD)
            obj1.setExpireDate(list.getString("expireDate")==null?"":list.getString("expireDate"));
            //�Ƿ���Ҫ�����ڹ���(Y/N ;Ĭ��ΪN)
            obj1.setIsShelfLifeMgmt(list.getString("isShelfLifeMgmt")==null?"N":list.getString("isShelfLifeMgmt"));
            //������(��λ��Сʱ)
            obj1.setShelfLife(list.getString("shelfLife")==null?1:Long.valueOf(list.getString("shelfLife")));
            //�����ڽ�������
            obj1.setRejectLifecycle(list.getString("rejectLifecycle")==null?1:Long.valueOf(list.getString("rejectLifecycle")));
            //�����ڽ�������
            obj1.setLockupLifecycle(list.getString("lockupLifecycle")==null?1:Long.valueOf(list.getString("lockupLifecycle")));
            //����������Ԥ������
            obj1.setAdventLifecycle(list.getString("adventLifecycle")==null?1:Long.valueOf(list.getString("adventLifecycle")));
            //�Ƿ���Ҫ���ι���(Y/N ;Ĭ��ΪN)
            obj1.setIsBatchMgmt(list.getString("isBatchMgmt")==null?"N":list.getString("isBatchMgmt"));
            //���δ���
            obj1.setBatchCode(list.getString("batchCode")==null?"":list.getString("batchCode"));
            //���α�ע
            obj1.setBatchRemark(list.getString("batchRemark")==null?"":list.getString("batchRemark"));
            //��װ����
            obj1.setPackCode(list.getString("packCode")==null?"":list.getString("packCode"));
            //���
            obj1.setPcs(list.getString("pcs")==null?"":list.getString("pcs"));
            //��Ʒ��ԭ����
            obj1.setOriginAddress(list.getString("originAddress")==null?"":list.getString("originAddress"));
            //��׼�ĺ�
            obj1.setApprovalNumber(list.getString("approvalNumber")==null?"":list.getString("approvalNumber"));
            //�Ƿ�����Ʒ(Y/N ;Ĭ��ΪN)
            obj1.setIsFragile(list.getString("isFragile")==null?"N":list.getString("isFragile"));
            //�Ƿ�Σ��Ʒ(Y/N ;Ĭ��ΪN)
            obj1.setIsHazardous(list.getString("isHazardous")==null?"N":list.getString("isHazardous"));
            //��ע
            obj1.setRemark(list.getString("remark")==null?"":list.getString("remark"));
            //����ʱ��(YYYY-MM-DD HH:MM:SS)
            obj1.setCreateTime(list.getString("createTime")==null?"":list.getString("createTime"));
            //����ʱ��(YYYY-MM-DD HH:MM:SS)
            obj1.setUpdateTime(list.getString("updateTime")==null?"":list.getString("updateTime"));
            //�Ƿ���Ч(Y/N ;Ĭ��ΪN)
            obj1.setIsValid(list.getString("isValid")==null?"N":list.getString("isValid"));
            //�Ƿ�sku(Y/N ;Ĭ��ΪN)
            obj1.setIsSku(list.getString("isSku")==null?"N":list.getString("isSku"));
            //��Ʒ��װ��������
            obj1.setPackageMaterial(list.getString("packageMaterial")==null?"":list.getString("packageMaterial"));
            //temp
            obj1.setSupplierCode(list.getString("temp_supplierCode")==null?"":list.getString("temp_supplierCode"));
            //�������ͷ�ʽ��0=����|1=����
            obj1.setLogisticsType(list.getString("logisticsType")==null?"":list.getString("logisticsType"));
            //�Ƿ�Һ��, Y/N, (Ĭ��ΪN)
            req.setItem(obj1);
            SingleitemSynchronizeResponse rsp = client.execute(req);
            result = "----��Ʒͬ���ӿڷ���-----code��" + rsp.getCode() + "---msg:" + rsp.getMessage() + "----flag:" + rsp.getFlag() + "----itemId:" + rsp.getItemId();
            String xml = rsp.getMessage();
            if (rsp.getFlag().equalsIgnoreCase("success")) {
                util.updateSql("", "WMS_PRODUCTALIAS", 99, new BigDecimal(list.getString("id")), "");
            } else {
                String msg = rsp.getMessage().replace("'", "\"");
                util.updateSql(msg, "WMS_PRODUCTALIAS", 90, new BigDecimal(list.getString("id")), "");
            }
        }
        return result;
    }
}

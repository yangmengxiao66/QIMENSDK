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
 * @Description :商品信息同步接口 ERP将新增的商品信息传到奇门
 * @Reference :
 * @Author : zhang.bw
 * @CreateDate : 2019-07-04 17:00
 * @Modify:YMX/2019-12-02
 */
public class SingleItemSynchronizeService {

    /**
     * 查找[商品信息 WMS_PRODUCTALIAS]表中[接收状态 RECIVESTATUS]为80或90的记录
     * 将这些记录调用奇门接口传入奇门
     * 如果传入成功，则更新[商品信息 WMS_PRODUCTALIAS]的[接收状态 RECIVESTATUS]为99；
     * 如果传入失败，则更新[商品信息 WMS_PRODUCTALIAS]的[接收状态 RECIVESTATUS]为90，并更新错误信息到字段[接收错误原因 ERRMSG]上。
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
            //操作类型(两种类型：add|update)/必填
            if("update".equals(type)){
                    req.setActionType("update");
                } else{
                    req.setActionType("add");
                }
            //仓库编码(统仓统配等无需ERP指定仓储编码的情况填OTHER)
            req.setWarehouseCode(list.getString("warehouseCode").equalsIgnoreCase("null")?"OTHER":list.getString("warehouseCode"));
            //货主编码
            req.setOwnerCode(list.getString("ownerCode").equalsIgnoreCase("null")?"":list.getString("ownerCode"));
            req.setVersion(constants.getVersion());
            //供应商编码
            req.setSupplierCode(list.getString("supplierCode").equalsIgnoreCase("null")?"":list.getString("supplierCode"));
            //供应商名称
            req.setSupplierName(list.getString("supplierName").equalsIgnoreCase("null")?"":list.getString("supplierName"));
            /**----------------商品信息，单条传输--------*/
            Item obj1 = new Item();
            //商品编码
            obj1.setItemCode(list.getString("itemCode")==null?"":list.getString("itemCode"));
            //仓储系统商品编码(该字段是WMS分配的商品编号;WMS如果分配了商品编码;则后续的商品操作都需要传该字段;如果WMS不使用;WMS可 以返回itemId=itemCode的值)
            obj1.setItemId(list.getString("itemId")==null?"":list.getString("itemId"));
            //货号
            obj1.setGoodsCode(list.getString("goodsCode")==null?"":list.getString("goodsCode"));
            //商品名称
            obj1.setItemName(list.getString("itemName")==null?"":list.getString("itemName"));
            //商品简称
            obj1.setShortName(list.getString("shortName")==null?"":list.getString("shortName"));
            //英文名
            obj1.setEnglishName(list.getString("englishName")==null?"":list.getString("englishName"));
            //条形码(可多个;用分号;隔开)
            obj1.setBarCode(list.getString("barCode")==null?"":list.getString("barCode"));
            //商品属性(如红色;XXL)
            obj1.setSkuProperty(list.getString("skuProperty")==null?"":list.getString("skuProperty"));
            //商品计量单位
            obj1.setStockUnit(list.getString("stockUnit")==null?"":list.getString("stockUnit"));
            //长(单位：厘米)
            obj1.setLength(list.getString("length")==null?"":list.getString("length"));
            //宽(单位：厘米)
            obj1.setWidth(list.getString("width")==null?"":list.getString("width"));
            //高(单位：厘米)
            obj1.setHeight(list.getString("height")==null?"":list.getString("height"));
            //体积(单位：升)
            obj1.setVolume(list.getString("volume")==null?"":list.getString("volume"));
            //毛重(单位：千克)
            obj1.setGrossWeight(list.getString("grossWeight")==null?"":list.getString("grossWeight"));
            //净重(单位：千克)
            obj1.setNetWeight(list.getString("netWeight")==null?"":list.getString("netWeight"));
            //颜色
            obj1.setColor(list.getString("color")==null?"":list.getString("color"));
            //尺寸
            obj1.setSize(list.getString("size")==null?"":list.getString("size"));
            //渠道中的商品标题
            obj1.setTitle(list.getString("title")==null?"":list.getString("title"));
            //商品类别ID
            obj1.setCategoryId(list.getString("categoryId")==null?"":list.getString("categoryId"));
            //商品类别名称
            obj1.setCategoryName(list.getString("categoryName")==null?"":list.getString("categoryName"));
            //计价货类
            obj1.setPricingCategory(list.getString("pricingCategory")==null?"":list.getString("pricingCategory"));
            //安全库存
            obj1.setSafetyStock(list.getString("safetyStock")==null?0:Long.valueOf(list.getString("safetyStock")));
            //商品类型(ZC=正常商品;FX=分销商品;ZH=组合商品;ZP=赠品;BC=包材;HC=耗材;FL=辅料;XN=虚拟品;FS=附属品;CC=残次品; OTHER=其它;只传英文编码)
            obj1.setItemType(list.getString("itemType")==null?"":list.getString("itemType"));
            //吊牌价
            obj1.setTagPrice(list.getString("tagPrice")==null?"":list.getString("tagPrice"));
            //零售价
            obj1.setRetailPrice(list.getString("retailPrice")==null?"":list.getString("retailPrice"));
            //成本价
            obj1.setCostPrice(list.getString("costPrice")==null?"":list.getString("costPrice"));
            //采购价
            obj1.setPurchasePrice(list.getString("purchasePrice")==null?"":list.getString("purchasePrice"));
            //季节编码
            obj1.setSeasonCode(list.getString("seasonCode")==null?"":list.getString("seasonCode"));
            //季节名称
            obj1.setSeasonName(list.getString("seasonName")==null?"":list.getString("seasonName"));
            //品牌代码
            obj1.setBrandCode(list.getString("brandCode")==null?"":list.getString("brandCode"));
            //品牌名称
            obj1.setBrandName(list.getString("brandName")==null?"":list.getString("brandName"));
            //是否需要串号管理(Y/N ;默认为N)
            obj1.setIsSNMgmt(list.getString("isSNMgmt")==null?"":list.getString("isSNMgmt"));
            //生产日期(YYYY-MM-DD)
            obj1.setProductDate(list.getString("productDate")==null?"":list.getString("productDate"));
            //过期日期(YYYY-MM-DD)
            obj1.setExpireDate(list.getString("expireDate")==null?"":list.getString("expireDate"));
            //是否需要保质期管理(Y/N ;默认为N)
            obj1.setIsShelfLifeMgmt(list.getString("isShelfLifeMgmt")==null?"N":list.getString("isShelfLifeMgmt"));
            //保质期(单位：小时)
            obj1.setShelfLife(list.getString("shelfLife")==null?1:Long.valueOf(list.getString("shelfLife")));
            //保质期禁收天数
            obj1.setRejectLifecycle(list.getString("rejectLifecycle")==null?1:Long.valueOf(list.getString("rejectLifecycle")));
            //保质期禁售天数
            obj1.setLockupLifecycle(list.getString("lockupLifecycle")==null?1:Long.valueOf(list.getString("lockupLifecycle")));
            //保质期临期预警天数
            obj1.setAdventLifecycle(list.getString("adventLifecycle")==null?1:Long.valueOf(list.getString("adventLifecycle")));
            //是否需要批次管理(Y/N ;默认为N)
            obj1.setIsBatchMgmt(list.getString("isBatchMgmt")==null?"N":list.getString("isBatchMgmt"));
            //批次代码
            obj1.setBatchCode(list.getString("batchCode")==null?"":list.getString("batchCode"));
            //批次备注
            obj1.setBatchRemark(list.getString("batchRemark")==null?"":list.getString("batchRemark"));
            //包装代码
            obj1.setPackCode(list.getString("packCode")==null?"":list.getString("packCode"));
            //箱规
            obj1.setPcs(list.getString("pcs")==null?"":list.getString("pcs"));
            //商品的原产地
            obj1.setOriginAddress(list.getString("originAddress")==null?"":list.getString("originAddress"));
            //批准文号
            obj1.setApprovalNumber(list.getString("approvalNumber")==null?"":list.getString("approvalNumber"));
            //是否易碎品(Y/N ;默认为N)
            obj1.setIsFragile(list.getString("isFragile")==null?"N":list.getString("isFragile"));
            //是否危险品(Y/N ;默认为N)
            obj1.setIsHazardous(list.getString("isHazardous")==null?"N":list.getString("isHazardous"));
            //备注
            obj1.setRemark(list.getString("remark")==null?"":list.getString("remark"));
            //创建时间(YYYY-MM-DD HH:MM:SS)
            obj1.setCreateTime(list.getString("createTime")==null?"":list.getString("createTime"));
            //更新时间(YYYY-MM-DD HH:MM:SS)
            obj1.setUpdateTime(list.getString("updateTime")==null?"":list.getString("updateTime"));
            //是否有效(Y/N ;默认为N)
            obj1.setIsValid(list.getString("isValid")==null?"N":list.getString("isValid"));
            //是否sku(Y/N ;默认为N)
            obj1.setIsSku(list.getString("isSku")==null?"N":list.getString("isSku"));
            //商品包装材料类型
            obj1.setPackageMaterial(list.getString("packageMaterial")==null?"":list.getString("packageMaterial"));
            //temp
            obj1.setSupplierCode(list.getString("temp_supplierCode")==null?"":list.getString("temp_supplierCode"));
            //销售配送方式（0=自配|1=菜鸟）
            obj1.setLogisticsType(list.getString("logisticsType")==null?"":list.getString("logisticsType"));
            //是否液体, Y/N, (默认为N)
            req.setItem(obj1);
            SingleitemSynchronizeResponse rsp = client.execute(req);
            result = "----商品同步接口返回-----code：" + rsp.getCode() + "---msg:" + rsp.getMessage() + "----flag:" + rsp.getFlag() + "----itemId:" + rsp.getItemId();
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

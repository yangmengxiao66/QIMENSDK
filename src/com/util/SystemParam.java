
package com.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author ymx
 * 
 * 配置文件：
 * 包含:sql
 * 
 * 
 *
 */
public class SystemParam {
	
//	public static void main(String[] args) {
//		System.out.println(new SystemParam().toString());
//	}
	
	   //从文件中获取内容
		public  SystemParam() {
			String classPath = SystemParam.class.getResource("SystemParam.class")
					.toString();
			System.out.println("classPath:" + classPath);
			try {
				String classFilePath = classPath;
				if (classFilePath.startsWith("file:/"))
					classFilePath = classFilePath.substring(6);
				classFilePath = classFilePath.replace("%20", " ");
				int pos = classFilePath.lastIndexOf(47);
				String file = classFilePath.substring(0, pos + 1);
				file = file + "config.properties";
				InputStream is;
				is = new FileInputStream(file);
				Properties p = new Properties();
				p.load(is);
				update_Sql = p.getProperty("update_Sql");
				DeliveryOrderCreate_Sql=p.getProperty("DeliveryOrderCreate_Sql");
				DeliveryOrderCreate_ItemSql=p.getProperty("DeliveryOrderCreate_ItemSql");
				DeliveryOrderConfirm_Sql=p.getProperty("DeliveryOrderConfirm_Sql");
				DeliveryOrderConfirm_ItemSql=p.getProperty("DeliveryOrderConfirm_ItemSql");
				DeliveryOrderConfirm_IsHasSql=p.getProperty("DeliveryOrderConfirm_IsHasSql");
				EntryOrderCreate_Sql=p.getProperty("EntryOrderCreate_Sql");
				EntryOrderCreate_ItemSql=p.getProperty("EntryOrderCreate_Sql");
				EntryOrderConfirm_Sql=p.getProperty("EntryOrderConfirm_Sql");
				EntryOrderConfirm_ItemSql=p.getProperty("EntryOrderConfirm_ItemSql");
				EntryOrderConfirm_IsHasSql=p.getProperty("EntryOrderConfirm_IsHasSql");
				InventoryReport_Sql=p.getProperty("InventoryReport_Sql");
				InventoryReport_ItemSql=p.getProperty("InventoryReport_ItemSql");
				OrderCancelService_sql=p.getProperty("OrderCancelService_sql");
				OrderCancelService_Insql=p.getProperty("OrderCancelService_Insql");
				OrderCancelService_Outsql=p.getProperty("OrderCancelService_Outsql");
				OrderCancelService_Procedure=p.getProperty("OrderCancelService_Procedure");
				SingleItemSynchronizeService_sql=p.getProperty("SingleItemSynchronizeService_sql");
				StockOutCreateService_sql=p.getProperty("StockOutCreateService_sql");
				StockOutCreateService_itemSql=p.getProperty("StockOutCreateService_itemSql");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	
	/**
	 * 更新sql
	 * 
	 * update ? set EDIFLAG=?,RECIVEDATE=SYSDATE,ERRMSG=? where id=?
	 * 
	 * */
	private String update_Sql="";
	
	/**-----------------------------------发货单--------------------------------------*/
	/**发货单创建主表sql
	 * select id,to_char(CREATIONDATE,'YYYY-MM-DD HH:MM:SS'),DOCNO,WMS_STORECODE,RECEIVER_NAME,RECEIVER_PHONE,RECEIVER_MOBILE,RECEIVER_ADDRESS,RECEIVER_PROVINCE,RECEIVER_CITY,DESCRIPTION,LOGISTICSNAME,DESTCODE,SCHEDULEDATE,to_char(MODIFIEDDATE,'YYYY-MM-DD HH:MM:SS'),to_char(CREATIONDATE,'YYYY-MM-DD HH:MM:SS'),LOGISTICSNAME,FNAME,FMOBILE,FPROVINCE,FCITY,FAREA,FDETAILADDRESS,AREA,OwnerCode from WMS_OUT_NOTICES where RECIVESTATUS<>99
	 * 
	 * */
	private String DeliveryOrderCreate_Sql="";
	
	/**发货单创建明细sql
	 * 
	 * select PRODUCTALIAS_NO,QTY from WMS_OUT_NOTICESITEM where DOCNO=?
	 * 
	*/
	private String DeliveryOrderCreate_ItemSql="";
	
	/**
	 * 发货单回传新增主表sql
	 * insert into WMS_RESULT_OUT(ID,DOCNO,WMS_DOCNO,DATEOUT,RECIVEDATE,CREATIONDATE,MODIFIEDDATE) values (?,?,?,to_number(to_char(SYSDATE, 'YYYYMMDD')),SYSDATE,SYSDATE,SYSDATE)
	 * 
	 * */
	private String DeliveryOrderConfirm_Sql="";
	/**
	 * 发货单回传新增明细表sql
	 * insert into WMS_RESULT_OUTITEM (ID,AD_CLIENT_ID,AD_ORG_ID,DOCNO,WMS_RESULT_OUT_ID,PRODUCTALIAS_NO,QTYOUT,CREATIONDATE,MODIFIEDDATE) values (?,37,27,?,?,?,?,sysdate,sysdate)
	 * 
	 * */
	private String DeliveryOrderConfirm_ItemSql="";
	/**
	 * 发货单回传查询是否重复sql
	 * 
	 * select id from WMS_RESULT_OUT where DOCNO=?
	 * */
	private String DeliveryOrderConfirm_IsHasSql="";
	/**-----------------------------------入库单--------------------------------------*/
	
	/**
	 * 入库单主表sql
	 * 
	 * select id,BILLTYPE,DOCNO,WMS_STORECODE,DESCRIPTION,WMS_STORECODE,ORIGCODE,PREDATEIN from WMS_IN_NOTICES where RECIVESTATUS in(80,90)
	 * 
	 * */
	private String EntryOrderCreate_Sql="";
	
	/**
	 * 入库单明细sql
	 * 
	 * select PRODUCTALIAS_NO,QTY from WMS_IN_NOTICESITEM where DOCNO=?
	 * 
	 * */
	private String EntryOrderCreate_ItemSql="";
	/**
	 * 入库单回传主表sql
	 * insert into WMS_RESULT_IN (ID,DOCNO,WMS_DOCNO,DATEIN,CREATIONDATE,MODIFIEDDATE) values (?,?,?,?,sysdate,sysdate)
	 * 
	 * */
	private String EntryOrderConfirm_Sql="";
	/**
	 * 入库单回传明细表sql
	 * insert into WMS_RESULT_INITEM (ID,DOCNO,PRODUCTALIAS_NO,QTYIN,WMS_RESULT_IN_ID,CREATIONDATE,MODIFIEDDATE) values (?,?,?,?,?,sysdate,sysdate)
	 * 
	 * */
	private String EntryOrderConfirm_ItemSql="";
	/**
	 * 入库单回传查询重复sql
	 * select id from WMS_RESULT_IN where DOCNO=?
	 * 
	 * */
	private String EntryOrderConfirm_IsHasSql="";	
	
	/**-----------------------------------库存盘点--------------------------------------*/
	
	/**
	 * 库存盘点主表
	 * 
	 * insert into WMS_ADJ_OUT(ID,AD_CLIENT_ID,AD_ORG_ID,DOCNO,BILL_TYPE,BILLDATE,ORIGNAME,WMS_REASON,OWNERID,MODIFIERID,CREATIONDATE,MODIFIEDDATE,ISACTIVE)  values (?,37,27,?,?,?,?,?,null,null,sysdate,sysdate,'Y')
	 * 
	 * */
	private String InventoryReport_Sql ="";
	
	/**
	 * 库存盘点明细sql
	 * 
	 * insert into WMS_ADJ_OUTITEM(ID,AD_CLIENT_ID,AD_ORG_ID,WMS_ADJ_OUT_ID,DOCNO,PRODUCTALIAS_NO,QTYBOOK,QTYCOUNT,QTYDIFF,WMSITEM_REASON,OWNERID,MODIFIERID,CREATIONDATE,MODIFIEDDATE,ISACTIVE) values (?,37,27,?,?,?,null,null,?,null,null,null,sysdate,sysdate,'Y')
	 * 
	 * */
	private String InventoryReport_ItemSql="";
	/**-----------------------------------取消--------------------------------------*/
	/**
	 * 取消中间表查询sql
	 * 
	 * select m.drp_docno,m.billtype,m.cancel_reason from M_WMSINOUT_CANCEL m where id=?
	 * 
	 * */
	private String OrderCancelService_sql="";
	
	/**
	 * 查询入库数据
	 * 
	 * select billtype,WMS_STORECODE from WMS_IN_NOTICES where docno =?
	 * */
	private String OrderCancelService_Insql="";
	/**
	 * 查询出库数据
	 * 
	 * select billtype,WMS_STORECODE from WMS_OUT_NOTICES where docno =?
	 * 
	 * */
	private String OrderCancelService_Outsql="";
	/**
	 * 取消提交程序
	 * 
	 * M_WMSINOUT_CANCEL_SUBMIT
	 * 
	 * */
	private String OrderCancelService_Procedure="";
	
	/**-----------------------------------商品信息--------------------------------------*/
	/**
	 * 商品查询sql
	 * 
	 * select wp.id,wp.no,wp.product_name,wp.no,wp.color_name,wp.size_name,wp.type from WMS_PRODUCTALIAS wp where RECIVESTATUS <> 99
	 * 
	 * */
	private String SingleItemSynchronizeService_sql="";
	/**-----------------------------------出库单创建--------------------------------------*/
	/**
	 * 出库单创建主表sql
	 * 
	 * select id, BILLTYPE, DOCNO, WMS_STORECODE, to_char(CREATIONDATE, 'YYYY-MM-DD HH:MM:SS'), RECEIVER_NAME, RECEIVER_POSTAL, RECEIVER_PHONE, RECEIVER_MOBILE, RECEIVER_ADDRESS, DESCRIPTION, RECEIVER_PROVINCE, RECEIVER_CITY, RECEIVER_DISTRICT  from WMS_OUT_NOTICES where RECIVESTATUS <> 99
	 * 
	 * */
	private String StockOutCreateService_sql="";
	/**
	 * 出库单创建明细sql
	 * 
	 * select PRODUCTALIAS_NO,QTY from WMS_OUT_NOTICESITEM where DOCNO=?
	 * 
	 * */
	private String StockOutCreateService_itemSql="";
	
	

	public String getEntryOrderConfirm_Sql() {
		return EntryOrderConfirm_Sql;
	}

	public void setEntryOrderConfirm_Sql(String entryOrderConfirm_Sql) {
		EntryOrderConfirm_Sql = entryOrderConfirm_Sql;
	}

	public String getEntryOrderConfirm_ItemSql() {
		return EntryOrderConfirm_ItemSql;
	}

	public void setEntryOrderConfirm_ItemSql(String entryOrderConfirm_ItemSql) {
		EntryOrderConfirm_ItemSql = entryOrderConfirm_ItemSql;
	}

	public String getEntryOrderConfirm_IsHasSql() {
		return EntryOrderConfirm_IsHasSql;
	}

	public void setEntryOrderConfirm_IsHasSql(String entryOrderConfirm_IsHasSql) {
		EntryOrderConfirm_IsHasSql = entryOrderConfirm_IsHasSql;
	}



	public String getUpdate_Sql() {
		return update_Sql;
	}

	public void setUpdate_Sql(String update_Sql) {
		this.update_Sql = update_Sql;
	}

	public String getSingleItemSynchronizeService_sql() {
		return SingleItemSynchronizeService_sql;
	}

	public void setSingleItemSynchronizeService_sql(
			String singleItemSynchronizeService_sql) {
		SingleItemSynchronizeService_sql = singleItemSynchronizeService_sql;
	}

	public String getStockOutCreateService_sql() {
		return StockOutCreateService_sql;
	}

	public void setStockOutCreateService_sql(String stockOutCreateService_sql) {
		StockOutCreateService_sql = stockOutCreateService_sql;
	}

	public String getStockOutCreateService_itemSql() {
		return StockOutCreateService_itemSql;
	}

	public void setStockOutCreateService_itemSql(
			String stockOutCreateService_itemSql) {
		StockOutCreateService_itemSql = stockOutCreateService_itemSql;
	}

	public String getOrderCancelService_sql() {
		return OrderCancelService_sql;
	}

	public void setOrderCancelService_sql(String orderCancelService_sql) {
		OrderCancelService_sql = orderCancelService_sql;
	}

	public String getOrderCancelService_Insql() {
		return OrderCancelService_Insql;
	}

	public void setOrderCancelService_Insql(String orderCancelService_Insql) {
		OrderCancelService_Insql = orderCancelService_Insql;
	}

	public String getOrderCancelService_Outsql() {
		return OrderCancelService_Outsql;
	}

	public void setOrderCancelService_Outsql(String orderCancelService_Outsql) {
		OrderCancelService_Outsql = orderCancelService_Outsql;
	}

	public String getOrderCancelService_Procedure() {
		return OrderCancelService_Procedure;
	}

	public void setOrderCancelService_Procedure(String orderCancelService_Procedure) {
		OrderCancelService_Procedure = orderCancelService_Procedure;
	}

	public String getInventoryReport_Sql() {
		return InventoryReport_Sql;
	}

	public void setInventoryReport_Sql(String inventoryReport_Sql) {
		InventoryReport_Sql = inventoryReport_Sql;
	}

	public String getInventoryReport_ItemSql() {
		return InventoryReport_ItemSql;
	}

	public void setInventoryReport_ItemSql(String inventoryReport_ItemSql) {
		InventoryReport_ItemSql = inventoryReport_ItemSql;
	}

	public String getEntryOrderCreate_Sql() {
		return EntryOrderCreate_Sql;
	}

	public void setEntryOrderCreate_Sql(String entryOrderCreate_Sql) {
		EntryOrderCreate_Sql = entryOrderCreate_Sql;
	}

	public String getEntryOrderCreate_ItemSql() {
		return EntryOrderCreate_ItemSql;
	}

	public void setEntryOrderCreate_ItemSql(String entryOrderCreate_ItemSql) {
		EntryOrderCreate_ItemSql = entryOrderCreate_ItemSql;
	}

	public String getDeliveryOrderCreate_Sql() {
		return DeliveryOrderCreate_Sql;
	}

	public void setDeliveryOrderCreate_Sql(String deliveryOrderCreate_Sql) {
		DeliveryOrderCreate_Sql = deliveryOrderCreate_Sql;
	}

	public String getDeliveryOrderCreate_ItemSql() {
		return DeliveryOrderCreate_ItemSql;
	}

	public void setDeliveryOrderCreate_ItemSql(String deliveryOrderCreate_ItemSql) {
		DeliveryOrderCreate_ItemSql = deliveryOrderCreate_ItemSql;
	}

	public String getDeliveryOrderConfirm_Sql() {
		return DeliveryOrderConfirm_Sql;
	}

	public void setDeliveryOrderConfirm_Sql(String deliveryOrderConfirm_Sql) {
		DeliveryOrderConfirm_Sql = deliveryOrderConfirm_Sql;
	}

	public String getDeliveryOrderConfirm_ItemSql() {
		return DeliveryOrderConfirm_ItemSql;
	}

	public void setDeliveryOrderConfirm_ItemSql(String deliveryOrderConfirm_ItemSql) {
		DeliveryOrderConfirm_ItemSql = deliveryOrderConfirm_ItemSql;
	}

	public String getDeliveryOrderConfirm_IsHasSql() {
		return DeliveryOrderConfirm_IsHasSql;
	}

	public void setDeliveryOrderConfirm_IsHasSql(
			String deliveryOrderConfirm_IsHasSql) {
		DeliveryOrderConfirm_IsHasSql = deliveryOrderConfirm_IsHasSql;
	}

	@Override
	public String toString() {
		return "SystemParam [update_Sql=" + update_Sql
				+ "; DeliveryOrderCreate_Sql=" + DeliveryOrderCreate_Sql
				+ "; DeliveryOrderCreate_ItemSql="
				+ DeliveryOrderCreate_ItemSql + "; DeliveryOrderConfirm_Sql="
				+ DeliveryOrderConfirm_Sql + "; DeliveryOrderConfirm_ItemSql="
				+ DeliveryOrderConfirm_ItemSql
				+ "; DeliveryOrderConfirm_IsHasSql="
				+ DeliveryOrderConfirm_IsHasSql + "; EntryOrderCreate_Sql="
				+ EntryOrderCreate_Sql + "; EntryOrderCreate_ItemSql="
				+ EntryOrderCreate_ItemSql + "; EntryOrderConfirm_Sql="
				+ EntryOrderConfirm_Sql + "; EntryOrderConfirm_ItemSql="
				+ EntryOrderConfirm_ItemSql + "; EntryOrderConfirm_IsHasSql="
				+ EntryOrderConfirm_IsHasSql + "; InventoryReport_Sql="
				+ InventoryReport_Sql + "; InventoryReport_ItemSql="
				+ InventoryReport_ItemSql + "; OrderCancelService_sql="
				+ OrderCancelService_sql + "; OrderCancelService_Insql="
				+ OrderCancelService_Insql + "; OrderCancelService_Outsql="
				+ OrderCancelService_Outsql + "; OrderCancelService_Procedure="
				+ OrderCancelService_Procedure
				+ "; SingleItemSynchronizeService_sql="
				+ SingleItemSynchronizeService_sql
				+ "; StockOutCreateService_sql=" + StockOutCreateService_sql
				+ "; StockOutCreateService_itemSql="
				+ StockOutCreateService_itemSql + "]";
	}

	
	
	
	

	
}

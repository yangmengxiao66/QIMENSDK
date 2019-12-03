package com.qimen.service.impl;

import com.bean.Constants;
import com.qimen.api.*;
import com.qimen.service.QiMenService;
import com.taobao.api.ApiException;
import com.util.ApiName;
import org.dom4j.DocumentException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class QiMenServiceImpl implements QiMenService {

    @Override
    public String execute(String apiName, Constants constants, Object[] args) throws ApiException, SQLException, ClassNotFoundException {
        if (ApiName.SINGLEITEM_SYNCHRONIZE.equals(apiName))
            return new SingleItemSynchronizeService().doItemSynchronize(constants);
        if (ApiName.DELIVERYORDER_CREATE.equals(apiName))
            return new DeliveryOrderCreateService().deliveryOrderCreate(constants);
        if (ApiName.ENTRYORDER_CREATE.equals(apiName))
            return new EntryOrderCreateService().entryOrderCreate(constants);
        if (ApiName.STOCKOUT_CREATE.equals(apiName))
            return new StockOutCreateService().stockOutCreateService(constants);
        if (ApiName.ORDER_CANCEL.equals(apiName))
            return new OrderCancelService().orderCancel(constants, (Long) args[0]);
        return null;
    }

    @Override
    public String execute(String apiName, Constants constants, HttpServletRequest req, HttpServletResponse resp) throws ClassNotFoundException, SQLException, DocumentException, IOException, ParseException {
        if (ApiName.DELIVERYORDER_CONFIRM.equals(apiName) || ApiName.STOCKOUT_CONFIRM.equals(apiName))
            return new DeliveryOrderConfirmService().deliveryOrderConfirmService(constants, req, resp);
        if (ApiName.ENTRYORDER_CONFIRM.equals(apiName))
            return new EntryOrderConfirmService().entryOrderConfirmService(constants, req, resp);
        if (ApiName.INVENTORY_REPORT.equals(apiName))
            return new InventoryReportService().inventoryReportService(constants, req, resp);
        return null;
    }
}

package com.qimen.service;

import com.bean.Constants;
import com.taobao.api.ApiException;
import org.dom4j.DocumentException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * 调用奇门接口服务统一入口
 *
 * @author zhang.bw
 * @version 1.0
 * @date 2019-07-04
 */
public interface QiMenService {

    /**
     * 执行调用方法ERP发起调用奇门
     *
     * @param apiName   需要调用的API名称,只允许传util.ApiName类中的常量 ep:ApiName.SINGLEITEM_SYNCHRONIZE
     * @param constants 公共参数的封装类
     * @param args      传参数组
     * @return 奇门接口返回参数
     */
    String execute(String apiName, Constants constants, Object[] args) throws ApiException, SQLException, ClassNotFoundException;


    /**
     * 执行调用方法奇门发起调用ERP
     *
     * @param apiName   需要调用的API名称,只允许传util.ApiName类中的常量 ep:ApiName.SINGLEITEM_SYNCHRONIZE
     * @param constants 公共参数的封装类
     * @param req       HttpServletRequest
     * @param resp      HttpServletResponse
     * @return 奇门接口返回参数
     */
    String execute(String apiName, Constants constants, HttpServletRequest req, HttpServletResponse resp) throws ClassNotFoundException, SQLException, DocumentException, IOException, ParseException;

}

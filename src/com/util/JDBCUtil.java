package com.util;

import com.bean.SPResult;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * JDBC工具类
 */
public class JDBCUtil {

    private String url;

    private String user;

    private String password;

    public JDBCUtil(String url, String user,
                    String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * 防止sql注入
     *
     * @param paramArgs
     * @param sql
     * @param pre
     * @throws SQLException
     */
    private void setSafeParam(Object[] paramArgs, String sql, PreparedStatement pre) throws SQLException {
        //记录日志使用
        StringBuilder logParam = new StringBuilder();
        if (paramArgs != null) {
            for (int i = 0; i < paramArgs.length; i++) {
                Object parameter = paramArgs[i];
                String logParamStr = "";
                if ((parameter instanceof String)) {
                    String parameterStr = (String) parameter;
                    parameterStr = parameterStr.replaceAll(";", "")
                            .replaceAll("\"", "")
                            .replaceAll("/", "")
                            .replaceAll("\\\\", "")
                            .replaceAll("delete", "")
                            .replaceAll("update", "")
                            .replaceAll("insert", "");
                    pre.setString(i + 1, parameterStr);
                    logParamStr = parameterStr;
                } else if ((parameter instanceof Integer)) {
                    int val = ((Integer) parameter).intValue();
                    pre.setInt(i + 1, val);
                    logParamStr = "" + ((Integer) parameter).intValue();
                } else if ((parameter instanceof Long)) {
                    long val = ((Long) parameter).longValue();
                    pre.setLong(i + 1, val);
                    logParamStr = "" + ((Long) parameter).longValue();
                } else if ((parameter instanceof Float)) {
                    pre.setFloat(i + 1, ((Float) parameter).floatValue());
                    logParamStr = "" + ((Float) parameter).floatValue();
                } else if ((parameter instanceof Double)) {
                    pre.setDouble(i + 1, ((Double) parameter).doubleValue());
                } else if ((parameter instanceof java.util.Date)) {
                    pre.setTimestamp(i + 1, new Timestamp(
                            ((java.util.Date) parameter).getTime()));
                    logParamStr = "" + ((Double) parameter).doubleValue();
                } else if ((parameter instanceof Clob)) {
                    pre.setClob(i + 1, (Clob) parameter);
                    logParamStr = "" + (Clob) parameter;
                } else if ((parameter instanceof Class)) {
                    if (parameter.equals(String.class))
                        pre.setNull(i + 1, 12);
                    else if (parameter.equals(Integer.class))
                        pre.setNull(i + 1, 4);
                    else if (parameter.equals(Float.class))
                        pre.setNull(i + 1, 6);
                    else if (parameter.equals(Double.class))
                        pre.setNull(i + 1, 8);
                    else if (parameter.equals(Date.class))
                        pre.setNull(i + 1, 93);
                    else if (parameter.equals(StringBuilder.class))
                        pre.setNull(i + 1, 2005);
                    else if (parameter.equals(StringBuffer.class))
                        pre.setNull(i + 1, 2005);
                    else if (parameter.equals(Clob.class))
                        pre.setNull(i + 1, 2005);
                    logParamStr = "" + parameter.getClass().toString();
                }
                logParam.append(logParamStr + ", ");
            }
        }
        System.out.println("search sql " + sql + " , parameter (" + logParam + ")");
    }

    /**
     * 查询sql
     *
     * @param sql       预编译sql
     * @param paramArgs sql传参
     * @return
     */
    public ResultSet search(String sql, Object[] paramArgs) throws SQLException, ClassNotFoundException {
        Vector<String[]> content = null;
        ResultSet rs = null;
        PreparedStatement pre = null;
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(url, user, password);
            content = new Vector<String[]>();
            if (con == null)
                return null;
            pre = con.prepareStatement(sql);
            //拼装sql传入参数
            setSafeParam(paramArgs, sql, pre);
            rs = pre.executeQuery();
            ResultSetMetaData rsMeta = rs.getMetaData();
//            while (rs.next()) {
//                int columnNum = rsMeta.getColumnCount();
//                String[] field = new String[columnNum];
//                String fieldValue = null;
//                for (int i = 1; i <= columnNum; i++) {
//                    fieldValue = rs.getString(i);
//                    if (fieldValue == null) {
//                        fieldValue = "";
//                    }
//                    field[i - 1] = fieldValue;
//                }
//                content.add(field);
//            }

        } finally {
            if (rs != null)
                rs.close();
            if (pre != null)
                pre.close();
            if (con != null)
                con.close();
        }
        return rs;
    }

    /**
     * 新增、修改sql
     *
     * @param sql       预编译sql
     * @param paramArgs sql传参
     * @return
     */
    public int execute(String sql, Object[] paramArgs) throws SQLException, ClassNotFoundException {
        boolean ret = true;
        Connection con = null;
        PreparedStatement pre = null;
        int result = -1;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(url, user, password);
            pre = con.prepareStatement(sql);
            //拼装sql传入参数
            setSafeParam(paramArgs, sql, pre);
            result = pre.executeUpdate();
        } finally {
            try {
                if (pre != null)
                    pre.close();
                if (con != null)
                    con.close();

            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 执行存储过程
     *
     * @param spName
     * @param params
     * @param hasReturnValue
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public String executeStoredProcedure(String spName, Collection params, boolean hasReturnValue) throws SQLException, ClassNotFoundException {
        CallableStatement stmt = null;
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);

            String paramQ = "";
            for (int i = 0; i < params.size(); i++) {
                paramQ += ",?";
            }
            if (hasReturnValue) paramQ += ",?,?";
            if (paramQ.length() > 0) paramQ = paramQ.substring(1);

            stmt = con.prepareCall(("{ call " + spName + " (" + paramQ + ")}"));
//        logger.debug("--- " + stmt) ;
            int j = 0;
            String desc = "{ call " + spName + " (";
            for (Iterator it = params.iterator(); it.hasNext(); ) {
                j++;
                Object param = it.next();
                desc += param + ",";
                if (param instanceof String) {
                    String parameterStr = (String) param;
                    parameterStr = parameterStr.replaceAll(";", "")
                            .replaceAll("\"", "")
                            .replaceAll("/", "")
                            .replaceAll("\\\\", "")
                            .replaceAll("delete", "")
                            .replaceAll("update", "")
                            .replaceAll("insert", "");
                    stmt.setString(j, parameterStr);
                } else if (param instanceof Integer) {
                    stmt.setInt(j, ((Integer) param).intValue());
                } else if ((param instanceof Long)) {
                    stmt.setLong(j, ((Long) param).longValue());
                } else if (param instanceof Float) {
                    stmt.setFloat(j, ((Float) param).floatValue());
                } else {
                    throw new SQLException("Intenal Error: unsupported type:" + param.getClass() + ",value=" + param);
                }
            }
            System.out.println("execute stored procedure:" + desc + ")}");

            if (hasReturnValue) {
                stmt.registerOutParameter(j + 1, java.sql.Types.INTEGER);
                stmt.registerOutParameter(j + 2, java.sql.Types.VARCHAR);
            }
            stmt.executeUpdate();
            if (hasReturnValue) {
                SPResult spr = new SPResult(stmt, j + 1);
                return spr.getMessage();
            } else {
                return "内部错误";
            }

        } catch (SQLException e) {
            System.out.println("Error doing Stored Procedure:" + spName + ":" + e.getMessage());
            return e.getMessage();
        } finally {
            try {
                stmt.close();
            } catch (Exception ea) {
            }
        }
    }

    public long getSequence(String tableName) throws SQLException, ClassNotFoundException {
        ResultSet list  = search("select Seq_" + tableName + ".NextVal From Dual", null);
        while (list.next()){
            return Long.parseLong(list.getString(0));
        }
        return 0L;
    }

    /**
     * 获取货主编码
     *
     * @return ownerCode
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public String getOwnerCode() throws SQLException, ClassNotFoundException {
        String ownerCode = "";
        ResultSet list = search("select value from ad_param p where p.name = 'portal.4305'", null);
        while (list.next()){
            ownerCode=  list.getString("value");
        }
        return ownerCode;
    }

    //更新数据状态方法
    public void updateSql(String msg, String tableName, int RECIVESTATUS, BigDecimal id, String wmsCode) throws SQLException, ClassNotFoundException {
        System.out.println("要更新的sql----" + "update " + tableName + " set EDIFLAG=" + RECIVESTATUS + ",RECIVEDATE=SYSDATE,ERRMSG='" + msg + "' where id=" + id);
        execute("update " + tableName + " set RECIVESTATUS=" + RECIVESTATUS + ",RECIVEDATE=SYSDATE,MODIFIEDDATE=SYSDATE,ERRMSG='" + msg + "' where id=" + id, null);
    }

    /**
     * 通用返回奇门信息方法
     *
     * @param req
     * @param resp
     * @param msg
     * @param code
     * @param flag
     */
    public String returnJson(HttpServletRequest req, HttpServletResponse resp, String msg, int code, String flag) throws IOException {
        resp.setCharacterEncoding("utf-8");
        /**
         * <response>
         <flag>success</flag>
         <code>0</code>
         <message>invalid appkey</message>
         </response>
         *
         *
         * */
        OutputStream os = resp.getOutputStream();
        Document document = DocumentHelper.createDocument();
        Element itemElement = document.addElement("response");
        Element idElement = itemElement.addElement("flag");
        idElement.setText(flag);
        Element nameElement = itemElement.addElement("code");
        nameElement.setText(String.valueOf(code));
        Element messageElement = itemElement.addElement("message");
        messageElement.setText(msg);
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding("utf-8");
        outputFormat.setNewLineAfterDeclaration(false);
        StringWriter stringWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);
        xmlWriter.write(document);
        System.out.println("-----返回信息-----" + stringWriter.toString());
        os.write(stringWriter.toString().getBytes("UTF-8"));
        xmlWriter.close();
        return xmlWriter.toString();
    }

    /**
     * 获取请求方法名
     * @param req
     * @return
     */
    public String getMethod(HttpServletRequest req){
        Map<String, String[]> params = req.getParameterMap();
        String method = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int j = 0; j < values.length; j++) {
                String value = values[j];
                if ("method".equals(key)) {
                    method = value;
                }
            }
        }
        return method;
    }

    /**
     * 处理request中的参数
     * @param req
     * @return
     */
    public String getParam(HttpServletRequest req) {
        StringBuffer data = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            while (null != (line = reader.readLine()))
                data.append(line);
            System.out.println("生成的data:******" + data.toString());
        } catch (IOException e) {
        }
        return data.toString();
    }

    /**
     * 回传的单据是否已被接受
     * @param type 出库or入库
     * @param docNo 单据编号
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public String isConfirm(String type,String docNo) throws SQLException, ClassNotFoundException {
        String result = "";
        StringBuffer sbSql = new StringBuffer("select count(*) from ");
        if("".equals("IN"))
            sbSql.append("WMS_RESULT_IN ");
        else
            sbSql.append("WMS_RESULT_OUT ");
        sbSql.append("where docno='").append(docNo).append("'");
        int count = 0;
        ResultSet countList = search(sbSql.toString(),null);
        while (countList.next()){
            count = Long.valueOf(countList.getString(0)).intValue();
        }
        if(count>0)
            result = "单据:"+docNo+"已回传，请勿重复操作";
        return result;
    }

    /**
     * 转换日期格式 将yyyyMMdd 转为yyyy-MM-dd HH:mm:ss 时分秒默认为00:00:00
     * @param date yyyyMMdd格式的日期
     * @return yyyy-MM-dd HH:mm:ss格式的日期
     */
    public static String paseDate(String date){
        String temp = date;
        if(date !=null&&!"".equals(date)) {
            date = temp.substring(0, 4)+"-";
            date = date + temp.substring(4,6)+"-";
            date = date + temp.substring(6,8);
            date = date + " 00:00:00";
        }
        return date;
    }

}
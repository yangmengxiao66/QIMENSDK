package com.bean;

public class Constants {

    private String appKey = "";
    private String secret = "";
    private String url = "";
    private String customerId = "";
    private String version = "";
    private String wareHouseCode = "";
    private String dbUrl = "";
    private String dbUserName = "";
    private String dbPassWord = "";

    public Constants(String appKey, String secret, String url, String customerId, String version, String wareHouseCode, String dbUrl, String dbUserName, String dbPassWord) {
        this.appKey = appKey;
        this.secret = secret;
        this.url = url;
        this.customerId = customerId;
        this.version = version;
        this.wareHouseCode = wareHouseCode;
        this.dbUrl = dbUrl;
        this.dbUserName = dbUserName;
        this.dbPassWord = dbPassWord;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWareHouseCode() {
        return wareHouseCode;
    }

    public void setWareHouseCode(String wareHouseCode) {
        this.wareHouseCode = wareHouseCode;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassWord() {
        return dbPassWord;
    }

    public void setDbPassWord(String dbPassWord) {
        this.dbPassWord = dbPassWord;
    }
}

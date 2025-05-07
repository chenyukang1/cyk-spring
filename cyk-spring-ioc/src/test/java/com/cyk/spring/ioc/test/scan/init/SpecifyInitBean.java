package com.cyk.spring.ioc.test.scan.init;

public class SpecifyInitBean {

    String appTitle;

    String appVersion;

    public String appName;

    public SpecifyInitBean(String appTitle, String appVersion) {
        this.appTitle = appTitle;
        this.appVersion = appVersion;
    }

    public void init() {
        this.appName = this.appTitle + " / " + this.appVersion;
    }
}

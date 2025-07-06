package com.cyk.spring.web.handler;

/**
 * The class RequestMappingInfo
 *
 * @author yukang.chen
 * @date 2025/7/5
 */
public class RequestMappingInfo {

    private Object bean;

    private Class<?> beanType;

    private HttpMethod httpMethod;

    private String url;

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }
}

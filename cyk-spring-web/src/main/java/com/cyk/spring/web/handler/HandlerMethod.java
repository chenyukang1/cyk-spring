package com.cyk.spring.web.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * The class HandlerMethod
 *
 * @author yukang.chen
 * @date 2025/7/1
 */
public class HandlerMethod {

    private final Object bean;

    private final Class<?> beanType;

    private final Method method;

    private final MethodParameter[] methodParameters;

    public HandlerMethod(Object bean, Class<?> beanType, Method method) {
        this.bean = bean;
        this.beanType = beanType;
        this.method = method;
        this.methodParameters = initMethodParameters();
    }

    public Object getBean() {
        return bean;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public Method getMethod() {
        return method;
    }

    public MethodParameter[] getMethodParameters() {
        return methodParameters;
    }

    private MethodParameter[] initMethodParameters() {
        Parameter[] parameters = method.getParameters();
        MethodParameter[] methodParameterArr = new MethodParameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            methodParameterArr[i] = new MethodParameter(parameter.getName(), i, parameter.getType(), parameter.getAnnotations());
        }
        return methodParameterArr;


    }

    static class MethodParameter {

        private final String parameterName;

        private final int parameterIndex;

        private final Class<?> parameterType;

        private final Annotation[] parameterAnnotations;

        MethodParameter(String parameterName, int parameterIndex, Class<?> parameterType, Annotation[] parameterAnnotations) {
            this.parameterName = parameterName;
            this.parameterIndex = parameterIndex;
            this.parameterType = parameterType;
            this.parameterAnnotations = parameterAnnotations;
        }

        public boolean hasParameterAnnotations() {
            return parameterAnnotations != null && parameterAnnotations.length > 0;
        }

        public String getParameterName() {
            return parameterName;
        }

        public int getParameterIndex() {
            return parameterIndex;
        }

        public Class<?> getParameterType() {
            return parameterType;
        }

        public Annotation[] getParameterAnnotations() {
            return parameterAnnotations;
        }
    }

}

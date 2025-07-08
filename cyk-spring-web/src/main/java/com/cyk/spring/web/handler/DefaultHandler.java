package com.cyk.spring.web.handler;

import com.cyk.spring.ioc.context.ConfigurableApplicationContext;
import com.cyk.spring.ioc.definition.BeanDefinition;
import com.cyk.spring.ioc.utils.ClassUtils;
import com.cyk.spring.ioc.utils.StringUtils;
import com.cyk.spring.web.annotation.Controller;
import com.cyk.spring.web.annotation.RequestMapping;
import com.cyk.spring.web.annotation.RequestParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The class DefaultHandler
 *
 * @author yukang.chen
 * @date 2025/7/5
 */
public class DefaultHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(DefaultHandler.class);

    private final ObjectMapper mapper = new ObjectMapper();

    private ConfigurableApplicationContext context;

    private final MappingRegistry mappingRegistry = new MappingRegistry();

    public void init(ConfigurableApplicationContext context) {
        this.context = context;
        for (String beanName: context.getBeanDefinitionNames()) {
            BeanDefinition definition = context.findBeanDefinition(beanName);
            if (definition != null && isHandler(definition)) {
                detectHandlerMethods(beanName, definition.getBeanClass());
            }
        }
    }

    @Override
    public void doHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = request.getRequestURI();
        HandlerMethod handlerMethod = mappingRegistry.getHandlerMethod(url);
        Object bean = handlerMethod.getBean();
        Method method = handlerMethod.getMethod();
        Object[] args = new Object[handlerMethod.getMethodParameters().length];

        for (HandlerMethod.MethodParameter parameter : handlerMethod.getMethodParameters()) {
            for (Annotation annotation : parameter.getParameterAnnotations()) {
                if (annotation.annotationType() == RequestParam.class) {
                    RequestParam requestParam = (RequestParam) annotation;
                    String paramName = requestParam.value();
                    if (StringUtils.isEmpty(paramName)) {
                        paramName = parameter.getParameterName();
                    }
                    String paramValue = request.getParameter(paramName);
                    if (paramValue != null) {
                        Object arg = ClassUtils.convertToType(paramValue, parameter.getParameterType());
                        args[parameter.getParameterIndex()] = arg;
                    } else {
                        throw new IllegalArgumentException("Required request parameter '" + paramName + "' is not present");
                    }
                }
            }
        }

        Object result = method.invoke(bean, args);
        if (result != null) {
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            mapper.writeValue(writer, result);
            writer.flush();
        }
    }

    private boolean isHandler(BeanDefinition definition) {
        Class<?> beanClass = definition.getBeanClass();
        return ClassUtils.findAnnotation(beanClass, Controller.class) != null;
    }

    private void detectHandlerMethods(String beanName, Class<?> beanClass) {
        Method[] declaredMethods = beanClass.getDeclaredMethods();
        List<Method> defaultMethods = ClassUtils.findDefaultMethodsOnInterfaces(beanClass);
        if (defaultMethods != null) {
            int index = declaredMethods.length;
            declaredMethods = new Method[index + defaultMethods.size()];
            for (int i = index; i < declaredMethods.length; i++) {
                declaredMethods[i] = defaultMethods.get(i - index);
            }
        }

        for (Method method : declaredMethods) {
            if (method.isBridge() || method.isSynthetic() || method.getDeclaringClass() == Object.class) {
                continue;
            }

            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                RequestMappingInfo mappingInfo = createRequestMappingInfo(
                        context.getBean(beanName, beanClass), beanClass, method);
                if (mappingInfo == null) {
                    continue;
                }
                mappingRegistry.register(mappingInfo, method);
            }
        }

        mappingRegistry.registry.forEach((mappingInfo, handlerMethod) -> {
            log.info("Registered handler method: {} for URL: {} with HTTP methods: {}",
                    handlerMethod.getMethod().getName(), mappingInfo.getUrl(), mappingInfo.getHttpMethod());
        });
    }

    private RequestMappingInfo createRequestMappingInfo(Object bean, Class<?> beanClass, Method method) {
        RequestMapping requestMapping = ClassUtils.findAnnotation(method, RequestMapping.class);
        if (requestMapping == null) {
            return null;
        }
        if (StringUtils.isEmpty(requestMapping.value())) {
            throw new IllegalArgumentException("RequestMapping value cannot be empty for method: " + method.getName());
        }
        RequestMappingInfo mappingInfo = new RequestMappingInfo();
        mappingInfo.setBean(bean);
        mappingInfo.setBeanType(beanClass);
        mappingInfo.setHttpMethod(requestMapping.method());
        mappingInfo.setUrl(requestMapping.value());
        return mappingInfo;
    }

    class MappingRegistry {

        private final Map<RequestMappingInfo, HandlerMethod> registry = new HashMap<>();

        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        public void register(RequestMappingInfo mappingInfo, Method method) {
            readWriteLock.writeLock().lock();
            try {
                HandlerMethod handlerMethod = new HandlerMethod(mappingInfo.getBean(), mappingInfo.getBeanType(), method);
                registry.put(mappingInfo, handlerMethod);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }

        public HandlerMethod getHandlerMethod(String url) {
            if (StringUtils.isEmpty(url)) {
                throw new IllegalArgumentException("URL cannot be empty");
            }
            readWriteLock.readLock().lock();
            try {
                // Logic to retrieve handler method based on request mapping info
                var optional = registry.entrySet().stream()
                        .filter(entry -> url.equals(entry.getKey().getUrl()))
                        .findAny();
                // No handler found for the given URL
                return optional.map(Map.Entry::getValue).orElse(null);
            } finally {
                readWriteLock.readLock().unlock();
            }
        }
    }
}

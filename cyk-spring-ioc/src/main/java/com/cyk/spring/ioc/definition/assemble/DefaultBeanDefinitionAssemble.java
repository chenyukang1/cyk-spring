package com.cyk.spring.ioc.definition.assemble;

import com.cyk.spring.ioc.annotation.Bean;
import com.cyk.spring.ioc.annotation.Component;
import com.cyk.spring.ioc.annotation.Order;
import com.cyk.spring.ioc.annotation.Primary;
import com.cyk.spring.ioc.exception.BeanDefinitionException;
import com.cyk.spring.ioc.utils.ClassUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * The class DefaultBeanDefinitionConverter.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/3
 */
public class DefaultBeanDefinitionAssemble extends AbstractBeanDefinitionAssemble {

    @Override
    public String getBeanName(Class<?> clazz) {
        String name = null;
        // 查找@Component
        Component component = clazz.getAnnotation(Component.class);
        if (component != null) {
            name = component.value();
        } else {
            // 未找到@Component，继续在其他注解中查找@Component
            for (Annotation annotation : clazz.getAnnotations()) {
                if (ClassUtils.findAnnotation(annotation.annotationType(), Component.class) != null) {
                    try {
                        name = (String) annotation.annotationType().getMethod("value").invoke(annotation);
                    } catch (ReflectiveOperationException e) {
                        throw new BeanDefinitionException("Cannot get annotation value.", e);
                    }
                }
            }
        }
        if (name == null || name.isEmpty()) {
            // default name: "HelloWorld" => "helloWorld"
            name = clazz.getSimpleName();
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        return name;
    }

    @Override
    public Constructor<?> getConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            constructors = clazz.getDeclaredConstructors();
            if (constructors.length != 1) {
                throw new BeanDefinitionException("More than one constructor found in class " + clazz.getName() + ".");
            }
        }
        if (constructors.length != 1) {
            throw new BeanDefinitionException("More than one public constructor found in class " + clazz.getName() + ".");
        }
        return constructors[0];
    }

    @Override
    protected int getOrder(Class<?> clazz) {
        Order order = clazz.getAnnotation(Order.class);
        return order == null ? Integer.MAX_VALUE : order.value();
    }

    @Override
    protected boolean IsPrimary(Class<?> clazz) {
        return clazz.isAnnotationPresent(Primary.class);
    }

    @Override
    protected Method getInitMethod(Class<?> clazz) {
        return ClassUtils.findAnnotationMethod(clazz, PostConstruct.class);
    }

    @Override
    protected Method getDestroyMethod(Class<?> clazz) {
        return ClassUtils.findAnnotationMethod(clazz, PreDestroy.class);
    }

    @Override
    protected String getBeanName(Method method) {
        Bean bean = method.getAnnotation(Bean.class);
        String name = bean.value();
        if (name.isEmpty()) {
            name = method.getName();
        }
        return name;
    }

    @Override
    protected int getOrder(Method method) {
        Order order = method.getAnnotation(Order.class);
        return order == null ? Integer.MAX_VALUE : order.value();
    }

    @Override
    protected boolean IsPrimary(Method method) {
        return method.isAnnotationPresent(Primary.class);
    }

    @Override
    protected String getInitMethodName(Bean bean) {
        return bean.initMethod().isEmpty() ? null : bean.initMethod();
    }

    @Override
    protected String getDestroyMethodName(Bean bean) {
        return bean.destroyMethod().isEmpty() ? null : bean.destroyMethod();
    }
}

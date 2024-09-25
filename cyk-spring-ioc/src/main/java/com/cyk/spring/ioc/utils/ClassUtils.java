package com.cyk.spring.ioc.utils;

import com.cyk.spring.ioc.context.exception.BeanDefinitionException;
import jakarta.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class ClassUtils.
 *
 * @author chenyukang
 * @email chen.yukang @qq.com
 * @date 2024 /8/1
 */
public class ClassUtils {

    /**
     * Gets context class loader.
     *
     * @param caller the caller
     * @return the context class loader
     */
    public static ClassLoader getContextClassLoader(Class<?> caller) {
        ClassLoader cl;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = caller.getClassLoader();
        }
        return cl;
    }

    /**
     * 递归查找Annotation
     * <br/>
     * 示例：Annotation A可以直接标注在Class定义:
     * <code>
     *
     * @param <A>       the type parameter
     * @param target    the target
     * @param annoClass the anno class
     * @return the a
     * @A public  class Hello {} </code> <br/> 或者Annotation B标注了A，Class标注了B: <br/> <code>
     * @A public  @interface B {}
     * @B public  class Hello {} </code>
     */
    public static <A extends Annotation> A findAnnotation(Class<?> target, Class<A> annoClass) {
        A a = target.getAnnotation(annoClass);
        for (Annotation anno : target.getAnnotations()) {
            Class<? extends Annotation> annoType = anno.annotationType();
            if (!"java.lang.annotation".equals(annoType.getPackage().getName())) {
                A found = findAnnotation(annoType, annoClass);
                if (found != null) {
                    if (a != null) {
                        throw new BeanDefinitionException("Duplicate @" + annoClass.getSimpleName() + " found on class " + target.getSimpleName());
                    }
                    a = found;
                }
            }
        }
        return a;
    }

    /**
     * Gets annotation.
     *
     * @param <A>         the type parameter
     * @param annotations the annotations
     * @param annoClass   the anno class
     * @return the annotation
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(Annotation[] annotations, Class<A> annoClass) {
        for (Annotation annotation : annotations) {
            if (annoClass.isInstance(annotation)) {
                return (A) annotation;
            }
        }
        return null;
    }

    /**
     * Find annotation method.
     *
     * @param clazz     the clazz
     * @param annoClass the anno class
     * @return the method
     */
    @Nullable
    public static Method findAnnotationMethod(Class<?> clazz, Class<? extends Annotation> annoClass) {
        // try get declared method
        List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annoClass))
                .peek(method -> {
                    if (method.getParameterCount() != 0) {
                        throw new BeanDefinitionException(
                                String.format("Method '%s' with @%s must not have argument: %s", method.getName(),
                                        annoClass.getSimpleName(), clazz.getName()));
                    }
                })
                .collect(Collectors.toList());
        if (methods.isEmpty()) {
            return null;
        }
        if (methods.size() == 1) {
            return methods.get(0);
        }
        throw new BeanDefinitionException(String.format("Multiple methods with @%s found in class: %s",
                annoClass.getSimpleName(), clazz.getName()));
    }
}

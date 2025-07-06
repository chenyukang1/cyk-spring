package com.cyk.spring.ioc.utils;

import com.cyk.spring.ioc.exception.BeanDefinitionException;
import jakarta.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

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

    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        Class<?> current = clazz;
        while (current != null) {
            Class<?>[] ifcs = clazz.getInterfaces();
            interfaces.addAll(Arrays.asList(ifcs));
            current = current.getSuperclass();
        }
        return interfaces;
    }

    public static <A> A convertToType(Object obj, Class<A> targetType) {
        if (obj == null) {
            return null;
        }
        if (targetType.isInstance(obj)) {
            return targetType.cast(obj);
        }
        if (targetType.isPrimitive()) {
            if (targetType == int.class) {
                return targetType.cast(Integer.parseInt(obj.toString()));
            } else if (targetType == long.class) {
                return targetType.cast(Long.parseLong(obj.toString()));
            } else if (targetType == double.class) {
                return targetType.cast(Double.parseDouble(obj.toString()));
            } else if (targetType == boolean.class) {
                return targetType.cast(Boolean.parseBoolean(obj.toString()));
            } else if (targetType == float.class) {
                return targetType.cast(Float.parseFloat(obj.toString()));
            } else if (targetType == short.class) {
                return targetType.cast(Short.parseShort(obj.toString()));
            } else if (targetType == byte.class) {
                return targetType.cast(Byte.parseByte(obj.toString()));
            } else if (targetType == char.class) {
                return targetType.cast(obj.toString().charAt(0));
            }
        }
        throw new IllegalArgumentException("Cannot convert " + obj.getClass() + " to " + targetType);
    }

    public static List<Method> findDefaultMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method method : ifc.getMethods()) {
                if (method.isDefault()) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(method);
                }
            }
        }
        return result;
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
        return doFindAnnotation(target.getAnnotation(annoClass), target.getAnnotations(), annoClass);
    }

    public static <A extends Annotation> A findAnnotation(Method method, Class<A> annoClass) {
        return doFindAnnotation(method.getAnnotation(annoClass), method.getAnnotations(), annoClass);
    }

    private static <A extends Annotation> A doFindAnnotation(A a, Annotation[] annotations, Class<A> annoClass) {
        for (Annotation anno : annotations) {
            Class<? extends Annotation> annoType = anno.annotationType();
            if (!"java.lang.annotation".equals(annoType.getPackage().getName())) {
                A found = findAnnotation(annoType, annoClass);
                if (found != null) {
                    if (a != null) {
                        throw new BeanDefinitionException("Duplicate @" + annoClass.getSimpleName() + " found on " + a);
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
                .toList();
        if (methods.isEmpty()) {
            return null;
        }
        if (methods.size() == 1) {
            return methods.get(0);
        }
        throw new BeanDefinitionException(String.format("Multiple methods with @%s found in class: %s",
                annoClass.getSimpleName(), clazz.getName()));
    }

    public static Method getNamedMethod(Class<?> clazz, String methodName) {
        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new BeanDefinitionException(
                    String.format("Method '%s' not found in class: %s", methodName, clazz.getName()));
        }
    }
}

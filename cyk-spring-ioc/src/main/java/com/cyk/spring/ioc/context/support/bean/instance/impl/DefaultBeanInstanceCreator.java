package com.cyk.spring.ioc.context.support.bean.instance.impl;

import com.cyk.spring.ioc.context.annotation.Autowired;
import com.cyk.spring.ioc.context.annotation.Configuration;
import com.cyk.spring.ioc.context.exception.BeanCreationException;
import com.cyk.spring.ioc.context.model.BeanDefinition;
import com.cyk.spring.ioc.context.support.bean.instance.IBeanInstanceCreate;
import com.cyk.spring.ioc.utils.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The class DefaultBeanInstanceCreator.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/9/7
 */
public class DefaultBeanInstanceCreator implements IBeanInstanceCreate {

    private final Set<String> creatingBeanNames = new HashSet<>();

    @Override
    public void createBean(Map<String, BeanDefinition> beanDefinitions) {

        // 1、创建@Configuration标注的工厂bean
        beanDefinitions.values().stream()
                .filter(beanDefinition -> ClassUtils.findAnnotation(beanDefinition.getBeanClass(), Configuration.class) != null)
                .forEach(this::createEarlyBean);

    }

    private void createEarlyBean(BeanDefinition def) {
        if (!this.creatingBeanNames.add(def.getBeanName())) {
            throw new BeanCreationException(String.format(
                    "Circular dependency detected when create bean '%s'", def.getBeanName()));
        }

        // 1、创建参数
        Executable executable = def.getConstructor() != null ? def.getConstructor() :
                def.getFactoryMethod();
        assert executable != null;

        final Parameter[] parameters = executable.getParameters();
        final Annotation[][] parameterAnnotationArr = executable.getParameterAnnotations();
        Object[] args = new Object[parameters.length];
        boolean isConfiguration = ClassUtils.findAnnotation(def.getBeanClass(), Configuration.class) != null;
        for (int i = 0; i < parameters.length; i++) {
            final Parameter param = parameters[i];
            final Annotation[] parameterAnnotations = parameterAnnotationArr[i];
            final Autowired autowired = ClassUtils.getAnnotation(parameterAnnotations, Autowired.class);

            if (autowired == null) continue;

            // @Configuration类型的Bean是工厂，不允许使用@Autowired创建:
            if (isConfiguration && autowired != null) {
                throw new BeanCreationException(
                        String.format("Cannot specify @Autowired when create @Configuration bean '%s': %s.", def.getBeanName(), def.getBeanClass().getName()));
            }


//            assert autowired != null;
//            // 参数类型:
//            final Class<?> type = param.getType();
//            // 参数是@Autowired:
//            String name = autowired.name();
//            boolean required = autowired.value();
//            // 依赖的BeanDefinition:
//            BeanDefinition dependsOnDef = name.isEmpty() ? findBeanDefinition(type) : findBeanDefinition(name, type);
//            // 检测required==true?
//            if (required && dependsOnDef == null) {
//                throw new BeanCreationException(String.format("Missing autowired bean with type '%s' when create bean '%s': %s.", type.getName(),
//                        def.getName(), def.getBeanClass().getName()));
//            }
//            if (dependsOnDef != null) {
//                // 获取依赖Bean:
//                Object autowiredBeanInstance = dependsOnDef.getInstance();
//                if (autowiredBeanInstance == null && !isConfiguration && !isBeanPostProcessor) {
//                    // 当前依赖Bean尚未初始化，递归调用初始化该依赖Bean:
//                    autowiredBeanInstance = createBeanAsEarlySingleton(dependsOnDef);
//                }
//                args[i] = autowiredBeanInstance;
//            } else {
//                args[i] = null;
//
//            }
//        }
//
//        // 创建Bean实例:
//        Object instance = null;
//        if (def.getFactoryName() == null) {
//            // 用构造方法创建:
//            try {
//                instance = def.getConstructor().newInstance(args);
//            } catch (Exception e) {
//                throw new BeanCreationException(String.format("Exception when create bean '%s': %s", def.getName(), def.getBeanClass().getName()), e);
//            }
//        } else {
//            // 用@Bean方法创建:
//            Object configInstance = getBean(def.getFactoryName());
//            try {
//                instance = def.getFactoryMethod().invoke(configInstance, args);
//            } catch (Exception e) {
//                throw new BeanCreationException(String.format("Exception when create bean '%s': %s", def.getName(), def.getBeanClass().getName()), e);
//            }
//        }
//        def.setInstance(instance);
//
//        // 调用BeanPostProcessor处理Bean:
//        for (BeanPostProcessor processor : beanPostProcessors) {
//            Object processed = processor.postProcessBeforeInitialization(def.getInstance(), def.getName());
//            if (processed == null) {
//                throw new BeanCreationException(String.format("PostBeanProcessor returns null when process bean '%s' by %s", def.getName(), processor));
//            }
//            // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用:
//            if (def.getInstance() != processed) {
//                logger.atDebug().log("Bean '{}' was replaced by post processor {}.", def.getName(), processor.getClass().getName());
//                def.setInstance(processed);
//            }
//        }
//        return def.getInstance();
        }
    }
}

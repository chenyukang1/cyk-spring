package com.cyk.spring.ioc.definition.handle;

import com.cyk.spring.ioc.annotation.Component;
import com.cyk.spring.ioc.annotation.ComponentScan;
import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Import;
import com.cyk.spring.ioc.exception.BeanCreationException;
import com.cyk.spring.ioc.definition.BeanDefinition;
import com.cyk.spring.ioc.definition.IBeanDefinitionAssemble;
import com.cyk.spring.ioc.definition.IBeanDefinitionHandle;
import com.cyk.spring.ioc.definition.assemble.DefaultBeanDefinitionAssemble;
import com.cyk.spring.ioc.io.ResourceResolver;
import com.cyk.spring.ioc.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The class DefaultBeanDefinitionHandler.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/4
 */
public class DefaultBeanDefinitionHandle implements IBeanDefinitionHandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBeanDefinitionHandle.class);

    private final IBeanDefinitionAssemble defaultBeanDefinitionAssemble;

    public DefaultBeanDefinitionHandle() {
        this.defaultBeanDefinitionAssemble = new DefaultBeanDefinitionAssemble();
    }

    @Override
    public Set<String> scanForClassNames(Class<?> configClass) {
        // 1.获取要扫描的package名称
        ComponentScan componentScan = ClassUtils.findAnnotation(configClass, ComponentScan.class);
        final String[] scanPackages = componentScan == null || componentScan.value().length == 0 ?
                new String[] { configClass.getPackage().getName() } : componentScan.value();
        LOGGER.info("扫描包下的所有Component: {}", Arrays.toString(scanPackages));

        Set<String> classNames = new HashSet<>();
        for (String scanPackage : scanPackages) {
            // 1.1.扫描package
            LOGGER.debug("扫描包下的所有Component: {}", scanPackage);
            ResourceResolver resourceResolver = new ResourceResolver(scanPackage);
            classNames.addAll(resourceResolver.scan());
        }

        // 2.查找@Import(Xyz.class)
        Import importConfig = configClass.getAnnotation(Import.class);
        if (importConfig != null) {
            for (Class<?> importConfigClass : importConfig.value()) {
                String importClassName = importConfigClass.getName();
                if (classNames.contains(importClassName)) {
                    LOGGER.warn("忽略import: {}, 因为它已经被扫描过", importClassName);
                } else {
                    LOGGER.debug("通过import找到类: {}", importClassName);
                    classNames.add(importClassName);
                }
            }
        }
        return classNames;
    }

    @Override
    public Map<String, BeanDefinition> createBeanDefinitions(Set<String> beanClassNames) {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
        for (String beanClassName : beanClassNames) {
            // 1.加载类，使用ClassLoader加载不需要做链接和初始化
            Class<?> loadedClass;
            try {
                loadedClass = ClassUtils.getContextClassLoader(getClass()).loadClass(beanClassName);
            } catch (ClassNotFoundException e) {
                throw new BeanCreationException("Bean " + beanClassName + " 创建失败", e);
            }

            // 2.只支持加载Class类型
            if (loadedClass.isAnnotation() || loadedClass.isInterface() || loadedClass.isEnum()) continue;

            // 3.排除abstract/private类
            int modifiers = loadedClass.getModifiers();
            if (Modifier.isAbstract(modifiers) || Modifier.isPrivate(modifiers)) continue;

            // 4.是否标注@Component
            Component component = ClassUtils.findAnnotation(loadedClass, Component.class);
            if (component != null) {
                defaultBeanDefinitionAssemble.assembleBean(loadedClass, beanDefinitions);
            }

            // 5.是否标注@Configuration
            Configuration configuration = ClassUtils.findAnnotation(loadedClass, Configuration.class);
            if (configuration != null) {
                defaultBeanDefinitionAssemble.assembleFactoryBean(loadedClass, beanDefinitions);
            }
        }

        return beanDefinitions;
    }
}

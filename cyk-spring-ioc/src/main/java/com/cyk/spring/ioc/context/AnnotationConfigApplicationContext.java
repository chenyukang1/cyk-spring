package com.cyk.spring.ioc.context;

import com.cyk.spring.common.utils.StringUtils;
import com.cyk.spring.ioc.annotation.Autowired;
import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Value;
import com.cyk.spring.ioc.exception.BeanCreationException;
import com.cyk.spring.ioc.exception.BeanNotOfRequiredTypeException;
import com.cyk.spring.ioc.exception.NoSuchBeanDefinitionException;
import com.cyk.spring.ioc.exception.NoUniqueBeanDefinitionException;
import com.cyk.spring.ioc.definition.BeanDefinition;
import com.cyk.spring.ioc.definition.IBeanDefinitionHandle;
import com.cyk.spring.ioc.definition.handle.DefaultBeanDefinitionHandle;
import com.cyk.spring.ioc.io.PropertyResolver;
import com.cyk.spring.ioc.utils.ClassUtils;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The class AnnotationConfigApplicationContext.
 *
 * @author chenyukang
 * @email chen.yukang@qq.com
 * @date 2024/8/3
 */
public class AnnotationConfigApplicationContext implements ConfigurableApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationConfigApplicationContext.class);

    protected final IBeanDefinitionHandle beanDefinitionHandler;
    protected final PropertyResolver propertyResolver;
    protected Map<String, BeanDefinition> beanDefinitions;
    private final Set<String> creatingBeanNames = new HashSet<>();

    public AnnotationConfigApplicationContext(PropertyResolver propertyResolver, Class<?> configClass) {
        this(new DefaultBeanDefinitionHandle(), propertyResolver, configClass);
    }

    public AnnotationConfigApplicationContext(IBeanDefinitionHandle beanDefinitionHandler,
                                              PropertyResolver propertyResolver,
                                              Class<?> configClass) {
        this.beanDefinitionHandler = beanDefinitionHandler;
        this.propertyResolver = propertyResolver;

        // init
        init(configClass);

        // refresh
        refresh(beanDefinitions);
    }

    @Override
    public List<BeanDefinition> findBeanDefinitions(Class<?> type) {
        return beanDefinitions.values().stream()
                .filter(definition -> definition.getBeanClass().isAssignableFrom(type))
                .sorted()
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(Class<?> type) {
        List<BeanDefinition> beanDefinitions = findBeanDefinitions(type);
        if (beanDefinitions.isEmpty()) {
            return null;
        }
        if (beanDefinitions.size() == 1) {
            return beanDefinitions.get(0);
        }
        List<BeanDefinition> primaries = beanDefinitions.stream().filter(BeanDefinition::isPrimary).toList();
        if (primaries.size() > 1) {
            throw new NoUniqueBeanDefinitionException(String.format(
                    "Multiple bean with type '%s' found, and multiple @Primary specified.", type.getName()));
        }
        if (primaries.isEmpty()) {
            throw new NoUniqueBeanDefinitionException(String.format(
                    "Multiple bean with type '%s' found, but no @Primary specified.", type.getName()));
        }
        return primaries.get(0);
    }

    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(String name) {
        return beanDefinitions.get(name);
    }

    @Nullable
    @Override
    public BeanDefinition findBeanDefinition(String name, Class<?> requiredType) {
        BeanDefinition beanDefinition = findBeanDefinition(name);
        if (beanDefinition == null) {
            return null;
        }
        if (!beanDefinition.getBeanClass().isAssignableFrom(requiredType)) {
            throw new BeanNotOfRequiredTypeException(String.format(
                    "Autowire required type '%s' but bean '%s' has actual type '%s'.",
                    requiredType.getName(), name, beanDefinition.getBeanClass().getName()));
        }
        return beanDefinition;
    }

    @Override
    public boolean containsBean(String name) {
        return beanDefinitions.containsKey(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        BeanDefinition beanDefinition = beanDefinitions.get(name);
        if (beanDefinition == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with name '%s'.", name));
        }
        return (T) beanDefinition.getRequiredInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name, Class<T> requiredType) {
        BeanDefinition beanDefinition = findBeanDefinition(name, requiredType);
        if (beanDefinition == null) {
            return null;
        }
        if (!requiredType.isAssignableFrom(beanDefinition.getBeanClass())) {
            throw new BeanNotOfRequiredTypeException(String.format(
                    "Autowire required type '%s' but bean '%s' has actual type '%s'.",
                    requiredType.getName(), name, beanDefinition.getBeanClass().getName()));
        }
        return (T) beanDefinition.getRequiredInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        BeanDefinition beanDefinition = findBeanDefinition(requiredType);
        if (beanDefinition == null) {
            throw new NoSuchBeanDefinitionException(String.format("No bean defined with type '%s'.", requiredType));
        }
        return (T) beanDefinition.getRequiredInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getBeans(Class<T> requiredType) {
        return findBeanDefinitions(requiredType).stream()
                .map(beanDefinition -> (T) beanDefinition.getRequiredInstance())
                .collect(Collectors.toList());
    }

    @Override
    public Object createBeanAsEarlySingleton(BeanDefinition definition) {
        if (definition.getInstance() == null && !creatingBeanNames.add(definition.getBeanName())) {
            throw new BeanCreationException(String.format(
                    "Circular dependency detected when create bean '%s'", definition.getBeanName()));
        }

        // 1、实例化的两种方式，工厂bean方法和普通的构造方法
        Executable executable = StringUtils.isEmpty(definition.getFactoryBeanName()) ?
                definition.getConstructor() : definition.getFactoryMethod();
        assert executable != null;

        // 2、初始化参数
        final Parameter[] parameters = executable.getParameters();
        final Annotation[][] parameterAnnotationArr = executable.getParameterAnnotations();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            // 构造方法或bean方法参数
            final Parameter parameter = parameters[i];
            // 参数类型
            final Class<?> type = parameter.getType();
            // 参数注释
            final Annotation[] parameterAnnotations = parameterAnnotationArr[i];
            final Autowired autowired = ClassUtils.getAnnotation(parameterAnnotations, Autowired.class);
            final Value value = ClassUtils.getAnnotation(parameterAnnotations, Value.class);

            // 参数不能@Value和@Autowired都有
            if (value != null && autowired != null) {
                throw new BeanCreationException(
                        String.format("Cannot specify both @Autowired and @Value when create bean '%s': %s.",
                                definition.getBeanName(), definition.getBeanClass().getName()));
            }
            if (value != null) {
                args[i] = propertyResolver.getRequiredProperty(value.value(), type);
            } else if (autowired != null) {
                String name = autowired.name();
                boolean required = autowired.value();
                // 依赖的BeanDefinition
                BeanDefinition dependsOnDef = name.isEmpty() ? findBeanDefinition(type) : findBeanDefinition(name, type);
                // 检测required==true
                if (required && dependsOnDef == null) {
                    throw new BeanCreationException(String.format(
                            "Missing autowired bean with type '%s' when create bean '%s': %s.",
                            type.getName(), definition.getBeanName(), definition.getBeanClass().getName()));
                }
                if (dependsOnDef != null) {
                    // 获取依赖Bean
                    Object autowiredBeanInstance = dependsOnDef.getInstance();
                    if (autowiredBeanInstance == null) {
                        // 当前依赖Bean尚未初始化，递归调用初始化该依赖Bean
                        autowiredBeanInstance = createBeanAsEarlySingleton(dependsOnDef);
                    }
                    args[i] = autowiredBeanInstance;
                } else {
                    args[i] = null;
                }
            } else {
                // 默认是强依赖
                BeanDefinition dependsOnDef = findBeanDefinition(type);
                if (dependsOnDef == null) {
                    throw new BeanCreationException(String.format(
                            "Missing depend bean with type '%s' when create bean '%s': %s.",
                            type.getName(), definition.getBeanName(), definition.getBeanClass().getName()));
                }
                Object autowiredBeanInstance = dependsOnDef.getInstance();
                if (autowiredBeanInstance == null) {
                    // 当前依赖Bean尚未初始化，递归调用初始化该依赖Bean
                    autowiredBeanInstance = createBeanAsEarlySingleton(dependsOnDef);
                }
                args[i] = autowiredBeanInstance;
            }
        }

        // 3、创建Bean实例
        Object instance;
        if (definition.getFactoryBeanName() == null) {
            // 3.1、用构造方法创建:
            try {
                instance = definition.getConstructor().newInstance(args);
            } catch (Exception e) {
                throw new BeanCreationException(String.format(
                        "Exception when create bean '%s': %s",
                        definition.getBeanName(), definition.getBeanClass().getName()), e);
            }
        } else {
            // 3.2、用@Bean方法创建:
            Object configInstance = getBean(definition.getFactoryBeanName());
            try {
                instance = definition.getFactoryMethod().invoke(configInstance, args);
            } catch (Exception e) {
                throw new BeanCreationException(String.format(
                        "Exception when create bean '%s': %s",
                        definition.getBeanName(), definition.getBeanClass().getName()), e);
            }
        }
        definition.setInstance(instance);
        return instance;
//
//        // 调用BeanPostProcessor处理Bean:
//        for (BeanPostProcessor processor : beanPostProcessors) {
//            Object processed = processor.postProcessBeforeInitialization(definition.getInstance(), definition.getName());
//            if (processed == null) {
//                throw new BeanCreationException(String.format("PostBeanProcessor returns null when process bean '%s' by %s", definition.getName(), processor));
//            }
//            // 如果一个BeanPostProcessor替换了原始Bean，则更新Bean的引用:
//            if (definition.getInstance() != processed) {
//                logger.atDebug().log("Bean '{}' was replaced by post processor {}.", definition.getName(), processor.getClass().getName());
//                definition.setInstance(processed);
//            }
//        }
//        return definition.getInstance();
    }

    @Override
    public void close() {

    }

    private void init(Class<?> configClass) {
        // 1.扫描获取所有Bean的Class类型
        Set<String> beanClassNames = beanDefinitionHandler.scanForClassNames(configClass);

        // 2.创建Bean的定义
        this.beanDefinitions = beanDefinitionHandler.createBeanDefinitions(beanClassNames);
    }

    private void refresh(Map<String, BeanDefinition> beanDefinitions) {
        // 1、创建@Configuration标注的工厂bean
        beanDefinitions.values().stream()
                .filter(beanDefinition -> ClassUtils.findAnnotation(beanDefinition.getBeanClass(), Configuration.class) != null)
                .forEach(this::createBeanAsEarlySingleton);
    }

}

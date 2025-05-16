package com.cyk.spring.ioc.context;

import com.cyk.spring.common.utils.StringUtils;
import com.cyk.spring.ioc.annotation.Autowired;
import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Value;
import com.cyk.spring.ioc.definition.BeanDefinition;
import com.cyk.spring.ioc.definition.IBeanDefinitionHandle;
import com.cyk.spring.ioc.definition.handle.DefaultBeanDefinitionHandle;
import com.cyk.spring.ioc.exception.*;
import com.cyk.spring.ioc.io.PropertyResolver;
import com.cyk.spring.ioc.utils.ClassUtils;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
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
    protected List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
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
        refresh();
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
        if (definition.getInstance() != null) {
            return definition.getInstance();
        }
        if (!creatingBeanNames.add(definition.getBeanName())) {
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
                    logger.debug("{} found proxy instance {} when constructor", definition.getBeanName(), autowiredBeanInstance);
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
                logger.debug("{} found proxy instance {} when constructor", definition.getBeanName(), autowiredBeanInstance);
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

        // 4、调用BeanPostProcessor处理Bean
        for (BeanPostProcessor processor : beanPostProcessors) {
            Object proxy = processor.postProcessBeforeInitialization(instance, definition.getBeanName());
            assert proxy != null;
            definition.setInstance(proxy);
        }

        return definition.getInstance();
    }

    @Override
    public void close() {
        beanDefinitions.values().forEach(beanDefinition ->
                callMethod(beanDefinition.getInstance(), beanDefinition.getDestroyMethod(),
                        beanDefinition.getDestroyMethodName()));
        beanDefinitions.clear();
        logger.info("{} closed", this);
    }

    /**
     * findBean与getBean类似，但是返回null，不抛异常
     *
     * @param <T>          the type parameter
     * @param name         the name
     * @param requiredType the required type
     * @return the t
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T> T findBean(String name, Class<T> requiredType) {
        BeanDefinition def = findBeanDefinition(name, requiredType);
        if (def == null) {
            return null;
        }
        return (T) def.getRequiredInstance();
    }

    /**
     * findBean与getBean类似，但是返回null，不抛异常
     *
     * @param <T>          the type parameter
     * @param requiredType the required type
     * @return the t
     */
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T> T findBean(Class<T> requiredType) {
        BeanDefinition def = findBeanDefinition(requiredType);
        if (def == null) {
            return null;
        }
        return (T) def.getRequiredInstance();
    }

    private void init(Class<?> configClass) {
        // 1.扫描获取所有Bean的Class类型
        Set<String> beanClassNames = beanDefinitionHandler.scanForClassNames(configClass);

        // 2.创建Bean的定义
        this.beanDefinitions = beanDefinitionHandler.createBeanDefinitions(beanClassNames);
    }

    private void refresh() {
        // 1、创建@Configuration标注的工厂bean
        beanDefinitions.values().stream()
                .filter(beanDefinition -> ClassUtils.findAnnotation(beanDefinition.getBeanClass(), Configuration.class) != null)
                .sorted()
                .forEach(this::createBeanAsEarlySingleton);

        // 2、创建BeanPostProcessor bean
        beanPostProcessors.addAll(beanDefinitions.values().stream()
                .filter(beanDefinition -> BeanPostProcessor.class.isAssignableFrom(beanDefinition.getBeanClass()))
                .sorted()
                .map(beanDefinition -> (BeanPostProcessor) createBeanAsEarlySingleton(beanDefinition))
                .toList());

        // 3、创建普通bean
        beanDefinitions.values().stream()
                .filter(beanDefinition -> beanDefinition.getInstance() == null)
                .sorted()
                .forEach(this::createBeanAsEarlySingleton);

        // 4、注入setter/属性值
        beanDefinitions.values().forEach(this::injectBean);

        // 5、调用init方法:
        beanDefinitions.values().forEach(this::initBean);
    }

    void injectBean(BeanDefinition beanDefinition) {
        try {
            Object instance = getOriginInstance(beanDefinition);
            injectProperties(beanDefinition, beanDefinition.getBeanClass(), instance);
        } catch (ReflectiveOperationException e) {
            throw new BeanCreationException(e);
        }
    }

    void initBean(BeanDefinition beanDefinition) {
        Object instance = getOriginInstance(beanDefinition);
        // 调用init方法
        callMethod(instance, beanDefinition.getInitMethod(), beanDefinition.getInitMethodName());
        for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            Object proxied = beanPostProcessor.postProcessAfterInitialization(beanDefinition.getInstance(), beanDefinition.getBeanName());
            beanDefinition.setInstance(proxied);
        }
    }

    void injectProperties(BeanDefinition beanDefinition, Class<?> clazz, Object instance) throws ReflectiveOperationException {
        // 在当前类查找Field和Method并注入
        for (Field field : clazz.getDeclaredFields()) {
            tryInjectProperties(beanDefinition, clazz, instance, field);
        }
        for (Method method : clazz.getDeclaredMethods()) {
            tryInjectProperties(beanDefinition, clazz, instance, method);
        }
        // 在父类查找Field和Method并注入:
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            injectProperties(beanDefinition, superClazz, instance);
        }
    }

    void tryInjectProperties(BeanDefinition beanDefinition, Class<?> clazz, Object instance,
                             AccessibleObject meta) throws ReflectiveOperationException {
        Value value = meta.getAnnotation(Value.class);
        Autowired autowired = meta.getAnnotation(Autowired.class);
        if (value == null && autowired == null) {
            return;
        }

        Field field = null;
        Method method = null;
        if (meta instanceof Field f) {
            checkFieldOrMethod(f);
            f.setAccessible(true);
            field = f;
        }
        if (meta instanceof Method m) {
            checkFieldOrMethod(m);
            if (m.getParameters().length != 1) {
                throw new BeanDefinitionException(
                        String.format("Cannot inject a non-setter method %s for instance '%s': %s",
                                m.getName(), beanDefinition.getBeanName(), beanDefinition.getBeanClass().getName()));
            }
            m.setAccessible(true);
            method = m;
        }

        String accessibleName = field != null ? field.getName() : method.getName();
        Class<?> accessibleType = field != null ? field.getType() : method.getParameterTypes()[0];

        if (value != null && autowired != null) {
            throw new BeanCreationException(
                    String.format("Cannot specify both @Autowired and @Value when inject %s.%s for instance '%s': %s",
                    clazz.getSimpleName(), accessibleName, beanDefinition.getBeanName(), beanDefinition.getBeanClass().getName()));
        }

        // @Value注入:
        if (value != null) {
            Object propValue = this.propertyResolver.getRequiredProperty(value.value(), accessibleType);
            if (field != null) {
                field.set(instance, propValue);
            }
            if (method != null) {
                method.invoke(instance, propValue);
            }
        }

        // @Autowired注入:
        if (autowired != null) {
            String name = autowired.name();
            boolean required = autowired.value();
            Object depends = name.isEmpty() ? findBean(accessibleType) : findBean(name, accessibleType);
            if (required && depends == null) {
                throw new UnsatisfiedDependencyException(
                        String.format("Dependency instance not found when inject %s.%s for instance '%s': %s",
                                clazz.getSimpleName(), accessibleName, beanDefinition.getBeanName(), beanDefinition.getBeanClass().getName()));
            }
            if (depends != null) {
                logger.debug("{} found proxy instance {} when inject properties", beanDefinition.getBeanName(), depends);
                if (field != null) {
                    field.set(instance, depends);
                }
                if (method != null) {
                    method.invoke(instance, depends);
                }
            }
        }
    }

    private Object getOriginInstance(BeanDefinition beanDefinition) {
        // 对代理类的属性注入需要找到原始类
        Object instance = beanDefinition.getInstance();
        // 多次代理的类由逆序还原成原始类 A -> proxy B -> proxy C
        List<BeanPostProcessor> postProcessors = new ArrayList<>(beanPostProcessors);
        for (int i = postProcessors.size() - 1; i >= 0; i--) {
            Object origin = postProcessors.get(i).postProcessOnSetProperty(instance, beanDefinition.getBeanName());
            if (origin != instance) {
                instance = origin;
            }
        }
        return instance;
    }

    void checkFieldOrMethod(Member m) {
        int mod = m.getModifiers();
        if (Modifier.isStatic(mod)) {
            throw new BeanDefinitionException("Cannot inject static field: " + m);
        }
        if (Modifier.isFinal(mod)) {
            if (m instanceof Field field) {
                throw new BeanDefinitionException("Cannot inject final field: " + field);
            }
            if (m instanceof Method) {
                logger.warn(
                        "Inject final method should be careful because it is not called on target bean when bean is proxied and may cause NullPointerException.");
            }
        }
    }

    void callMethod(Object beanInstance, Method method, String namedMethod) {
        // 调用init/destroy方法
        if (method != null) {
            try {
                method.invoke(beanInstance);
            } catch (ReflectiveOperationException e) {
                throw new BeanCreationException(e);
            }
        } else if (namedMethod != null) {
            // 查找initMethod/destroyMethod="xyz"，注意是在实际类型中查找
            Method named = ClassUtils.getNamedMethod(beanInstance.getClass(), namedMethod);
            named.setAccessible(true);
            try {
                named.invoke(beanInstance);
            } catch (ReflectiveOperationException e) {
                throw new BeanCreationException(e);
            }
        }
    }

}

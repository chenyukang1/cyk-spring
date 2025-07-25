package com.cyk.spring.web;

import com.cyk.spring.ioc.annotation.Bean;
import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.web.handler.DefaultHandlerMapping;
import com.cyk.spring.web.handler.HandlerMapping;

/**
 * The class WebMvcConfiguration
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
@Configuration
public class WebMvcConfiguration {

//    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfiguration.class);

//    @Bean(initMethod = "init")
//    public ViewResolver viewResolver(@Autowired ServletContext servletContext,
//                               @Value("${cyk.web.freemarker.template-path:/WEB-INF/templates}") String templatePath,
//                               @Value("${cyk.web.freemarker.template-encoding:UTF-8}") String templateEncoding) {
//        return new FreeMarkerViewResolver(new TemplateConfig(templatePath, templateEncoding), servletContext);
//    }

    @Bean
    public HandlerMapping handlerMapping() {
        return new DefaultHandlerMapping();
    }

}

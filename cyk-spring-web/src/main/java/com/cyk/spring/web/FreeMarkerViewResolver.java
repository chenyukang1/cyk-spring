package com.cyk.spring.web;

import com.cyk.spring.web.exception.ServerErrorException;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * The class FreeMarkerViewResolver
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
public class FreeMarkerViewResolver implements ViewResolver {

    private static final Logger logger = LoggerFactory.getLogger(FreeMarkerViewResolver.class);

    private final TemplateConfig templateConfig;

    private Configuration config;

    private final ServletContext servletContext;

    public FreeMarkerViewResolver(TemplateConfig templateConfig, ServletContext servletContext) {
        this.templateConfig = templateConfig;
        this.servletContext = servletContext;
    }

    @Override
    public void init() {
        logger.info("init {}, set template path: {}", getClass().getSimpleName(), templateConfig.templatePath());
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setOutputFormat(HTMLOutputFormat.INSTANCE);
        cfg.setDefaultEncoding(templateConfig.templateEncoding());
        cfg.setTemplateLoader(new ServletTemplateLoader(this.servletContext, templateConfig.templatePath()));
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        cfg.setAutoEscapingPolicy(Configuration.ENABLE_IF_SUPPORTED_AUTO_ESCAPING_POLICY);
        cfg.setLocalizedLookup(false);

        var ow = new DefaultObjectWrapper(Configuration.VERSION_2_3_32);
        ow.setExposeFields(true);
        cfg.setObjectWrapper(ow);
        this.config = cfg;
    }

    @Override
    public void render(String viewName, Map<String, Object> model, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Template template;
        try {
            template = this.config.getTemplate(viewName);
        } catch (Exception e) {
            throw new ServerErrorException("View not found: " + viewName);
        }
        PrintWriter pw = resp.getWriter();
        try {
            template.process(model, pw);
        } catch (TemplateException e) {
            throw new ServerErrorException(e);
        }
        pw.flush();
    }
}

package com.cyk.spring.web;

import com.cyk.spring.ioc.context.ConfigurableApplicationContext;
import com.cyk.spring.web.exception.ServerErrorException;
import com.cyk.spring.web.handler.HandlerMapping;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * The class DispatherServlet
 *
 * @author yukang.chen
 * @date 2025/6/27
 */
public class DispatcherServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    private List<HandlerMapping> handlerMappings;

    private final ConfigurableApplicationContext applicationContext;

    public DispatcherServlet(ConfigurableApplicationContext webApplicationContext) {
        this.applicationContext = webApplicationContext;
    }

    public void initServlet() throws ServletException {
        initHandlerMappings();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
//        writer.println("Hello, this is a response from DispatcherServlet!");
        handlerMappings.forEach(mapping -> {
            try {
                var handler = mapping.getHandler(req);
                if (!handler.applyPreHandle(req, resp)) {
                    return; // PreHandle failed, skip further processing
                }

                handler.doHandle(req, resp);

                handler.applyPostHandle(req, resp);
            } catch (Exception e) {
                logger.error("Error processing request", e);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writer.println("Error processing request: " + e.getMessage());
            }
        });
    }

    private void initHandlerMappings() {
        this.handlerMappings = null;
        List<HandlerMapping> mappings = applicationContext.getBeans(HandlerMapping.class);
        if (mappings != null && !mappings.isEmpty()) {
            this.handlerMappings = mappings;
        } else {
            throw new ServerErrorException("No HandlerMapping found in the application context.");
        }
        for (HandlerMapping mapping : handlerMappings) {
            try {
                mapping.init();
            } catch (Exception e) {
                logger.error("Failed to initialize HandlerMapping: {}", mapping.getClass().getName(), e);
                throw new ServerErrorException("Failed to initialize HandlerMapping: " + mapping.getClass().getName(), e);
            }
        }
    }
}

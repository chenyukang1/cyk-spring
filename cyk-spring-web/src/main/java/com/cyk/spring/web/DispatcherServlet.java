package com.cyk.spring.web;

import com.cyk.spring.web.handler.HandlerMapping;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

    private List<HandlerMapping> handlerMappings;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        writer.println("Hello, this is a response from DispatcherServlet!");
    }
}

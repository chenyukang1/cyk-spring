package com.cyk.spring.web.controller;

import com.cyk.spring.web.annotation.Controller;
import com.cyk.spring.web.annotation.RequestMapping;
import com.cyk.spring.web.annotation.RequestParam;
import com.cyk.spring.web.handler.HttpMethod;

/**
 * The class HelloController
 *
 * @author yukang.chen
 * @date 2025/7/6
 */
@Controller
public class HelloController {

    @RequestMapping(value = "/hello", method = HttpMethod.GET)
    public String hello(@RequestParam("toName") String name) {
        return "Hello, " + name + "!";
    }
}

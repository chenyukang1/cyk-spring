package com.cyk.spring.ioc.test.scan.primary;

import com.cyk.spring.ioc.annotation.Component;
import com.cyk.spring.ioc.annotation.Primary;

@Primary
@Component
public class TeacherBean extends PersonBean {

}

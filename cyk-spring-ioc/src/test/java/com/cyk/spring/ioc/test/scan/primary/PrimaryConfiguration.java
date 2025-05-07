package com.cyk.spring.ioc.test.scan.primary;

import com.cyk.spring.ioc.annotation.Bean;
import com.cyk.spring.ioc.annotation.Configuration;
import com.cyk.spring.ioc.annotation.Primary;

@Configuration
public class PrimaryConfiguration {

    @Primary
    @Bean
    public DogBean husky() {
        return new DogBean("Husky");
    }

    @Bean
    public DogBean teddy() {
        return new DogBean("Teddy");
    }
}

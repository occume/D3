package org.d3.context;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:beans.xml")
@MapperScan(basePackages = {"org.d3.mybatis.mapper"})
public class SpringConfig{
	
}

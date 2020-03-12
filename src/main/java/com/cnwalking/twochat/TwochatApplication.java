package com.cnwalking.twochat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.cnwalking.twochat.dao")
@SpringBootApplication
public class TwochatApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwochatApplication.class, args);
	}

}

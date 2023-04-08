package com.zju.vis.print_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class PrintBackendApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PrintBackendApplication.class, args);
	}

}

package com.jinloes.springazuread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringAzureAdApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAzureAdApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean myFilterBean(BasicFilter filter) {
		final FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
		filterRegBean.setFilter(filter);
		filterRegBean.addUrlPatterns("/secure/*");
		filterRegBean.setEnabled(Boolean.TRUE);
		filterRegBean.setName("azure test");
		filterRegBean.setAsyncSupported(Boolean.TRUE);
		return filterRegBean;
	}

}

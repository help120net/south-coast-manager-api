package cn.south.toast.mgmt;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cn.south.toast.common.config.DruidAutoConfiguration;
import cn.south.toast.common.config.ErrorConfig;

/**
 * 
 * @author huangbh
 *  date of 2018年4月4日
 */
@SpringBootApplication
@ComponentScan(basePackages = "cn.south.toast")
@Import({ErrorConfig.class, DruidAutoConfiguration.class})
@MapperScan(annotationClass = Repository.class, basePackages = { "cn.south.toast"})
@EnableTransactionManagement
@EnableAsync
@ServletComponentScan

public class MgmtApiApplication {
	

	public static void main(String[] args) {
		
		SpringApplication.run(MgmtApiApplication.class, args);

	}

}

package com.artisan.redpacket.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
//定义Spring 扫描的包
@ComponentScan(value= "com.*", includeFilters= {@Filter(type = FilterType.ANNOTATION, value ={Service.class})})
//使用事务驱动管理器
@EnableTransactionManagement
//实现接口TransactionManagementConfigurer，这样可以配置注解驱动事务
public class RootConfig implements TransactionManagementConfigurer {
	
	private DataSource dataSource = null;
	
	/**
	 * 配置数据库.
	 * @return 数据连接池
	 */
	@Bean(name = "dataSource")
	public DataSource initDataSource() {
		if (dataSource != null) {
			return dataSource;
		}
		try {
			Properties props = new Properties();
			props.load(RootConfig.class.getClassLoader().getResourceAsStream("jdbc.properties"));
			props.setProperty("driverClassName", props.getProperty("jdbc.driver"));
			props.setProperty("url", props.getProperty("jdbc.url"));
			props.setProperty("username", props.getProperty("jdbc.username"));
			props.setProperty("password", props.getProperty("jdbc.password"));
			dataSource = BasicDataSourceFactory.createDataSource(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSource;
	}
	
	/***
	 * 配置SqlSessionFactoryBean
	 * @return SqlSessionFactoryBean
	 */
	@Bean(name="sqlSessionFactory")
	public SqlSessionFactoryBean initSqlSessionFactory() {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(initDataSource());
		//配置MyBatis配置文件
		Resource resource = new ClassPathResource("mybatis/mybatis-config.xml");
		sqlSessionFactory.setConfigLocation(resource);
		return sqlSessionFactory;
	}
	
	/***
	 * 通过自动扫描，发现MyBatis Mapper接口
	 * @return Mapper扫描器
	 */
	@Bean 
	public MapperScannerConfigurer initMapperScannerConfigurer() {
		MapperScannerConfigurer msc = new MapperScannerConfigurer();
		msc.setBasePackage("com.*");
		msc.setSqlSessionFactoryBeanName("sqlSessionFactory");
		msc.setAnnotationClass(Repository.class);
		return msc;
	}
	
	
	/**
	 * 实现接口方法，注册注解事务，当@Transactional 使用的时候产生数据库事务 
	 */
	@Override
	@Bean(name="annotationDrivenTransactionManager")
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		DataSourceTransactionManager transactionManager = 
           new DataSourceTransactionManager();
		transactionManager.setDataSource(initDataSource());
		return transactionManager;
	}
	
	

}
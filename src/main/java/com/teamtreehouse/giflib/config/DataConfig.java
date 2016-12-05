package com.teamtreehouse.giflib.config;


import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

@Configuration
@PropertySource("app.properties")
public class DataConfig {
    // Spring will load all the properties from
    // app.properties in env so we can use elsewhere
    @Autowired
    private Environment env;

    @Bean
    public LocalSessionFactoryBean sessionFactory(){
        Resource config = new ClassPathResource("hibernate.cfg.xml");
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        // set location for config file
        sessionFactory.setConfigLocation(config);

        // Set location to scan for entities
        sessionFactory.setPackagesToScan(env.getProperty("giflib.entity.package"));

        // Set the source of the data(the database)
        sessionFactory.setDataSource(dataSource());
        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();

        // Driver class name
        ds.setDriverClassName(env.getProperty("giflib.db.driver"));

        // Set URL
        ds.setUrl(env.getProperty("giflib.db.url"));

        // Set username & password
        ds.setUsername(env.getProperty("giflib.db.username"));
        ds.setPassword(env.getProperty("giflib.db.password"));

        return ds;
    }


}

package com.tung.bcbe.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public AmazonS3 s3Client() {
        return AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
    }

    /*
    https://stackoverflow.com/questions/21708339/avoid-jackson-serialization-on-non-fetched-lazy-objects/21760361#21760361
     */
    /*
    https://medium.com/@evonsdesigns/spring-boot-include-transient-fields-in-jackson-serialization-4d6e24571585
     */
    @Bean
    public Module datatypeHibernateModule() {
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        hibernate5Module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        return hibernate5Module;
    }
}

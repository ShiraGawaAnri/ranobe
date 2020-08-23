package com.kd.novel.backend.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.hibernate.Hibernate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    /**
     * 解决jpa懒加载
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = converter.getObjectMapper();
        mapper.registerModule(new Hibernate5Module());
        //在反序列化时忽略在 json 中存在但 Java 对象不存在的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //在序列化时忽略值为 null 的属性
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //忽略值为默认值的属性
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

        //
        mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return converter;
    }

    @Bean
    public Hibernate5Module hibernate5Module() {
        Hibernate5Module module = new Hibernate5Module();
        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        module.enable(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        return module;
    }

//    @Bean
//    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
//        OpenEntityManagerInViewFilter openEntityManagerInViewFilter= new OpenEntityManagerInViewFilter();
//        openEntityManagerInViewFilter.setEntityManagerFactoryBeanName("chexAdminJpaEntityManagerFactory");
//        return  openEntityManagerInViewFilter;
//    }
}

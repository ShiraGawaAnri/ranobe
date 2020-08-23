package com.kd.novel.backend.config.druid;

import com.alibaba.druid.support.http.WebStatFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 *  * druid监控拦截去
 *  
 */
@WebFilter(filterName = "druidWebStatFilter",
        urlPatterns = "/*",
        initParams = {
                @WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")
        })
public class DruidStatFilter extends WebStatFilter {

}

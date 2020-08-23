package com.kd.novel.backend.config;


import com.kd.novel.backend.exception.JWTAccessDeniedHandler;
import com.kd.novel.backend.exception.JWTAuthenticationEntryPoint;
import com.kd.novel.backend.filter.JWTAuthenticationFilter;
import com.kd.novel.backend.filter.JWTAuthorizationFilter;
import com.kd.novel.backend.filter.OptionsRequestFilter;
import com.kd.novel.backend.service.jwt.JwtUserDetailsServiceImpl;
import com.kd.novel.backend.config.jwt.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Created by echisan on 2018/6/23
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String[] path = new String[]{
            "/log/**", "/charge/**", "/sys/**", "/user/**", "/dashboard/**", "/stateraccount/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean("jwtUserService")
    protected JwtUserDetailsServiceImpl jwtUserService() {

        return new JwtUserDetailsServiceImpl();
    }

    @Bean("jwtAuthenticationProvider")
    protected AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtUserService());
    }

    @Bean("daoAuthenticationProvider")
    protected AuthenticationProvider daoAuthenticationProvider() {
        //这里会默认使用BCryptPasswordEncoder比对加密后的密码，注意要跟createUser时保持一致
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService());
        return daoProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider()).userDetailsService(jwtUserService()).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new OptionsRequestFilter(), CorsFilter.class).csrf().disable()
                .authorizeRequests()
                .antMatchers(path).authenticated()
                // 测试用资源，需要验证了的用户才能访问
                .antMatchers("/tasks/**").authenticated()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(HttpMethod.DELETE, "/tasks/**").hasRole("ADMIN")
                // 其他都放行了
                .anyRequest().permitAll()
                .and()
                .formLogin().disable()
                .sessionManagement().disable()
                .headers().frameOptions().sameOrigin()
                .and()

                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                /*// 不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)*/
                .exceptionHandling().authenticationEntryPoint(new JWTAuthenticationEntryPoint())
                .accessDeniedHandler(new JWTAccessDeniedHandler());      //添加无权限时的处理
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

}

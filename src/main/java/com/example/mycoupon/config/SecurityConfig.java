package com.example.mycoupon.config;

import com.example.mycoupon.security.CustomUserDetailsService;
import com.example.mycoupon.domain.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MemberService memberService;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomUserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager manager) throws Exception {
        return new JwtAuthenticationFilter(manager);
    }

    private JwtAuthorizationFilter jwtAuthorizationFilter(AuthenticationManager manager) throws Exception {
        return new JwtAuthorizationFilter(manager);
    }

    private SignUpFilter signUpFilter(AuthenticationManager manager) {
        return new SignUpFilter(manager, memberService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .addFilter(jwtAuthenticationFilter(authenticationManagerBean()))
                    .addFilterBefore(signUpFilter(authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class)
                    .addFilter(jwtAuthorizationFilter(authenticationManagerBean()))
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/signup").permitAll()
                    .antMatchers(HttpMethod.POST, "/login").permitAll()
                    //.antMatchers(HttpMethod.GET, "/h2-console/**").permitAll()
                    .anyRequest().authenticated();

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

}

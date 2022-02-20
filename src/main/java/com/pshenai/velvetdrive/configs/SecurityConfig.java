package com.pshenai.velvetdrive.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
                          AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/")
                .hasAnyRole("USER", "ADMIN")
                //.antMatchers("/admin") //@PreAuthorize
                //    .hasRole("ADMIN")
                .antMatchers("/register")
                .permitAll()
                .and()
                    .exceptionHandling()
                .accessDeniedPage("/unauthorized")
                .and()
                    .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/j_spring_security_check")
                .failureUrl("/login?error=true")
                .usernameParameter("j_login")
                .passwordParameter("j_password")
                .permitAll()
                .and()
                .oauth2Login()
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler)
                .and()
                    .logout()
                .permitAll()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout");

    }

}

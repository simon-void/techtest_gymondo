package net.gymondo.subservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {

    /**
     * Whitelisting two endpoints:
     * GraphQL and Actuator
     * Everything else is denied.
     */
    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers("/actuator/health").permitAll()
            .antMatchers("/graphql").permitAll()
            .anyRequest().denyAll()
            .and()
            .csrf()
            .disable()
    }

}
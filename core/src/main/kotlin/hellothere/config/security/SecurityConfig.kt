package hellothere.config.security

import hellothere.config.RestUrl.USER
import hellothere.service.security.JwtTokenFilter
import hellothere.service.security.JwtTokenService
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@Primary
class SecurityConfig(
    private val jwtTokenService: JwtTokenService
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers("/api/**").permitAll()
            .antMatchers("$USER/**")
            .authenticated()
    }
}

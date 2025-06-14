package com.gustavo.blogpessoaldeise.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration = DIZ AO SPRING QUE ESSA É UMA CLASSE DE CONFIGURAÇÃO (VAI DIZER COMO O SISTEMA DEVE SE COMPORTAR)
@Configuration
//@EnableWebSecurity =  ATIVA A SEGURANÇA DA WEB DO SPRING (SPRING SECURITY)
@EnableWebSecurity
public class BasicSecurityConfig {

	
	//@Autowired = É COMO PEDIR "SPRING, ME ENTREGA ISSO PRONTO"
	@Autowired 
	// CRIANDO UMA VARIAVEL CHAMADA authFilter DO TIPO JwtAuthFilter
	private JwtAuthFilter authFilter; 

	
	
	
	@Bean
	UserDetailsService userDetailsService() {

		return new UserDetailsServiceImpl();
	}

	
	
	
	
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
	

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	
	
	

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	
	
	

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .csrf(csrf -> csrf.disable()).cors(withDefaults());

	    http.authorizeHttpRequests((auth) -> auth
	            // ESSA LINHA É A MAIS IMPORTANTE E DEVE VIR PRIMEIRO PARA O SWAGGER
	            .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

	            // OUTRAS REGRAS PERMITALL() VÊM AQUI (login, cadastrar, error, OPTIONS, /, GET /usuarios)
	            .requestMatchers("/usuarios/logar").permitAll()
	            .requestMatchers("/usuarios/cadastrar").permitAll()
	            .requestMatchers("/error/**").permitAll()
	            .requestMatchers(HttpMethod.OPTIONS).permitAll()
	            .requestMatchers("/").permitAll()
	            .requestMatchers(HttpMethod.GET, "/usuarios").permitAll()

	            // ESTA LINHA DEVE SER SEMPRE A ÚLTIMA!
	            .anyRequest().authenticated())
	            .authenticationProvider(authenticationProvider())
	            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
	            .httpBasic(basic -> basic.disable());

	    return http.build();

	}

}

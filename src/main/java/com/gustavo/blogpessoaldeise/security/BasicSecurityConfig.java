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
	// O @BEAN DIZ AO SPRING PARA GERENCIAR ESTE MÉTODO E O OBJETO QUE ELE RETORNA.
	// NESTE CASO, ELE CRIA E CONFIGURA O "FILTRO DE SEGURANÇA" PRINCIPAL DA SUA APLICAÇÃO.
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	    // ESTA PARTE CONFIGURA COMO O SPRING SECURITY VAI GERENCIAR AS SESSÕES DOS USUÁRIOS.
	    // 'SESSIONMANAGEMENT': GERENCIAMENTO DE SESSÃO.
	    // 'SESSIONCREATIONPOLICY(SESSIONCREATIONPOLICY.STATELESS)': INDICA QUE A APLICAÇÃO NÃO VAI MANTER ESTADO DE SESSÃO NO SERVIDOR.
	    // ISSO É TÍPICO PARA APIS REST COM JWT (JSON WEB TOKEN), ONDE CADA REQUISIÇÃO CONTÉM TODAS AS INFORMAÇÕES NECESSÁRIAS (O TOKEN).
	    http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        // 'CSRF(CSRF -> CSRF.DISABLE())': DESABILITA A PROTEÇÃO CONTRA CSRF (CROSS-SITE REQUEST FORGERY).
	        // ISSO GERALMENTE É SEGURO PARA APIS REST, POIS ELAS NÃO USAM COOKIES DE SESSÃO PARA AUTENTICAÇÃO.
	        .csrf(csrf -> csrf.disable())
	        // 'CORS(WITHDEFAULTS())': HABILITA O COMPARTILHAMENTO DE RECURSOS ENTRE ORIGENS (CORS) COM AS CONFIGURAÇÕES PADRÃO DO SPRING.
	        // ISSO PERMITE QUE SEU FRONT-END (SE ESTIVER EM UM DOMÍNIO DIFERENTE) SE COMUNIQUE COM O BACK-END.
	        .cors(withDefaults());

	    // ESTA É A PARTE PRINCIPAL ONDE VOCÊ DEFINE AS REGRAS DE AUTORIZAÇÃO PARA OS ENDPOINTS (URLS) DA SUA API.
	    // 'AUTHORIZEHTTPREQUESTS': INICIA A CONFIGURAÇÃO DE AUTORIZAÇÃO PARA REQUISIÇÕES HTTP.
	    http.authorizeHttpRequests((auth) -> auth
	            // 'REQUESTMATCHERS("/USUARIOS/LOGAR").PERMITALL()': PERMITE QUE QUALQUER PESSOA ACESSE O ENDPOINT DE LOGIN SEM NECESSIDADE DE AUTENTICAÇÃO.
	            .requestMatchers("/usuarios/logar").permitAll()
	            // 'REQUESTMATCHERS("/USUARIOS/CADASTRAR").PERMITALL()': PERMITE QUE QUALQUER PESSOA SE CADASTRE SEM NECESSIDADE DE AUTENTICAÇÃO.
	            .requestMatchers("/usuarios/cadastrar").permitAll()
	            // 'REQUESTMATCHERS("/ERROR/**").PERMITALL()': PERMITE ACESSO À PÁGINA DE ERRO PADRÃO DO SPRING BOOT. O '/**' INCLUI QUALQUER COISA DEPOIS DE /ERROR.
	            .requestMatchers("/error/**").permitAll()
	            // 'REQUESTMATCHERS(HTTPMETHOD.OPTIONS).PERMITALL()': PERMITE REQUISIÇÕES HTTP DO TIPO 'OPTIONS'.
	            // ESTAS SÃO REQUISIÇÕES "PRÉ-VOO" USADAS PELOS NAVEGADORES PARA VERIFICAR PERMISSÕES DE CORS ANTES DE ENVIAR A REQUISIÇÃO REAL.
	            .requestMatchers(HttpMethod.OPTIONS).permitAll()
	            
	            // **NOVA LINHA OU AJUSTE:**
	            // 'REQUESTMATCHERS("/SWAGGER-UI/**", "/V3/API-DOCS/**", "/SWAGGER-RESOURCES/**", "/WEBJARS/**").PERMITALL()':
	            // ESTAS SÃO AS LINHAS CRUCIAIS PARA LIBERAR O ACESSO AO SWAGGER UI E À DOCUMENTAÇÃO DA API.
	            // O '/**' É IMPORTANTE PARA INCLUIR TODOS OS ARQUIVOS (HTML, CSS, JS, ETC.) NECESSÁRIOS PARA A INTERFACE FUNCIONAR CORRETAMENTE.
	            // AO USAR 'PERMITALL()', VOCÊ DIZ AO SPRING SECURITY PARA NÃO APLICAR NENHUMA RESTRIÇÃO DE AUTENTICAÇÃO OU AUTORIZAÇÃO A ESSES CAMINHOS.
	            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
	            
	            // 'REQUESTMATCHERS("/").PERMITALL()': PERMITE ACESSO À RAIZ DA SUA APLICAÇÃO (EX: WWW.SEUSITE.COM/).
	            // ATENÇÃO: EM PRODUÇÃO, PODE SER NECESSÁRIO PROTEGER ESTE ENDPOINT OU REMOVÊ-LO SE NÃO FOR NECESSÁRIO.
	            .requestMatchers("/").permitAll()
	            // 'REQUESTMATCHERS(HTTPMETHOD.GET, "/USUARIOS").PERMITALL()': PERMITE REQUISIÇÕES 'GET' PARA O ENDPOINT '/USUARIOS' SEM AUTENTICAÇÃO.
	            // ATENÇÃO: EM PRODUÇÃO, PROVAVELMENTE VOCÊ QUER PROTEGER ESTE ENDPOINT PARA QUE APENAS USUÁRIOS AUTENTICADOS POSSAM LISTAR TODOS OS USUÁRIOS.
	            .requestMatchers(HttpMethod.GET, "/usuarios").permitAll()
	            
	            // 'ANYREQUEST().AUTHENTICATED()': ESTA É A REGRA MAIS IMPORTANTE.
	            // DEPOIS DE TODAS AS REGRAS 'PERMITALL()' ACIMA, QUALQUER OUTRA REQUISIÇÃO (QUALQUER URL QUE NÃO FOI PERMITIDA EXPLICITAMENTE)
	            // DEVE SER 'AUTHENTICATED()', OU SEJA, O USUÁRIO PRECISA ESTAR LOGADO E TER UM TOKEN VÁLIDO.
	            .anyRequest().authenticated())
	            // 'AUTHENTICATIONPROVIDER(AUTHENTICATIONPROVIDER())': REGISTRA O PROVEDOR DE AUTENTICAÇÃO QUE VOCÊ CONFIGUROU.
	            // ELE É RESPONSÁVEL POR COMO OS USUÁRIOS SÃO VERIFICADOS (EX: VERIFICANDO E-MAIL E SENHA NO BANCO).
	            .authenticationProvider(authenticationProvider())
	            // 'ADDFILTERBEFORE(AUTHFILTER, USERNAMEPASSWORDAUTHENTICATIONFILTER.CLASS)':
	            // ADICIONA O SEU FILTRO JWT (JWTAUTHFILTER) ANTES DO FILTRO DE AUTENTICAÇÃO PADRÃO DO SPRING SECURITY.
	            // ISSO GARANTE QUE SEU FILTRO JWT PROCESSE O TOKEN ANTES QUE O SPRING TENTE FAZER SUA PRÓPRIA VERIFICAÇÃO DE USUÁRIO E SENHA.
	            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
	            // 'HTTPBASIC(BASIC -> BASIC.DISABLE())': DESABILITA A AUTENTICAÇÃO HTTP BÁSICA, QUE GERALMENTE ENVIA CREDENCIAIS SEM CRIPTOGRAFIA (MENOS SEGURO).
	            // COMO VOCÊ ESTÁ USANDO JWT, NÃO PRECISA DE AUTENTICAÇÃO BÁSICA.
	            .httpBasic(basic -> basic.disable());

	    // RETORNA A CADEIA DE FILTROS DE SEGURANÇA CONFIGURADA PARA O SPRING.
	    return http.build();

	}

}

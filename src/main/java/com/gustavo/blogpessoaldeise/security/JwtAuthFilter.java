package com.gustavo.blogpessoaldeise.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
//ANOTAÇÃO PARA O SPRING GERENCIAR ESSA CLASSE COMO UM COMPONENTE
public class JwtAuthFilter extends OncePerRequestFilter {

@Autowired
// INJEÇÃO AUTOMÁTICA DO SERVIÇO QUE TRABALHA COM TOKENS JWT
private JwtService jwtService;

@Autowired
// INJEÇÃO AUTOMÁTICA DO SERVIÇO QUE CARREGA OS DADOS DO USUÁRIO
private UserDetailsServiceImpl userDetailsService;

@Override
// MÉTODO QUE SERÁ CHAMADO AUTOMATICAMENTE PARA FILTRAR TODAS AS REQUISIÇÕES
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
   
   // PEGAR O CABEÇALHO "Authorization" DA REQUISIÇÃO
   String authHeader = request.getHeader("Authorization");
   String token = null;
   String useremail = null;

   
   
   
   
   try {
       // VERIFICA SE O CABEÇALHO NÃO É NULO E COMEÇA COM "Bearer "
       if (authHeader != null && authHeader.startsWith("Bearer ")) {
           // REMOVE A PALAVRA "Bearer " DO COMEÇO PARA PEGAR APENAS O TOKEN
           token = authHeader.substring(7);
           // EXTRAI O USERNAME DO TOKEN USANDO O SERVIÇO JWT
           useremail = jwtService.extractUsername(token);
       }

       // SE O USERNAME FOI ENCONTRADO E NÃO HÁ NINGUÉM AUTENTICADO AINDA NO CONTEXTO DE SEGURANÇA
       if (useremail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
           // CARREGA OS DADOS DO USUÁRIO PELO USERNAME
           UserDetails userDetails = userDetailsService.loadUserByUsername(useremail);
               
           // VALIDA SE O TOKEN É VÁLIDO PARA ESSE USUÁRIO
           if (jwtService.validateToken(token, userDetails)) {
               // CRIA UM OBJETO DE AUTENTICAÇÃO PARA O USUÁRIO, COM SUAS PERMISSÕES
               UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
               // ADICIONA DETALHES DA REQUISIÇÃO (EX: IP, AGENTE DE USUÁRIO)
               authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               // COLOCA ESSA AUTENTICAÇÃO NO CONTEXTO DE SEGURANÇA PARA SER USADA DURANTE A REQUISIÇÃO
               SecurityContextHolder.getContext().setAuthentication(authToken);
           }
       }
       // PASSA A REQUISIÇÃO PARA O PRÓXIMO FILTRO DA CADEIA
       filterChain.doFilter(request, response);

   } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException 
           | SignatureException | ResponseStatusException e) {
       // SE ALGUMA EXCEÇÃO DE TOKEN ACONTECER (TOKEN EXPIRADO, MAL FORMADO, ETC),
       // ENVIA O STATUS 403 (FORBIDDEN) PARA O CLIENTE E PARA A EXECUÇÃO
       response.setStatus(HttpStatus.FORBIDDEN.value());
       return;
   }
}

}

/*
RESUMO:
ESSA CLASSE É UM FILTRO QUE INTERCEPTA TODAS AS REQUISIÇÕES QUE CHEGAM AO SERVIDOR.
ELA PEGA O TOKEN JWT DO CABEÇALHO DE AUTORIZAÇÃO, EXTRAI O NOME DO USUÁRIO E VERIFICA SE O TOKEN É VÁLIDO.
SE O TOKEN FOR VÁLIDO, ELA AUTENTICA O USUÁRIO NO SISTEMA, COLOCANDO ESSA INFORMAÇÃO NO CONTEXTO DE SEGURANÇA.
SE O TOKEN FOR INVÁLIDO, EXPIRADO OU MAL FORMADO, A REQUISIÇÃO É BLOQUEADA E O SERVIDOR RESPONDE COM ERRO 403.
ISSO GARANTE QUE APENAS USUÁRIOS AUTENTICADOS E COM TOKEN VÁLIDO POSSAM ACESSAR OS RECURSOS PROTEGIDOS DO SISTEMA.
*/

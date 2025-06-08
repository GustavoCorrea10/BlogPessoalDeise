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


//@COMPONENT: TRANSFORMA ESTA CLASSE EM UM COMPONENTE GERENCIADO PELO SPRING.
//É COMO COLOCAR O "SEGURANÇA" NA LISTA DE FUNCIONÁRIOS DA EMPRESA.
//A CLASSE EXTENDS ONCEPERREQUESTFILTER: ISSO GARANTE QUE O "SEGURANÇA" VERIFIQUE CADA PESSOA APENAS UMA VEZ, MESMO QUE ELA PASSE POR VÁRIOS CORREDORES.
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	
	
	
@Autowired
// INJEÇÃO AUTOMÁTICA DO SERVIÇO QUE TRABALHA COM TOKENS JWT
// É O SEGURANÇA PEGANDO A "FÁBRICA DE CRACHÁS" PARA SABER COMO LER E VALIDAR OS CRACHÁS.
private JwtService jwtService;





@Autowired
// INJEÇÃO AUTOMÁTICA DO SERVIÇO QUE CARREGA OS DADOS DO USUÁRIO
// É O SEGURANÇA PEGANDO O CONTATO DO "PORTEIRO" PARA CONFERIR A LISTA DE MEMBROS.
private UserDetailsServiceImpl userDetailsService;




//MÉTODO PRINCIPAL DO FILTRO. É AQUI QUE O TRABALHO DO SEGURANÇA ACONTECE.
// ELE RECEBE O PEDIDO DE ENTRADA (REQUEST), A RESPOSTA QUE SERÁ DADA (RESPONSE) E A "FILA DE VERIFICAÇÃO" (FILTERCHAIN).
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
 
	// PASSO 1: TENTAR PEGAR O CRACHÁ (TOKEN) DA PESSOA.
    // O CRACHÁ GERALMENTE VEM NO CABEÇALHO "AUTHORIZATION".
   String authHeader = request.getHeader("Authorization");
   String token = null;
   String useremail = null;

   
   
   
   // ESTE BLOCO 'TRY' É PARA TENTAR FAZER ALGO QUE PODE DAR ERRO.
   // SE O CRACHÁ FOR FALSO OU ESTRAGADO, O ERRO SERÁ CAPTURADO PELO 'CATCH' LÁ EMBAIXO
   try {
	   
	   // VERIFICA SE A PESSOA MOSTROU ALGUM CABEÇALHO E SE ELE COMEÇA COM "BEARER ".
       // "BEARER" É O PADRÃO, COMO DIZER "EU ESTOU PORTANDO ESTE CRACHÁ"
       if (authHeader != null && authHeader.startsWith("Bearer ")) {
    	   
           // SE SIM, PEGA SÓ O CÓDIGO DO CRACHÁ, IGNORANDO O "BEARER ".
    	   token = authHeader.substring(7);

           // USA A "FÁBRICA DE CRACHÁS" (JWTSERVICE) PARA LER O NOME ESCRITO NO CRACHÁ.
           useremail = jwtService.extractUsername(token);
       }

       
       // PASSO 2: VERIFICAR O CRACHÁ E AUTORIZAR A ENTRADA.
       // "SE CONSEGUIMOS LER UM E-MAIL DO CRACHÁ E AINDA NÃO TEM NINGUÉM COM 'PULSEIRINHA VIP' NESTA REQUISIÇÃO..."
       if (useremail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    	   

           // USA O "PORTEIRO" (USERDETAILSSERVICE) PARA BUSCAR A FICHA COMPLETA DO USUÁRIO PELO E-MAIL.
           UserDetails userDetails = userDetailsService.loadUserByUsername(useremail);
               
           

           // USA A "FÁBRICA DE CRACHÁS" (JWTSERVICE) PARA VALIDAR O CRACHÁ:
           // "ESTE CRACHÁ PERTENCE MESMO A ESTA PESSOA E NÃO ESTÁ VENCIDO?"
           if (jwtService.validateToken(token, userDetails)) {
        	   

               // SE O CRACHÁ É VÁLIDO, CRIA UMA "PULSEIRINHA VIP" (AUTENTICAÇÃO) PARA O USUÁRIO.
               UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

               // ADICIONA DETALHES EXTRAS NA "PULSEIRINHA", COMO DE ONDE A PESSOA VEIO (IP).
               authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               

               // O PASSO MAIS IMPORTANTE**: COLOCA A "PULSEIRINHA VIP" NO CONTEXTO DE SEGURANÇA.
               // A PARTIR DE AGORA, O SISTEMA TODO SABE QUE ESTA PESSOA ESTÁ AUTENTICADA.
               SecurityContextHolder.getContext().setAuthentication(authToken);
           }
       }

       // PASSO 3: DEIXAR A PESSOA CONTINUAR NA FILA.
       // PASSA A REQUISIÇÃO PARA O PRÓXIMO FILTRO OU PARA O DESTINO FINAL (O CONTROLLER).
       // SE ISSO NÃO FOR CHAMADO, A PESSOA FICA PRESA NA PORTA!
       filterChain.doFilter(request, response);

       
       // SE O CRACHÁ FOR FALSO, EXPIRADO, ESTRAGADO OU QUALQUER OUTRO PROBLEMA...
   } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException 
           | SignatureException | ResponseStatusException e) {
       
	   
       // O SEGURANÇA BARRA A ENTRADA E AVISA QUE O ACESSO É PROIBIDO.

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

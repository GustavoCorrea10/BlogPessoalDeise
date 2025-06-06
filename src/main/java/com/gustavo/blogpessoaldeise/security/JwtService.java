package com.gustavo.blogpessoaldeise.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

//@COMPONENT É UMA ANOTAÇÃO DO SPRING QUE DIZ PARA ELE CRIAR E GERENCIAR UM OBJETO DESTA CLASSE AUTOMATICAMENTE QUANDO A APLICAÇÃO INICIAR.
//ISSO PERMITE QUE ESSA CLASSE SEJA USADA FACILMENTE EM OUTRAS PARTES DO PROJETO SEM QUE VOCÊ PRECISE CRIAR O OBJETO MANUALMENTE COM "NEW".
//É COMO SE O SPRING FICASSE RESPONSÁVEL POR PREPARAR E ENTREGAR ESSA CLASSE PRONTA PARA QUEM PRECISAR USÁ-LA.
@Component
public class JwtService {

	// STATIC = SIGNIFICA QUE A VARIÁVEL PERTENCE À CLASSE E NÃO AO OBJETO
	// FINAL = SIGNIFICA QUE O VALOR NÃO PODE SER MUDADO (É CONSTANTE)
	// STRING = O TIPO DA VARIÁVEL
	// SECRET = É O NOME DA VARIÁVEL
	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

	
	
	
	
	
	// MÉTODO PRIVADO getSignKey
	// Key = É O TIPO DE DADO USADO PARA REPRESENTAR UMA CHAVE DE SEGURANÇA
	private Key getSignKey() {
		
		// BYTE = TIPO DE DADO QUE REPRESENTA UM NÚMERO PEQUENO
		// [] = SIGNIFICA QUE É UM ARRAY (LISTA DE BYTES)
		// keyBytes = NOME DA VARIÁVEL
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		
		// SECRET É UMA STRING QUE ESTÁ EM FORMATO BASE64
		// Decoders.BASE64.decode() CONVERTE A STRING EM BYTES
		// ISSO É NECESSÁRIO PARA CRIAR UMA CHAVE DE SEGURANÇA USÁVEL

		return Keys.hmacShaKeyFor(keyBytes);
		// Keys.hmacShaKeyFor() PEGA OS BYTES E CRIA UMA CHAVE SEGURA PARA ASSINAR E VALIDAR O TOKEN
	}

	
	
	
	
	
	
	
	
	
	
	
	// MÉTODO PRIVADO QUE EXTRAI TODAS AS INFORMAÇÕES (CLAIMS) DO TOKEN
	private Claims extractAllClaims(String token) {
		// Jwts.parserBuilder() CRIA UM OBJETO QUE CONSEGUE LER O TOKEN
		// .setSigningKey(getSignKey()) DEFINE A CHAVE DE ASSINATURA
		// .parseClaimsJws(token) FAZ A LEITURA DO TOKEN
		// .getBody() DEVOLVE SOMENTE O CORPO DO TOKEN, ONDE ESTÃO OS CLAIMS (DADOS)
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
	}

	
	
	
	
	
	
	
	
	
	
	
	// MÉTODO GENÉRICO QUE EXTRAI UMA INFORMAÇÃO ESPECÍFICA DO TOKEN
	// <T> T = SIGNIFICA QUE ESSE MÉTODO PODE RETORNAR QUALQUER TIPO DE DADO
	// claimsResolver = FUNÇÃO QUE DIZ QUAL DADO PEGAR DOS CLAIMS
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token); // PEGA TODOS OS CLAIMS DO TOKEN
		return claimsResolver.apply(claims); // APLICA A FUNÇÃO PARA PEGAR A INFORMAÇÃO ESPECÍFICA
	}

	
	
	
	
	
	
	
	
	
	
	
	// MÉTODO PARA PEGAR O USERNAME DO TOKEN
	// O USERNAME FICA NO CAMPO "SUBJECT" DOS CLAIMS
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject); // USA O MÉTODO GENÉRICO PARA PEGAR O SUBJECT
	}

	
	
	
	
	
	
	
	
	
	// MÉTODO PARA PEGAR A DATA DE EXPIRAÇÃO DO TOKEN
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration); // USA O MÉTODO GENÉRICO PARA PEGAR A EXPIRAÇÃO
	}

	
	
	
	
	
	
	
	
	
	
	// MÉTODO PRIVADO PARA VERIFICAR SE O TOKEN JÁ EXPIROU
	private Boolean isTokenExpired(String token) {
		// COMPARA A DATA DE EXPIRAÇÃO COM A DATA ATUAL
		return extractExpiration(token).before(new Date());
	}

	
	
	
	
	
	
	
	
	
	// MÉTODO PARA VERIFICAR SE O TOKEN É VÁLIDO
	// ELE VERIFICA SE O USERNAME DO TOKEN É IGUAL AO USERNAME DO USUÁRIO LOGADO
	// E TAMBÉM SE O TOKEN NÃO EXPIROU
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String email = extractUsername(token); // PEGA O USERNAME DO TOKEN
		return (email.equals(userDetails.getUsername()) && !isTokenExpired(token)); // COMPARA E VERIFICA A VALIDADE
	}

	
	
	
	
	
	
	
	
	
	// MÉTODO PRIVADO QUE CRIA UM NOVO TOKEN
	// claims = INFORMAÇÕES EXTRAS QUE PODEM SER ADICIONADAS AO TOKEN
	// userName = USERNAME QUE VAI FICAR DENTRO DO TOKEN
	private String createToken(Map<String, Object> claims, String userEmail) {
		return Jwts.builder()
				.setClaims(claims) // ADICIONA AS INFORMAÇÕES EXTRAS
				.setSubject(userEmail) // DEFINE O USERNAME
				.setIssuedAt(new Date(System.currentTimeMillis())) // DATA DE CRIAÇÃO DO TOKEN
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // EXPIRA EM 1 HORA
				.signWith(getSignKey(), SignatureAlgorithm.HS256) // ASSINA O TOKEN COM A CHAVE E O ALGORITMO
				.compact(); // FINALIZA E DEVOLVE O TOKEN EM FORMATO STRING
	}

	
	
	
	
	
	
	
	
	
	// MÉTODO PÚBLICO QUE GERA O TOKEN
	// ELE CHAMA O MÉTODO DE CIMA COM UMA LISTA VAZIA DE CLAIMS E O USERNAME
	public String generateToken(String userEmail) {
		Map<String, Object> claims = new HashMap<>(); // CRIA UM MAPA VAZIO DE CLAIMS
		return createToken(claims, userEmail); // GERA O TOKEN
	}
	
	
	
	
	
	/*
	RESUMO:
	ESSA CLASSE É RESPONSÁVEL POR CRIAR E VALIDAR TOKENS JWT,
	QUE SÃO USADOS PARA GARANTIR A SEGURANÇA NAS COMUNICAÇÕES ENTRE CLIENTE E SERVIDOR.
	ELA CONVERTE UMA CHAVE SECRETA (EM BASE64) PARA UMA CHAVE QUE PODE ASSINAR OS TOKENS,
	EXTRAI INFORMAÇÕES DO TOKEN COMO O USERNAME E A DATA DE EXPIRAÇÃO,
	VERIFICA SE O TOKEN AINDA ESTÁ VÁLIDO (NÃO EXPIRADO E PERTENCE AO USUÁRIO CORRETO),
	E POR FIM, GERA NOVOS TOKENS COM INFORMAÇÕES BÁSICAS E TEMPO DE VALIDADE DEFINIDO.
	COM ESSA CLASSE, O SISTEMA CONSEGUE CONTROLAR ACESSOS DE FORMA SEGURA USANDO JWT.
*/


}

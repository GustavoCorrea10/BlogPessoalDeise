// PACOTE: É O ENDEREÇO DA CASA ONDE ESTE CÓDIGO MORA.
package com.gustavo.blogpessoaldeise.security;

// IMPORTAÇÕES: SÃO AS FERRAMENTAS QUE O CÓDIGO PEGA DE OUTRAS CAIXAS DE FERRAMENTAS PARA PODER TRABALHAR.
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// @COMPONENT: É UM AVISO PARA O CHEFE (O SPRING) E DIZ: "EI, QUANDO O TRABALHO COMEÇAR, ME DEIXE PRONTO PARA USAR!".
// O SPRING GUARDA ESSE SERVIÇO EM UMA "PRATELEIRA" PARA ENTREGAR A QUEM PRECISAR.
@Component
public class JwtService {

	// @VALUE: É COMO UM BILHETE QUE DIZ: "VÁ ATÉ O ARQUIVO DE CONFIGURAÇÕES ('application.properties'),
	// ENCONTRE A LINHA 'blog.security.jwt.secret' E TRAGA O VALOR DELA PARA CÁ".
	@Value("${blog.security.jwt.secret}")
	// PRIVATE STRING SECRET: É UMA GAVETA SECRETA DENTRO DA CLASSE ONDE GUARDAMOS A SENHA MESTRA
	// QUE VEIO DO ARQUIVO DE CONFIGURAÇÕES.
	private String secret;

	
	
	
	
	// ESTE MÉTODO É O "FERREIRO". ELE PEGA A SENHA MESTRA, QUE É UM TEXTO,
	// E A TRANSFORMA EM UMA CHAVE DE METAL DE VERDADE, QUE PODE SER USADA PARA TRANCAR E DESTRANCAR OS CRACHÁS.
	private Key getSignKey() {
		// DECODE: TRANSFORMA O TEXTO DA SENHA EM UM FORMATO QUE O COMPUTADOR ENTENDE MELHOR (BYTES).
		byte[] keyBytes = Decoders.BASE64.decode(this.secret);
		// HMACSHAKEYFOR: CRIA A "CHAVE DE METAL" (A CHAVE DE ASSINATURA) A PARTIR DESSE MATERIAL.
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	
	
	
	
	
	
	
	
	
	

	// ESTE MÉTODO É O "LEITOR DE CRACHÁ". ELE PEGA UM CRACHÁ (TOKEN)
	// E USA A CHAVE DE METAL PARA ABRIR E LER TODAS AS INFORMAÇÕES ESCRITAS DENTRO DELE.
	// 'CLAIMS' SÃO AS INFORMAÇÕES: NOME DO DONO, VALIDADE, ETC.
	// SE ALGUÉM TENTAR USAR UM CRACHÁ FALSO (COM A ASSINATURA ERRADA), ESTE MÉTODO DARÁ UM ERRO.
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignKey()).build()
				.parseClaimsJws(token).getBody();
	}

	
	
	
	
	
	
	// ESTE É UM MÉTODO "ESPERTINHO". EM VEZ DE LER TUDO DO CRACHÁ SEMPRE,
	// ELE DEIXA VOCÊ PEDIR SÓ A INFORMAÇÃO QUE QUER.
	// VOCÊ DIZ: "ME DÊ SÓ O NOME DO DONO", E ELE PEGA SÓ ISSO PRA VOCÊ.
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	
	
	
	
	// AQUI USAMOS O MÉTODO "ESPERTINHO" PARA PEGAR INFORMAÇÕES ESPECÍFICAS.
	// EXTRACTUSERNAME: PEGA O NOME DO DONO DO CRACHÁ (O E-MAIL).
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	
	
	
	// EXTRACTEXPIRATION: PEGA A DATA DE VALIDADE ESCRITA NO CRACHÁ.
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	
	
	
	// ESTE MÉTODO OLHA O RELÓGIO. ELE PEGA A DATA DE VALIDADE DO CRACHÁ E COMPARA COM A HORA ATUAL.
	// SE A HORA ATUAL JÁ PASSOU DA VALIDADE, ELE AVISA QUE O CRACHÁ "VENCEU".
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	
	
	
	// ESTE É O "VERIFICADOR FINAL". ELE FAZ DUAS PERGUNTAS IMPORTANTES PARA SABER SE O CRACHÁ É BOM:
	// 1. "O NOME ESCRITO NESTE CRACHÁ É O MESMO NOME DESTA PESSOA QUE ESTÁ NA MINHA FRENTE?"
	// 2. "ESTE CRACHÁ AINDA ESTÁ DENTRO DA VALIDADE?"
	// SÓ SE AS DUAS RESPOSTAS FOREM "SIM", O CRACHÁ É CONSIDERADO VÁLIDO.
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String email = extractUsername(token);
		return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	
	
	
	// ESTA É A "MÁQUINA QUE FAZ O CRACHÁ".
	// ELA PEGA UM MOLDE EM BRANCO E VAI PREENCHENDO:
	private String createToken(Map<String, Object> claims, String userEmail) {
		return Jwts.builder()
				// SETCLAIMS: ADICIONA INFORMAÇÕES EXTRAS NO CRACHÁ (SE TIVER).
				.setClaims(claims)
				// SETSUBJECT: ESCREVE O NOME DO DONO (O E-MAIL).
				.setSubject(userEmail)
				// SETISSUEDAT: CARIMBA A DATA E HORA QUE O CRACHÁ FOI FEITO.
				.setIssuedAt(new Date(System.currentTimeMillis()))
				// SETEXPIRATION: CARIMBA A DATA E HORA QUE O CRACHÁ VAI VENCER (DAQUI A 1 HORA).
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
				// SIGNWITH: USA A "CHAVE DE METAL" (A CHAVE SECRETA) PARA DAR UMA ASSINATURA ÚNICA E SEGURA NO CRACHÁ.
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
				// COMPACT: FINALIZA E ENTREGA O CRACHÁ PRONTO, EM FORMATO DE TEXTO.
				.compact();
	}

	
	
	
	
	// ESTE É O BOTÃO "FAZER UM CRACHÁ NOVO" QUE AS OUTRAS PARTES DO PROGRAMA VÃO APERTAR.
	// ELE SIMPLESMENTE CHAMA A "MÁQUINA DE FAZER CRACHÁ" E PASSA O E-MAIL DA PESSOA.
	public String generateToken(String userEmail) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userEmail);
	}

}
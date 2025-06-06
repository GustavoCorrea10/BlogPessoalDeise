package com.gustavo.blogpessoaldeise.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gustavo.blogpessoaldeise.model.Usuario;

//CRIANDO UMA CLASSE QUE "IMPLEMENTA" (OU SEJA, SEGUE AS REGRAS DE UMA INTERFACE CHAMADA UerDetails)
public class UserDetailsImpl implements UserDetails {

	
	//VARIAVEL PRIVADA
	//QUE SERVE PARA DEFINIR A VERSÃO DESSE SISTEMA
	//1L = O 1 É VERSÃO DESSE SISTEMA E O "L" INDICA QUE O NÚMERO É DO TIPO LONG
	private static final long serialVersionUID = 1L;

	
	
	
	//VARIAVEIS QUE GUARDA O NOME DO USUARIO E SENHA
	private String userEmail;
	private String password;
	
	//PRIVADO
	//COM UMA LISTA
	//GrantedAuthority = REPRESENTA AS PERMISSÕES QUE CADA USUARIO VAI TER (EX: PERMISSÕES DE ADMIN, USER OU MODERADOR)
	//authorities = NOME DA VARIAVEL
	//RESUMO: authorities É DO TIPO DE UMA LISTA DE PERMISSÕES, ONDE ELE GUARDA TODOS OS TIPO DE PERMISSÕES QUE UM USUARIO PODE TER
	private List<GrantedAuthority> authorities;

	
	
	
	//PEGA O LOGIN DO USUSARIO E A SENHA E GUARDA NO UserDetailsImpl
	//PARA QUE ISSO: O SPRING SECURITY PRECISA DE UM OBJETO QUE TENHA O NOME DO USUARIO, SENHA E AS PERMISSÕES
	//PARA SABER SE O USUARIO PODE ENTRAR E O QUE ELE PODE ACESSAR DEPOIS DE LOGADO
	public UserDetailsImpl(Usuario email) {
		this.userEmail = email.getEmail();
		this.password = email.getSenha();
	}

	
	
	//CONSTRUTOR VAZIO
	//OBS: NÃO ENTENDI MUITO BEM O MOTIVO, ENTÃO SÓ DEIXAR ASSIM
	public UserDetailsImpl() {	}

	
	
	
	//@OVERRIDE = ESTÁ SOBESCREVENDO UM MÉTODO QUE JÁ EXISTE
	//ESSE MÉTODO VEM DA INTERFACE UserDetails
	@Override
	//Collection = VAI GUARDAR UMA COLEÇÃO DE PERMISSÕES DO USUARIO
	//? = SIGNIFICA QUALQUER TIPO
	//MAS NÃO VAI GUARDAR QUALQUER TIPO, VAI GUARDAR QUALQUER TIPO QUE SEJA GranteAuthority	OU QUE HERDE DELA
	//getAuthorities() = NOME DO METODO
	public Collection<? extends GrantedAuthority> getAuthorities() {

		//RETORNA UMA LISTA DE PERIMISSÕES DO USUARIO (TIPO ADMIN, USER, ETC...)
		return authorities;
		
		//QUANDO CHAMA O METODO getAuthorities, ELE DEVOLVE ESSA LISTA DE PERMISSÕES QUE ESTÁ GUARDADA NA VARIAVEL authorities
	}

	
	
	
	
	
	
	//INDICA QUE VOCE ESTÁ SOBRESCREVENDO UM METODO QUE VEM DA INTERFACE USERDETAILS
	//OU SEJA, VOCE ESTÁ ESCREVENDO O QUE ESSE MÉTODO REALMENTE FAZ NA SUA CLASSE
	@Override
	public String getPassword() {

		//RETORNA O CONTEÚDO DA VARIAVEL PASSWORD
		//QUE É A SENHA DO USUÁRIO
		return password;
	}

	@Override
	public String getUsername() {

		//RETORNA O NOME DO USUARIO QUE ESTÁ GUARDADA NA VARIAVEL userName
		return userEmail;
	}

	@Override
	// ESSE MÉTODO SOBRESCREVE O MÉTODO DA INTERFACE UserDetails
	public boolean isAccountNonExpired() {
		
	    // AQUI VOCÊ ESTÁ DIZENDO: SEMPRE QUE PERGUNTAREM SE A CONTA NÃO ESTÁ EXPIRADA,
	    // A RESPOSTA SERÁ TRUE, OU SEJA, A CONTA ESTÁ VÁLIDA, NÃO ESTÁ EXPIRADA.
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		
		// AQUI VOCÊ ESTÁ DIZENDO: SEMPRE QUE PERGUNTAREM SE A CONTA NÃO ESTÁ BLOQUEADA,
	    // A RESPOSTA SERÁ TRUE, OU SEJA, A CONTA NÃO ESTÁ BLOQUEADA E O USUÁRIO PODE ACESSAR.
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		// DIZ QUE A SENHA DO USUÁRIO AINDA ESTÁ VALIDA (NÃO EXPIROU)
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		// DIZ QUE A CONTA DO USUÁRIO ESTÁ ATIVA, OU SEJA, ELE TEM PERMISSÃO PARA ENTRAR NO SISTEMA
		// MAS ISSO NÃO SIGNIFICA QUE ELE JÁ ESTÁ LOGADO, APENAS QUE PODE FAZER LOGIN
		return true;
	}
	
	
	
	
	
	
	  
	 // RESUMO: ESTA CLASSE LIGA O USUÁRIO AO SPRING SECURITY.  
   // ELA GUARDA LOGIN, SENHA E PERMISSÕES DO USUÁRIO, 
   // E AJUDA O SPRING A FAZER O LOGIN E CHECAR SE A CONTA ESTÁ ATIVA.


}
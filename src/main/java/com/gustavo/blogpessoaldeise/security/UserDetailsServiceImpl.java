package com.gustavo.blogpessoaldeise.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavo.blogpessoaldeise.model.Usuario;
import com.gustavo.blogpessoaldeise.repository.UsuarioRepository;

//@Service = O SPRING (UM GERENTE DO SEU PROGRAMA) VAI CRIAR AUTOMATICAMENTE UM "PROFISSIONAL" (OBJETO) DESSA CLASSE.
//ISSO FACILITA SUA VIDA, POIS VOCÊ NÃO PRECISA SE PREOCUPAR EM CRIAR ESSE "PROFISSIONAL" SEMPRE QUE PRECISAR DELE.
//ELE FICA PRONTO PARA SER USADO EM OUTRAS PARTES DO CÓDIGO (COMO UM AJUDANTE SEMPRE À DISPOSIÇÃO).
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	// UserDetailsServiceImpl = É O NOME DESTA "RECEITA" OU "CLASSE".
	// public = QUALQUER PARTE DO SEU PROGRAMA PODE USAR ESTA CLASSE.
	// class = É A "PLANTA" OU "RECEITA" PARA CRIAR UM OBJETO (O PROFISSIONAL).
	// implements = SIGNIFICA QUE ESTA CLASSE ESTÁ "ASSINANDO UM CONTRATO".
	// UserDetailsService = É O "CONTRATO" QUE O SPRING SECURITY (O SEGURANÇA DO SEU PROGRAMA) EXIGE.
	// ESSE CONTRATO DIZ: "VOCÊ PRECISA SABER COMO ENCONTRAR OS DADOS DE UM USUÁRIO PARA O LOGIN!".

	
	
	
	
	
	@Autowired
	// @Autowired = ESSE É O ADESIVO MÁGICO QUE DIZ AO SPRING: "POR FAVOR, ENCONTRE A FERRAMENTA 'USUARIOREPOSITORY' E ME DÊ ELA AQUI."
	// O SPRING VAI COLOCAR AUTOMATICAMENTE O "AJUDANTE DO BANCO DE DADOS" AQUI.
	private UsuarioRepository usuarioRepository;
	// private = SÓ ESTA CLASSE PODE USAR DIRETAMENTE ESSE "AJUDANTE". É UM SEGREDO DELA.
	// UsuarioRepository = É O TIPO DO NOSSO "AJUDANTE". ELE SABE FALAR COM O BANCO DE DADOS SOBRE USUÁRIOS.
	// usuarioRepository = É O NOME QUE DAMOS AO NOSSO "AJUDANTE" AQUI DENTRO.


	
	
	
	
	
	
	
	
	
	// @Override = ESTE CARIMBO MOSTRA QUE ESTAMOS CUMPRINDO UMA EXIGÊNCIA DO "CONTRATO" (UserDetailsService).
	// O UserDetailsService EXIGE UM MÉTODO (UMA FUNÇÃO) CHAMADO loadUserByUsername.
	@Override
	public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
		// public = QUALQUER UM PODE CHAMAR ESSA FUNÇÃO.
		// UserDetails = É O TIPO DE "CARTÃO DE IDENTIDADE" QUE ESSA FUNÇÃO VAI DEVOLVER PARA O SPRING.
		// loadUserByUsername = ESSE É O NOME DA FUNÇÃO QUE O "CONTRATO" EXIGE. NÃO PODE MUDAR O NOME!
		// (String userName) = ESSA FUNÇÃO PRECISA RECEBER O "NOME DE USUÁRIO" (UM TEXTO) DE QUEM TENTA FAZER LOGIN.
		// throws UsernameNotFoundException = SE A FUNÇÃO NÃO ENCONTRAR O USUÁRIO, ELA VAI "GRITAR" ESSE ERRO.

		
		// BUSCAR O USUÁRIO NO BANCO DE DADOS:
		Optional<Usuario> email = usuarioRepository.findByEmail(userEmail);
		// Optional<Usuario> = UMA CAIXA QUE PODE TER UM "USUARIO" DENTRO OU ESTAR VAZIA.
		// ISSO EVITA ERROS CASO O USUÁRIO NÃO EXISTA. É MAIS SEGURO!
		// usuario = É O NOME QUE DAMOS PARA ESSA CAIXA ONDE VAI ESTAR O USUÁRIO (SE ENCONTRADO).
		// usuarioRepository.findByUsuario(userName) = PEDIMOS PARA O "AJUDANTE DO BANCO DE DADOS" (usuarioRepository)
		// PROCURAR UM USUÁRIO PELO "NOME DE USUÁRIO" (userName) QUE RECEBEMOS.
		
		// VERIFICA SE O USUÁRIO FOI ENCONTRADO:
		if (email.isPresent()) // SE A "CAIXA" (Optional) TEM UM USUÁRIO DENTRO (ELE EXISTE NO BANCO):
			// return = DEVOLVEMOS O RESULTADO.
			// new UserDetailsImpl(usuario.get()) = PEGAMOS O USUÁRIO QUE ENCONTRAMOS (usuario.get())
			// E O "VESTIMOS" NO "CARTÃO DE IDENTIDADE" (UserDetailsImpl) QUE O SPRING PRECISA.
			return new UserDetailsImpl(email.get());
		else // SE A "CAIXA" ESTIVER VAZIA (O USUÁRIO NÃO EXISTE NO BANCO):
			
			// throw = SIGNIFICA "GRITAR" OU "LANÇAR" UM ERRO.
			// new ResponseStatusException(HttpStatus.FORBIDDEN) = GRITAMOS UM ERRO DIZENDO QUE O "ACESSO É PROIBIDO" (CÓDIGO 403).
			// É COMO DIZER: "NÃO ENCONTREI ESSE MORADOR NA LISTA, ENTÃO ELE NÃO PODE ENTRAR!"
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			
	}
	
	// RESUMO FINAL DE TUDO:
	// ESTA CLASSE É O "PORTEIRO DO PRÉDIO".
	// 1. QUANDO ALGUÉM TENTA FAZER LOGIN COM UM NOME DE USUÁRIO, O "PORTEIRO" RECEBE ESSE NOME.
	// 2. ELE CONSULTA A "LISTA DE MORADORES" (O BANCO DE DADOS) PARA VER SE ESSE NOME EXISTE.
	// 3. SE O MORADOR EXISTE, O PORTEIRO PEGA OS DETALHES DELE E PASSA PARA O "SEGURANÇA" (SPRING SECURITY) NO FORMATO CERTO.
	// 4. SE O MORADOR NÃO EXISTE NA LISTA, O PORTEIRO GRITA: "ACESSO PROIBIDO!" (ERRO 403).
}

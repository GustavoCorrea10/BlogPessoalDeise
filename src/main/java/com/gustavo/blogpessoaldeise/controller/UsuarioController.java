package com.gustavo.blogpessoaldeise.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.gustavo.blogpessoaldeise.model.Usuario;
import com.gustavo.blogpessoaldeise.model.UsuarioLogin;
import com.gustavo.blogpessoaldeise.model.UsuarioResponseDTO;
import com.gustavo.blogpessoaldeise.repository.UsuarioRepository;
import com.gustavo.blogpessoaldeise.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@GetMapping
	public ResponseEntity<List<UsuarioResponseDTO>> getAll() {
		return ResponseEntity.ok(usuarioService.listarTodos());
	}

	
	
	
	
	
	@PostMapping("/cadastrar")
	public ResponseEntity<UsuarioResponseDTO> post(@Valid @RequestBody Usuario usuario) {
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criarUsuario(usuario));
	}

	
	
	

	@PostMapping("/logar")
	public ResponseEntity<UsuarioLogin> logar(@RequestBody UsuarioLogin usuarioLogin) {
	    
	    // CHAMA O SERVIÇO DE AUTENTICAÇÃO E ESPERA UMA RESPOSTA.
	    return usuarioService.autenticarUsuario(usuarioLogin)
	        // SE O SERVIÇO RETORNAR UM OBJETO (LOGIN COM SUCESSO),
	        // O '.map' TRANSFORMA ISSO EM UMA RESPOSTA '200 OK' COM OS DADOS.
	        .map(resposta -> ResponseEntity.ok(resposta))
	        
	        // SE O SERVIÇO RETORNAR VAZIO (LOGIN FALHOU),
	        // O '.orElse' CRIA UMA RESPOSTA '401 UNAUTHORIZED' (NÃO AUTORIZADO).
	        .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}
	
	
	
	
	
	
    // O MÉTODO 'PUT' AGORA RECEBE O 'PRINCIPAL'
	//PRINCIPAL É COMO SE FOSSE O CRACHÁ DE QUEM ESTÁ LOGADO - ELE CONTEM A INDENTIDADE
	//(GERALMENTE O EMAIL OU USERNAME) DA PESSOA AUTENTICADA
	@PutMapping("/editar")
	public ResponseEntity<UsuarioResponseDTO> put(@Valid @RequestBody Usuario usuario, Principal principal) {
		
        // 1. PEGA O E-MAIL DO USUARIO QUE ESTÁ NO TOKEN JWT (LOGADO)
		String emailUsuarioLogado = principal.getName();
		
        // 2. BUSCA O OBJETO COMPLETO DO USUARIO USANDO O EMAIL QUE VEIO NO emailUsuarioLogado
		//SE NÃO ENCONTRAR NINGUEM COM ESSE EMAIL, LANÇA ERRO 401 - NÃO AUTENTICADO
		Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuarioLogado)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario não autenticado."));
		
		
		// 3. CHAMA O SERVIÇO, AGORA PASSANDO OS DOIS PARÂMETROS NECESSÁRIOS:
        // O OBJETO 'USUARIO' COM OS DADOS PARA ATUALIZAR E O usuarioLogado.getId()
		//QUE CONTEM SÓ O ID DO USUARIO
		return ResponseEntity.ok(usuarioService.atualizarUsuario(usuario, usuarioLogado.getId()));
	}

	
	
	
	
	
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id, Principal principal) {
		
		String emailUsuarioLogado = principal.getName();
		
		Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuarioLogado)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario não autenticado."));
		
		 usuarioService.deletarUsuario(id, usuarioLogado.getId() );
	}

}

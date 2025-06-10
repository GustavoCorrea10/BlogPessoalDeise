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

import com.gustavo.blogpessoaldeise.model.Postagem;
import com.gustavo.blogpessoaldeise.model.PostagemResponseDTO;
import com.gustavo.blogpessoaldeise.model.Usuario;
import com.gustavo.blogpessoaldeise.repository.UsuarioRepository;
import com.gustavo.blogpessoaldeise.service.PostagemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagem")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

	@Autowired
	private PostagemService postagemService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	
	
	
	@GetMapping
	public ResponseEntity<List<PostagemResponseDTO>> getAll() {
		return ResponseEntity.ok(postagemService.listarPostagem());
	}

	
	
	
	
	
	
	
	@PostMapping("/criar")
	public ResponseEntity<PostagemResponseDTO> post(@RequestBody @Valid Postagem postagem, Principal principal) {
		
        // 1. PEGA O E-MAIL DO USUARIO QUE ESTÁ NO TOKEN JWT (LOGADO)
	    // O 'principal' É UM OBJETO AUTOMÁTICO QUE REPRESENTA O USUÁRIO AUTENTICADO.
		String emaiUsuariolLogado = principal.getName();
		
		
		// 2. BUSCA O USUÁRIO COMPLETO NO BANCO DE DADOS USANDO O E-MAIL.
		Usuario autor = usuarioRepository.findByEmail(emaiUsuariolLogado)
			    // SE NÃO ENCONTRAR, LANÇA ERRO 401 (NÃO AUTORIZADO).
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario não autenticado."));
		
	    // 3. DEFINE O USUÁRIO LOGADO COMO AUTOR DA POSTAGEM.
		// ENTÃO, USAMOS O setUsuario() PARA ASSOCIAR O USUÁRIO LOGADO À POSTAGEM.
		// O 'autor' É A VARIÁVEL QUE GUARDA O USUÁRIO LOGADO (PEGANDO PELO EMAIL DO JWT).
		// ASSIM, A GENTE GARANTE QUE A POSTAGEM FICA VINCULADA AO USUÁRIO CERTO.
		postagem.setUsuario(autor);

		// 4. CHAMA O SERVICE PARA SALVAR A POSTAGEM NO BANCO DE DADOS,
	    // E DEVOLVE A POSTAGEM SALVA COM O CÓDIGO HTTP 201 (CRIADO).
		return ResponseEntity.status(HttpStatus.CREATED).body(postagemService.criarPostagem(postagem));
	}

	
	
	
	
	
	
	
	
	@PutMapping("/editar")
	public ResponseEntity<PostagemResponseDTO> put(@RequestBody @Valid Postagem postagem, Principal principal) {
		
		
		// PEGAMOS O E-MAIL DO USUÁRIO LOGADO USANDO O OBJETO 'PRINCIPAL'
	    // O MÉTODO .GETNAME() RETORNA O NOME OU E-MAIL USADO PARA LOGIN (DEPENDENDO DE COMO O SPRING SECURITY FOI CONFIGURADO)
		String emailUsuarioLogado = principal.getName();
		
		
		// PASSO 3: BUSCAR O OBJETO 'USUARIO' COMPLETO PARA PEGAR O ID
		Usuario autor = usuarioRepository.findByEmail(emailUsuarioLogado)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado."));
		
		// PASSO 4: CHAMAR O SERVIÇO PASSANDO O ID (LONG), E NÃO O E-MAIL (STRING)
		// O MÉTODO DO SERVIÇO AGORA RECEBE UM 'LONG' E RETORNA UM 'PostagemResponseDTO'
		return ResponseEntity.ok(postagemService.editarPostagem(postagem, autor.getId()));
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Principal principal) {
		
		String emailUsuarioLogado = principal.getName();
		
		Usuario autor = usuarioRepository.findByEmail(emailUsuarioLogado)
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario não autenticado."));
		
		
		postagemService.deletarPostagem(id, autor.getId());
	}

}

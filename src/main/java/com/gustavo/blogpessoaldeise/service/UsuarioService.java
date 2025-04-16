package com.gustavo.blogpessoaldeise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavo.blogpessoaldeise.model.Usuario;
import com.gustavo.blogpessoaldeise.repository.UsuarioRepository;

@Service
public class UsuarioService {

	
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public ResponseEntity<List<Usuario>> listarTodos(){
		return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.findAll());
	}
	
	
	public ResponseEntity<Usuario> atualizarUsuario(Usuario usuario){
		return usuarioRepository.findById(usuario.getId())
				.map(resposta -> ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(usuario)))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	public ResponseEntity<Usuario> criarUsuario(Usuario usuario) {
	    return ResponseEntity.status(HttpStatus.CREATED)
	    		.body(usuarioRepository.save(usuario));
	}
	
	public void deletarUsuario(Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		if(usuario.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		usuarioRepository.deleteById(id);
	}
}

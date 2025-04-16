package com.gustavo.blogpessoaldeise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavo.blogpessoaldeise.model.Postagem;
import com.gustavo.blogpessoaldeise.repository.PostagemRepository;
import com.gustavo.blogpessoaldeise.repository.UsuarioRepository;

@Service
public class PostagemService {

	
	@Autowired
	private PostagemRepository postagemRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	
	
	public ResponseEntity<List<Postagem>> listarPostagem(){
		return ResponseEntity.status(HttpStatus.OK).body(postagemRepository.findAll());
	}
	
	
	
	public ResponseEntity<Postagem> criarPostagem(Postagem postagem){
		if(usuarioRepository.existsById(postagem.getUsuario().getId()))
			return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario não existe!");

	}
	
	
	
	public ResponseEntity<Postagem> editarPostagem(Postagem postagem){
		
		//PEGA O ID DA POSTAGEM PARA VERIFICAR SE ELA EXISTE
		if(postagemRepository.existsById(postagem.getId())) {
			
			if(usuarioRepository.existsById(postagem.getUsuario().getId()))
				
			return ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem));
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario não existe");
		}
		
		//SE A POSTAGEM NÃO EXISTIR
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Postagem não existe");
		
	}
	
	
	
	public void deletarPostagem(Long id){
		Optional<Postagem> postagem = postagemRepository.findById(id);
		if(postagem.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Postagem não encontrada!");
		}
		
		postagemRepository.deleteById(id);
	}
}

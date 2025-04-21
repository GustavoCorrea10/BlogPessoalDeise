package com.gustavo.blogpessoaldeise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

	public List<Postagem> listarPostagem() {
		return postagemRepository.findAll();
	}

	public Postagem criarPostagem(Postagem postagem) {
		if (usuarioRepository.existsById(postagem.getUsuario().getId())) {
			return postagemRepository.save(postagem);

		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario não existe!");

	}

	public Postagem editarPostagem(Postagem postagem) {

		// VERIFICA SE A POSTAGEM EXISTE(SE EXISTE, ELE CAI NO RETURN, SE NÃO, CAI NO
		// THROW NEW)
		if (!postagemRepository.existsById(postagem.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Postagem não existe");
		}

		// VERIFICA SE O USUARIO EXISTE
		if (!usuarioRepository.existsById(postagem.getUsuario().getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não existe");
		}

		// CE TUDO EXISTIR, CAI NO RETURN E SALVA (ATUALIZA) O USUARIO
		return postagemRepository.save(postagem);
	}

	public void deletarPostagem(Long id) {
		Optional<Postagem> postagem = postagemRepository.findById(id);
		if (postagem.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Postagem não encontrada!");
		}

		postagemRepository.deleteById(id);
	}
}

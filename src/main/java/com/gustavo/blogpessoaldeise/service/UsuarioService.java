package com.gustavo.blogpessoaldeise.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavo.blogpessoaldeise.model.Usuario;
import com.gustavo.blogpessoaldeise.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	public Usuario criarUsuario(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public Usuario atualizarUsuario(Usuario usuario) {
		// VERIFICA SE O USUARIO EXISTE
		if (!usuarioRepository.existsById(usuario.getId())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrado");
		}

		// SE EXISTE, ELE CAI NO RETURN E SALVA (ATUALIZA)
		return usuarioRepository.save(usuario);

	}

	public void deletarUsuario(Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);

		// isEmpty = O USUARIO ESTÁ VAZIO?
		// SE SIM, LANÇA O THROW NEW
		if (usuario.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não existe!");
		}

		// SE NÃO ESTIVER VAZIO, DELETA O USUARIO
		usuarioRepository.deleteById(id);
	}
}

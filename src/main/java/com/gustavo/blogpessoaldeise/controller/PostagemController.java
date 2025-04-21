package com.gustavo.blogpessoaldeise.controller;

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

import com.gustavo.blogpessoaldeise.model.Postagem;
import com.gustavo.blogpessoaldeise.service.PostagemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagem")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

	@Autowired
	private PostagemService postagemService;

	@GetMapping
	public ResponseEntity<List<Postagem>> getAll() {
		return ResponseEntity.ok(postagemService.listarPostagem());
	}

	@PostMapping("/criar")
	public ResponseEntity<Postagem> post(@RequestBody @Valid Postagem postagem) {
		return ResponseEntity.status(HttpStatus.CREATED).body(postagemService.criarPostagem(postagem));
	}

	@PutMapping("/editar")
	public ResponseEntity<Postagem> put(@RequestBody @Valid Postagem postagem) {
		return ResponseEntity.ok(postagemService.editarPostagem(postagem));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
		postagemService.deletarPostagem(id);
	}

}

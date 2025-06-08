package com.gustavo.blogpessoaldeise.model;

import java.time.LocalDateTime;

public class PostagemResponseDTO {

	private Long id;
	private String texto;
	private String fotoPostagem;
	private LocalDateTime dataPostagem;
	private UsuarioResponseDTO usuario;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getFotoPostagem() {
		return fotoPostagem;
	}

	public void setFotoPostagem(String fotoPostagem) {
		this.fotoPostagem = fotoPostagem;
	}

	public LocalDateTime getDataPostagem() {
		return dataPostagem;
	}

	public void setDataPostagem(LocalDateTime dataPostagem) {
		this.dataPostagem = dataPostagem;
	}

	public UsuarioResponseDTO getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioResponseDTO usuario) {
		this.usuario = usuario;
	}
	
	
}

package com.gustavo.blogpessoaldeise.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_postagem")
public class Postagem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O titulo é obrigatório!")
	@Size(min = 5, max = 100, message = "Digite no minimo 5 e no maximo 100 caracteres")
	private String titulo;

	@NotBlank(message = "o campo é obrigatório")
	@Size(min = 10, max = 1000, message = "Digite no minimo 10 e no maximo 1000 caracteres")
	private String texto;

	@UpdateTimestamp
	private LocalDateTime dataPostagem;

	@ManyToOne
	@JsonIgnoreProperties("postagem")
	private Usuario usuario;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public LocalDateTime getDataPostagem() {
		return dataPostagem;
	}

	public void setDataPostagem(LocalDateTime dataPostagem) {
		this.dataPostagem = dataPostagem;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}

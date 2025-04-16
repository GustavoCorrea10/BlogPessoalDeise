package com.gustavo.blogpessoaldeise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gustavo.blogpessoaldeise.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

}

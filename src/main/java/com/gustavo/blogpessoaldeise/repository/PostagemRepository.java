package com.gustavo.blogpessoaldeise.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gustavo.blogpessoaldeise.model.Postagem;

public interface PostagemRepository extends JpaRepository<Postagem, Long>{

}

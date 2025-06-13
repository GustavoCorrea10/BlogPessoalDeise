package com.gustavo.blogpessoaldeise.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavo.blogpessoaldeise.model.Postagem;
import com.gustavo.blogpessoaldeise.model.PostagemResponseDTO;
import com.gustavo.blogpessoaldeise.model.UsuarioResponseDTO;
import com.gustavo.blogpessoaldeise.repository.PostagemRepository;
import com.gustavo.blogpessoaldeise.repository.UsuarioRepository;

@Service
public class PostagemService {

	@Autowired
	private PostagemRepository postagemRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	
	
	
	
	// ESTE MÉTODO PROMETE DEVOLVER UMA LISTA DE "POSTAGENS SEGURAS" (DTO).
	public List<PostagemResponseDTO> listarPostagem() {
			
		// PASSO 1: VAI ATÉ O BANCO DE DADOS E PEGA TODAS AS POSTAGENS.
	    // ELAS VÊM COMPLETAS, COM TODOS OS DADOS, INCLUSIVE OS DADOS DO AUTOR.
		List<Postagem> postagens = postagemRepository.findAll();
			
	    // PASSO 2: AQUI COMEÇA A "MÁGICA" DA CONVERSÃO.
	    // '.STREAM()': COLOCA TODAS AS POSTAGENS COMPLETAS EM UMA "ESTEIRA DE PRODUÇÃO".
	    // '.MAP(...)': É UM "ROBÔ" NA ESTEIRA QUE VAI TRANSFORMAR CADA POSTAGEM, UMA POR UMA.
		//E ESSA NOVA VERSÃO VAI FICAR GUARDADA NA VARIAVEL DTOS
		List<PostagemResponseDTO> dtos = postagens.stream().map(postagem ->{
				
			// PARA CADA 'POSTAGEM' QUE O ROBÔ PEGA NA ESTEIRA, ELE FAZ O SEGUINTE:

			// A. PEGA UMA "EMBALAGEM NOVA E SEGURA" PARA A POSTAGEM.
			PostagemResponseDTO postagemDTO = new PostagemResponseDTO();
			
			
			
	        // B. COPIA AS INFORMAÇÕES DA POSTAGEM ORIGINAL PARA A EMBALAGEM NOVA.
			postagemDTO.setId(postagem.getId());
			postagemDTO.setTexto(postagem.getTexto());
			postagemDTO.setFotoPostagem(postagem.getFotoPostagem());
			postagemDTO.setDataPostagem(postagem.getDataPostagem());
				
				
			
			
			// C. VERIFICA SE EXISTE UM AUTOR NESSA POSTAGEM.
			//SE ESSA POSTAGEM TEM UM USUARIO(OU SEJA, SE GETUSUARIO() NÃO FOR NULL) FAÇA ALGUMA COISA
			if(postagem.getUsuario() != null) {
					
				// D. SE EXISTIR UM USUARIO NA POSTAGEM, CRIA UMA "EMBALANGEM SEGURA" (DTO) PARA ELE
				UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO();
					
	            // E. COPIA SÓ OS DADOS PÚBLICOS DO USUARIO PARA ESSA EMBALAGEM MENOR (SEM A SENHA!).
				//NÃO COPIA SENHA, NEM DADOS SENSÍVEIS
				usuarioDTO.setId(postagem.getUsuario().getId());          //PEGA O ID DO USUARIO
				usuarioDTO.setNome(postagem.getUsuario().getNome());      //PEGA O NOME DO USUARIO
				usuarioDTO.setEmail(postagem.getUsuario().getEmail());    //PEGA O EMAIL DO USUARIO
				usuarioDTO.setFoto(postagem.getUsuario().getFoto());      //PEGA A FOTO DO USUARIO
					
				// F. DEPOIS DE MONTAR A EMBALAGEM DO USUARIO, COLOCA ELA DENTRO DA POSTAGEM (DTO)
				postagemDTO.setUsuario(usuarioDTO);
			}
				
	        // G. DEVOLVE A POSTAGEM DTO COMPLETA, PRONTA E SEGURA PARA SER MOSTRADA
			return postagemDTO;

			// ESSA PARTE AQUI EMBAIXO É ONDE O STREAM FAZ A TRANSFORMAÇÃO DE TODAS AS POSTAGENS
			// ELE VAI RODAR ISSO PARA CADA POSTAGEM QUE EXISTE, E JUNTAR TUDO EM UMA LISTA FINAL:
		}).collect(Collectors.toList());
			
		
		// POR FIM, DEVOLVE A LISTA COM TODAS AS POSTAGENS JÁ PRONTAS E SEGURAS
		return dtos;
	}

	
	
	
	
	
	
	
	
	
	
	public PostagemResponseDTO criarPostagem(Postagem postagem) { 
		
	    // 1. SALVA A POSTAGEM NO BANCO DE DADOS E GUARDA O OBJETO COMPLETO COM ID, DATA E USUÁRIO.
			Postagem novaPostagem = postagemRepository.save(postagem);
			
		    // 2. CRIA UMA "EMBALAGEM SEGURA" SÓ COM OS DADOS QUE VÃO PARA O USUÁRIO (PostagemResponseDTO).
			PostagemResponseDTO postagemDTO = new PostagemResponseDTO();
			
			
		    // 3. COPIA OS DADOS QUE PODEM SER MOSTRADOS PUBLICAMENTE DA POSTAGEM:
			postagemDTO.setId(novaPostagem.getId());
			postagemDTO.setTexto(novaPostagem.getTexto());
			postagemDTO.setFotoPostagem(novaPostagem.getFotoPostagem());
			postagemDTO.setDataPostagem(novaPostagem.getDataPostagem());
			
			
		    // 4. CRIA UMA OUTRA EMBALAGEM PARA O USUARIO DA POSTAGEM (SEM SENHA!).
			UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO();
			
		    // 5. COPIA APENAS OS DADOS PÚBLICOS DO USUARIO SEM A SENHA:
			usuarioDTO.setId(novaPostagem.getUsuario().getId());
			usuarioDTO.setNome(novaPostagem.getUsuario().getNome());
			usuarioDTO.setEmail(novaPostagem.getUsuario().getEmail());
			usuarioDTO.setFoto(novaPostagem.getUsuario().getFoto());

			// F. DEPOIS DE MONTAR A EMBALAGEM DO USUARIO, COLOCA ELA DENTRO DA POSTAGEM (DTO)
			postagemDTO.setUsuario(usuarioDTO);
			
		    // 7. DEVOLVE A POSTAGEM JÁ MONTADA E SEGURA PARA SER EXIBIDA NA API.
			return postagemDTO;
	}

	
	
	
	
	
	public PostagemResponseDTO editarPostagem(Postagem postagem, Long idUsuarioLogado) {

	    // PASSO 1 E 2: BUSCAR A POSTAGEM ORIGINAL NO BANCO PELO ID DELA
	    // USAMOS O .FINDBYID PARA PROCURAR A POSTAGEM QUE TEM O MESMO ID DA POSTAGEM RECEBIDA
	    // SE NÃO ENCONTRAR, JÁ LANÇA UM ERRO 404 (NOT FOUND) E PARA A EXECUÇÃO AQUI
	    Postagem postagemOriginal = postagemRepository.findById(postagem.getId())
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postagem não encontrada!"));

	    // PASSO 3: VERIFICAR SE QUEM ESTÁ TENTANDO EDITAR É O DONO DA POSTAGEM
	    // PEGAMOS O USUÁRIO DONO DA POSTAGEM ORIGINAL E COMPARAMOS COM O ID DO USUÁRIO LOGADO
	    // SE FOR DIFERENTE (!), LANÇAMOS UM ERRO 403 (FORBIDDEN), NEGANDO O ACESSO
	    // PEGAMOS O USUÁRIO ASSOCIADO À POSTAGEM ORIGINAL COM .GETUSUARIO(), QUE REPRESENTA O DONO DA POSTAGEM,
	    // E USAMOS .GETID() PARA PEGAR O ID DESSE USUÁRIO.
	    // DEPOIS COMPARAMOS COM O ID DO USUÁRIO LOGADO (IDUSUARIOLOGADO) PARA VERIFICAR
	    // SE QUEM ESTÁ TENTANDO EDITAR A POSTAGEM É REALMENTE O DONO DELA.
	    if (!postagemOriginal.getUsuario().getId().equals(idUsuarioLogado)) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "O usuário não tem permissão para editar esta postagem!");
	    }

	    // PASSO 3.1: ATUALIZAR OS CAMPOS DA POSTAGEM ORIGINAL COM OS NOVOS DADOS
	    // AGORA QUE SABEMOS QUE A POSTAGEM EXISTE E QUE O USUÁRIO TEM PERMISSÃO,
	    // PEGAMOS OS NOVOS DADOS (TEXTO E FOTO) ENVIADOS NA POSTAGEM E APLICAMOS NA POSTAGEM ORIGINAL
	    // USAMOS OS MÉTODOS .SETTEXTO(...) E .SETFOTOPOSTAGEM(...) PARA ATUALIZAR ESSES CAMPOS
	    postagemOriginal.setTexto(postagem.getTexto());
	    postagemOriginal.setFotoPostagem(postagem.getFotoPostagem());

	    // PASSO 4: SALVAR A POSTAGEM EDITADA
	    // JÁ QUE PASSOU PELA VERIFICAÇÃO, SALVAMOS A POSTAGEM QUE VEIO COM OS DADOS NOVOS
	    // SALVAMOS A POSTAGEM EDITADA USANDO O REPOSITÓRIO. O MÉTODO .SAVE(...) ATUALIZA A POSTAGEM NO BANCO,
	    // E RETORNA O OBJETO ATUALIZADO, QUE É ARMAZENADO NA VARIÁVEL POSTAGEMATUALIZADA.
	    Postagem postagemAtualizada = postagemRepository.save(postagemOriginal);

	    // PASSO 5: CRIAR UM OBJETO DTO PARA RETORNAR AO CLIENTE
	    // O DTO (DATA TRANSFER OBJECT) É UMA FORMA SEGURA DE ENVIAR DADOS PELA API
	    // AQUI CRIAMOS O DTO E PREENCHERMOS COM OS DADOS DA POSTAGEM SALVA
	    PostagemResponseDTO postagemDTO = new PostagemResponseDTO();
	    postagemDTO.setId(postagemAtualizada.getId());
	    postagemDTO.setTexto(postagemAtualizada.getTexto());
	    postagemDTO.setFotoPostagem(postagemAtualizada.getFotoPostagem());
	    postagemDTO.setDataPostagem(postagemAtualizada.getDataPostagem());

	    // PASSO 6: SE A POSTAGEM TIVER UM USUÁRIO, CRIAMOS UM DTO PARA O USUÁRIO TAMBÉM
	    // ISSO É ÚTIL PARA ENVIAR AS INFORMAÇÕES DO AUTOR DA POSTAGEM DE FORMA SEGURA
	    if (postagemAtualizada.getUsuario() != null) {
	        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO();
	        usuarioDTO.setId(postagemAtualizada.getUsuario().getId());
	        usuarioDTO.setNome(postagemAtualizada.getUsuario().getNome());
	        usuarioDTO.setEmail(postagemAtualizada.getUsuario().getEmail());
	        usuarioDTO.setFoto(postagemAtualizada.getUsuario().getFoto());
	        postagemDTO.setUsuario(usuarioDTO);
	    }

	    // POR FIM, RETORNAMOS O DTO COM A POSTAGEM ATUALIZADA E OS DADOS DO USUÁRIO
	    return postagemDTO;
	}



	
	
	
	
	
	
	
	
	public void deletarPostagem(Long id, Long idusuarioLogado) {
		
		Postagem postagemOriginal = postagemRepository.findById(id)
		.orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "Postagem não encontrada!"));
		
		
		if(!postagemOriginal.getUsuario().getId().equals(idusuarioLogado)) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "O usuário não tem permissão para deletar esta postagem!");
		}

		postagemRepository.deleteById(id);
	}
}

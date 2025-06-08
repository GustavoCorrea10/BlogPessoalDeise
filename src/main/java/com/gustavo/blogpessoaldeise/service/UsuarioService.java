package com.gustavo.blogpessoaldeise.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavo.blogpessoaldeise.model.Usuario;
import com.gustavo.blogpessoaldeise.model.UsuarioResponseDTO;
import com.gustavo.blogpessoaldeise.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	// MÉTODO CORRIGIDO
		public List<UsuarioResponseDTO> listarTodos() {		
			
	        // 1. PEGA A LISTA DE USUÁRIOS COMPLETOS DO BANCO (AS "BANANAS")
			List<Usuario> usuarios = usuarioRepository.findAll();

			
			
			
	        // 2. CONVERTE CADA 'USUARIO' DA LISTA EM UM 'USUARIORESPONSEDTO'
	        // PENSE no .stream() COMO UMA "ESTEIRA DE PRODUÇÃO".
			List<UsuarioResponseDTO> dtos = usuarios.stream()
				.map(usuario -> { // .map() é o "OPERÁRIO" QUE TRANSFORMA CADA ITEM DA ESTEIRA.
					
					
					
					
	                // PARA CADA 'USUARIO' QUE CHEGA CHEGA NA ESTEIRA...
					UsuarioResponseDTO dto = new UsuarioResponseDTO(); // PEGA UMA "MAÇA" VAZIA.
					

					
					
	                // COPIA OS DADOS DO USUARIO PARA O DTO.
					dto.setId(usuario.getId());
					dto.setNome(usuario.getNome());
					dto.setEmail(usuario.getEmail());
					dto.setFoto(usuario.getFoto());
	                // NOTE QUE A SENHA NÃO É COPIADA.

					return dto; // DEVOLVE A "MAÇA" PREENCHIDA.

				})
				
				
				.collect(Collectors.toList()); // .collect() JUNTA TODAS AS "MAÇAS" EM UMA NOVA CAIXA.

	        // 3. RETORNA A NOVA LISTA DE DTOS (A CAIXA DE "MAÇÃS")
			return dtos;
		}
	







//O METODO AGORA PROMETE RETORNAR UM DTO SEGURO
        public UsuarioResponseDTO criarUsuario(Usuario usuario) {
        	
        	
		Optional<Usuario> usuarioExiste = usuarioRepository.findByEmail(usuario.getEmail());

				
		if(usuarioExiste.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário (e-mail) já existe!");
		}
		
		
		Usuario novoUsuario = usuarioRepository.save(usuario);
		
		UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();
		
		usuarioResponseDTO.setId(novoUsuario.getId());
		usuarioResponseDTO.setNome(novoUsuario.getNome());
		usuarioResponseDTO.setEmail(novoUsuario.getEmail());
		usuarioResponseDTO.setFoto(novoUsuario.getFoto());
		
		
		return usuarioResponseDTO; 
	}
        
        
        
        
        
        
        
        
        

	public UsuarioResponseDTO atualizarUsuario(Usuario usuario, Long idUsuarioLogado) {
		
		
		 // AQUI ESTÁ A NOVA REGRA DE SEGURANÇA (AUTORIZAÇÃO)
	    // VERIFICAMOS SE O ID DO USUÁRIO QUE VEIO NO CORPO DA REQUISIÇÃO ('usuario.getId()')
	    // É O MESMO ID DO USUÁRIO QUE ESTÁ LOGADO ('idUsuarioLogado').
		//.equals = É UM METODO QUE COMPARA O CONTEUDO DE DOIS OBJETOS PARA SABER SE ELES SÃO IGUAIS DE VERDADE
	    if (!usuario.getId().equals(idUsuarioLogado)) {
	    	
	    	
	        // SE NÃO FOREM IGUAIS, LANÇAMOS UM ERRO 403 - FORBIDDEN (ACESSO PROIBIDO).
	        // É O SISTEMA DIZENDO: "VOCÊ NÃO TEM PERMISSÃO PARA MEXER NAS COISAS DE OUTRA PESSOA."
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "O usuário não tem permissão para atualizar os dados de outro usuário!");
	    }
	    
	    
	    
		
		// VERIFICA SE O USUARIO EXISTE PELO ID
		if (!usuarioRepository.existsById(usuario.getId())) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrado");
		}
				
		Optional<Usuario> usuarioExiste = usuarioRepository.findByEmail(usuario.getEmail());
				
		if(usuarioExiste.isPresent() && !usuarioExiste.get().getId().equals(usuario.getId())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail informado já está em uso por outro usuário.");

		}

		Usuario novoUsuario = usuarioRepository.save(usuario);
		
		UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();
		
		usuarioResponseDTO.setId(novoUsuario.getId());
		usuarioResponseDTO.setNome(novoUsuario.getNome());
		usuarioResponseDTO.setEmail(novoUsuario.getEmail());
		usuarioResponseDTO.setFoto(novoUsuario.getFoto());
				
		return usuarioResponseDTO;
	}
	
	
	
	
	
	
	
	

	public void deletarUsuario(Long id, Long idUsuarioLogado) {
		
		
		
		//SE O ID A SER DELETADO É IGUAL AO ID DE QUEM ESTÁ LOGADO
		//ELE DELETA
        if (!id.equals(idUsuarioLogado)) {
	    	
	    	//SE FOR DIFERENTE, ELE NÃO DEIXA EXCLUIR
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "O usuário não tem permissão para deletar os dados de outro usuário!");
	    }
		
		//VE SE O USUARIO EXISTE PELO ID
		Optional<Usuario> usuarioId = usuarioRepository.findById(id);

		// isEmpty = O USUARIO ESTÁ VAZIO?
		// SE SIM, LANÇA O THROW NEW
		//SE O ID NÃO EXISTIR, LANÇA UM ERRO
		if (usuarioId.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não existe!");
		}

		// SE EXISTIR, DELETA O USUARIO
	usuarioRepository.deleteById(id);
	}
}

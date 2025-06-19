package com.gustavo.blogpessoaldeise.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gustavo.blogpessoaldeise.model.Usuario;
import com.gustavo.blogpessoaldeise.model.UsuarioLogin;
import com.gustavo.blogpessoaldeise.model.UsuarioResponseDTO;
import com.gustavo.blogpessoaldeise.repository.UsuarioRepository;
import com.gustavo.blogpessoaldeise.security.JwtService;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
	
		
		
		
		
		
		// ESTE MÉTODO É O "ESPECIALISTA EM LOGIN".
		// ELE RECEBE OS DADOS DE LOGIN E PROMETE DEVOLVER UM 'OPTIONAL' DE 'USUARIOLOGIN'.
		// 'OPTIONAL' SIGNIFICA QUE ELE PODE DEVOLVER O USUÁRIO LOGADO OU NADA (SE O LOGIN FALHAR).
		public Optional<UsuarioLogin> autenticarUsuario(UsuarioLogin usuarioLogin) {

		    // PASSO 1: PREPARAR AS CREDENCIAIS PARA O SEGURANÇA.
		    // AQUI, PEGAMOS O E-MAIL E A SENHA QUE O USUÁRIO ENVIOU E COLOCAMOS EM UM "PACOTE" PADRÃO
		    // QUE O SPRING SECURITY ENTENDE, CHAMADO 'USERNAMEPASSWORDAUTHENTICATIONTOKEN'.
			var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.getEmail(), usuarioLogin.getSenha());

		    // PASSO 2: ENTREGAR AS CREDENCIAIS PARA O GERENTE DE AUTENTICAÇÃO.
		    // ESTA LINHA CHAMA O 'AUTHENTICATIONMANAGER' E DIZ: "VERIFIQUE SE ESTE E-MAIL E SENHA SÃO VÁLIDOS!".
		    // SE O LOGIN OU A SENHA ESTIVEREM ERRADOS, O PROGRAMA PARA AQUI E DÁ UM ERRO.
		    // SE O CÓDIGO CONTINUAR PARA A PRÓXIMA LINHA, SIGNIFICA QUE O LOGIN FOI UM SUCESSO.
			authenticationManager.authenticate(credenciais);
				    
		    // PASSO 3: BUSCAR OS DADOS COMPLETOS DO USUÁRIO NO BANCO.
		    // AGORA QUE SABEMOS QUE O LOGIN É VÁLIDO, VAMOS AO BANCO DE DADOS PEGAR AS OUTRAS
		    // INFORMAÇÕES DO USUÁRIO (ID, NOME, FOTO) PARA MOSTRAR NA RESPOSTA.
			Optional<Usuario> usuario = usuarioRepository.findByEmail(usuarioLogin.getEmail());
				    
		    // PASSO 4: VERIFICAR SE ENCONTRAMOS O USUÁRIO NO BANCO.
		    // 'ISPRESENT()' PERGUNTA: "A BUSCA NO BANCO REALMENTE RETORNOU UM USUÁRIO?".
			if(usuario.isPresent()){
		        
		        // A. SE ENCONTRAMOS, CHAMAMOS NOSSA "FÁBRICA DE CRACHÁS" (JWTSERVICE) PARA GERAR UM NOVO TOKEN JWT.
			    String token = jwtService.generateToken(usuarioLogin.getEmail());
				        
		        // B. AGORA, VAMOS PREPARAR O OBJETO DE RESPOSTA.
		        // COPIAMOS AS INFORMAÇÕES DO USUÁRIO (ID, NOME, FOTO) E O TOKEN GERADO PARA O OBJETO 'USUARIOLOGIN'
		        // QUE SERÁ ENVIADO DE VOLTA PARA O CLIENTE.
				usuarioLogin.setId(usuario.get().getId());
				usuarioLogin.setNome(usuario.get().getNome());
				usuarioLogin.setFoto(usuario.get().getFoto());
				usuarioLogin.setToken(token);
		        
		        // C. MEDIDA DE SEGURANÇA MUITO IMPORTANTE: APAGAMOS A SENHA DO OBJETO DE RESPOSTA.
		        // NUNCA DEVEMOS ENVIAR UMA SENHA DE VOLTA, NEM MESMO A QUE O USUÁRIO ENVIOU.
				usuarioLogin.setSenha("");

		        // D. "EMBRULHAMOS" O OBJETO DE RESPOSTA COMPLETO EM UM 'OPTIONAL' E O RETORNAMOS.
		        // ISSO INDICA UM LOGIN BEM-SUCEDIDO.
				return Optional.of(usuarioLogin);
			}
				        
		    // PASSO 5: SE, POR ALGUM MOTIVO MUITO RARO, O USUÁRIO FOI AUTENTICADO MAS NÃO FOI ENCONTRADO NO BANCO,
		    // RETORNAMOS UM 'OPTIONAL' VAZIO PARA INDICAR QUE O LOGIN FALHOU.
			return Optional.empty();
		}



		
		
		
		
		


//O METODO AGORA PROMETE RETORNAR UM DTO SEGURO
        public UsuarioResponseDTO criarUsuario(Usuario usuario) {
        	
        	
		Optional<Usuario> usuarioExiste = usuarioRepository.findByEmail(usuario.getEmail());

				
		if(usuarioExiste.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário (e-mail) já existe!");
		}
		
		
	    // AQUI ESTÁ A LINHA MÁGICA DA CORREÇÃO:
	    // PEGA A SENHA ORIGINAL, CRIPTOGRAFA, E COLOCA DE VOLTA NO OBJETO USUARIO.
		String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
		
		System.out.println("SENHA CRIPTOGRAFADA GERADA: " + senhaCriptografada);
		usuario.setSenha(senhaCriptografada);
		
		Usuario novoUsuario = usuarioRepository.save(usuario);
		
		UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();
		
		usuarioResponseDTO.setId(novoUsuario.getId());
		usuarioResponseDTO.setNome(novoUsuario.getNome());
		usuarioResponseDTO.setEmail(novoUsuario.getEmail());
		usuarioResponseDTO.setFoto(novoUsuario.getFoto());
		
		
		return usuarioResponseDTO; 
	}
        
        
        
        
        
        
        
        
        
        public UsuarioResponseDTO atualizarUsuario(Usuario usuarioAtualizado, Long idUsuarioLogado) {

            // VERIFICA SE O USUÁRIO LOGADO ESTÁ TENTANDO EDITAR OS PRÓPRIOS DADOS
            // SE O ID DO USUÁRIO A SER EDITADO FOR DIFERENTE DO ID DO USUÁRIO LOGADO, BLOQUEIA
            if (!usuarioAtualizado.getId().equals(idUsuarioLogado)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "O USUÁRIO NÃO TEM PERMISSÃO PARA ATUALIZAR OS DADOS DE OUTRO USUÁRIO!");
            }

            // PROCURA O USUÁRIO NO BANCO USANDO O ID INFORMADO
            Optional<Usuario> optionalUsuarioNoBanco = usuarioRepository.findById(usuarioAtualizado.getId());
            if (optionalUsuarioNoBanco.isEmpty()) {
                // SE NÃO ENCONTRAR, RETORNA ERRO 404
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USUARIO NÃO ENCONTRADO PELO ID PARA ATUALIZAÇÃO");
            }

            // OBTÉM O USUÁRIO ENCONTRADO NO BANCO
            Usuario usuarioNoBanco = optionalUsuarioNoBanco.get();

            // ATUALIZA APENAS O NOME E A FOTO COM OS NOVOS VALORES RECEBIDOS
            usuarioNoBanco.setNome(usuarioAtualizado.getNome());
            usuarioNoBanco.setFoto(usuarioAtualizado.getFoto());

            // SALVA AS ALTERAÇÕES NO BANCO (SOMENTE NOME E FOTO FORAM MODIFICADOS)
            Usuario novoUsuarioSalvo = usuarioRepository.save(usuarioNoBanco);

            // MONTA O OBJETO DE RESPOSTA COM OS DADOS ATUALIZADOS (SEM SENHA)
            UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO();
            usuarioResponseDTO.setId(novoUsuarioSalvo.getId());         // ID DO USUÁRIO
            usuarioResponseDTO.setNome(novoUsuarioSalvo.getNome());     // NOME ATUALIZADO
            usuarioResponseDTO.setEmail(novoUsuarioSalvo.getEmail());   // EMAIL ORIGINAL (NÃO FOI ALTERADO AQUI)
            usuarioResponseDTO.setFoto(novoUsuarioSalvo.getFoto());     // FOTO ATUALIZADA

            // RETORNA A RESPOSTA COM OS NOVOS DADOS DO USUÁRIO
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
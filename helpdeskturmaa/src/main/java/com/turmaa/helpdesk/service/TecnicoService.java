package com.turmaa.helpdesk.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.turmaa.helpdesk.domain.Tecnico;
import com.turmaa.helpdesk.domain.dtos.TecnicoDTO;
import com.turmaa.helpdesk.repositories.TecnicoRepository;
import com.turmaa.helpdesk.service.exceptions.DataIntegrityViolationException;
import com.turmaa.helpdesk.service.exceptions.ObjectNotFoundException;

/**
 * Serviço responsável pela lógica de negócio relacionada aos técnicos.
 * 
 * <p>
 * Esta classe centraliza todas as regras de negócio e validações relacionadas
 * aos técnicos do sistema, servindo como camada intermediária entre os
 * controllers e o acesso aos dados.
 * </p>
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Validação de dados antes da persistência</li>
 *   <li>Verificação de unicidade de CPF e email</li>
 *   <li>Conversão entre entidades e DTOs</li>
 *   <li>Tratamento de exceções específicas do domínio</li>
 *   <li>Aplicação de regras de negócio</li>
 * </ul>
 * 
 * <h3>Operações disponíveis:</h3>
 * <ul>
 *   <li>Busca por ID com validação de existência</li>
 *   <li>Listagem completa com conversão para DTO</li>
 *   <li>Criação com validação de CPF único</li>
 *   <li>Atualização com preservação de dados</li>
 *   <li>Exclusão com verificação de dependências</li>
 * </ul>
 * 
 * @author Turma A
 * @version 1.0
 * @since 1.0
 * 
 * @see Tecnico
 * @see TecnicoDTO
 * @see TecnicoRepository
 */
@Service
public class TecnicoService {

	/**
	 * Repositório para acesso aos dados dos técnicos.
	 */
	@Autowired
	private TecnicoRepository repository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	/**
	 * Busca um técnico específico por seu ID.
	 * 
	 * <p>
	 * Realiza a busca no banco de dados e valida se o técnico existe.
	 * Caso não encontre, lança exceção específica com mensagem personalizada.
	 * </p>
	 * 
	 * @param id Identificador único do técnico
	 * @return Entidade Tecnico encontrada
	 * @throws ObjectNotFoundException se o técnico não for encontrado
	 */
	public Tecnico findById(Integer id) {
		Optional<Tecnico> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Técnico não encontrado! id: " + id));
	}

	/**
	 * Lista todos os técnicos cadastrados no sistema.
	 * 
	 * <p>
	 * Busca todos os técnicos no banco de dados e converte cada um
	 * para TecnicoDTO, ocultando informações sensíveis como senha
	 * e simplificando a estrutura para transferência via API.
	 * </p>
	 * 
	 * @return Lista de TecnicoDTO com todos os técnicos
	 */
	public List<TecnicoDTO> findAll() {
		return repository.findAll().stream()
				.map(obj -> new TecnicoDTO(obj))
				.collect(Collectors.toList());
	}

	/**
	 * Cria um novo técnico no sistema.
	 * 
	 * <p>
	 * Aplica validações de negócio antes da criação:
	 * </p>
	 * <ul>
	 *   <li>Verifica se o CPF já está cadastrado no sistema</li>
	 *   <li>Converte o DTO para entidade</li>
	 *   <li>Persiste no banco de dados</li>
	 * </ul>
	 * 
	 * @param objDto Dados do técnico a ser criado
	 * @return Entidade Tecnico criada e persistida
	 * @throws DataIntegrityViolationException se CPF já existir no sistema
	 */
	public Tecnico create(TecnicoDTO objDto) {
		if (repository.findByCpf(objDto.getCpf()).isPresent()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}
		Tecnico tecnico = new Tecnico(objDto);
		if (objDto.getSenha() != null) {
			tecnico.setSenha(passwordEncoder.encode(objDto.getSenha()));
		}
		return repository.save(tecnico);
	}

	/**
	 * Atualiza os dados de um técnico existente.
	 * 
	 * <p>
	 * Processo de atualização segura:
	 * </p>
	 * <ol>
	 *   <li>Busca o técnico existente (valida se existe)</li>
	 *   <li>Verifica se o CPF não conflita com outro técnico</li>
	 *   <li>Atualiza apenas os campos permitidos</li>
	 *   <li>Preserva dados sensíveis como senha e perfis</li>
	 *   <li>Persiste as alterações</li>
	 * </ol>
	 * 
	 * @param id Identificador do técnico a ser atualizado
	 * @param objDto Novos dados do técnico
	 * @return Entidade Tecnico atualizada
	 * @throws ObjectNotFoundException se o técnico não for encontrado
	 * @throws DataIntegrityViolationException se CPF conflitar com outro técnico
	 */
	public Tecnico update(Integer id, TecnicoDTO objDto) {
		Tecnico oldObj = findById(id);

		if (!oldObj.getCpf().equals(objDto.getCpf()) && repository.findByCpf(objDto.getCpf()).isPresent()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}

		oldObj.setNome(objDto.getNome());
		oldObj.setEmail(objDto.getEmail());
		oldObj.setCpf(objDto.getCpf());

		return repository.save(oldObj);
	}

	/**
	 * Remove um técnico do sistema.
	 * 
	 * <p>
	 * Realiza a exclusão após validar a existência do técnico.
	 * </p>
	 * 
	 * <p>
	 * <strong>Atenção:</strong> A exclusão pode falhar se o técnico
	 * possuir chamados vinculados devido a restrições de integridade
	 * referencial do banco de dados.
	 * </p>
	 * 
	 * @param id Identificador do técnico a ser removido
	 * @throws ObjectNotFoundException se o técnico não for encontrado
	 * @throws DataIntegrityViolationException se técnico possuir chamados vinculados
	 */
	public void delete(Integer id) {
		Tecnico obj = findById(id);
		repository.delete(obj);
	}
}

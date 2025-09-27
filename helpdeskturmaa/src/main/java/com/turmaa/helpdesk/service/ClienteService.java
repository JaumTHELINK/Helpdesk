package com.turmaa.helpdesk.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turmaa.helpdesk.domain.Cliente;
import com.turmaa.helpdesk.domain.dtos.ClienteDTO;
import com.turmaa.helpdesk.repositories.ClienteRepository;
import com.turmaa.helpdesk.service.exceptions.DataIntegrityViolationException;
import com.turmaa.helpdesk.service.exceptions.ObjectNotFoundException;

/**
 * Serviço responsável pela lógica de negócio relacionada aos clientes.
 * 
 * <p>
 * Esta classe centraliza todas as regras de negócio e validações relacionadas
 * aos clientes do sistema, servindo como camada intermediária entre os
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
 * @see Cliente
 * @see ClienteDTO
 * @see ClienteRepository
 */
@Service
public class ClienteService {

	/**
	 * Repositório para acesso aos dados dos clientes.
	 */
	@Autowired
	private ClienteRepository repository;

	/**
	 * Busca um cliente específico por seu ID.
	 * 
	 * <p>
	 * Realiza a busca no banco de dados e valida se o cliente existe.
	 * Caso não encontre, lança exceção específica com mensagem personalizada.
	 * </p>
	 * 
	 * @param id Identificador único do cliente
	 * @return Entidade Cliente encontrada
	 * @throws ObjectNotFoundException se o cliente não for encontrado
	 */
	public Cliente findById(Integer id) {
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Cliente não encontrado! id: " + id));
	}

	/**
	 * Lista todos os clientes cadastrados no sistema.
	 * 
	 * <p>
	 * Busca todos os clientes no banco de dados e converte cada um
	 * para ClienteDTO, ocultando informações sensíveis como senha
	 * e simplificando a estrutura para transferência via API.
	 * </p>
	 * 
	 * @return Lista de ClienteDTO com todos os clientes
	 */
	public List<ClienteDTO> findAll() {
		return repository.findAll().stream()
				.map(obj -> new ClienteDTO(obj))
				.collect(Collectors.toList());
	}

	/**
	 * Cria um novo cliente no sistema.
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
	 * @param objDto Dados do cliente a ser criado
	 * @return Entidade Cliente criada e persistida
	 * @throws DataIntegrityViolationException se CPF já existir no sistema
	 */
	public Cliente create(ClienteDTO objDto) {
		if (repository.findByCpf(objDto.getCpf()).isPresent()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}
		return repository.save(new Cliente(objDto));
	}

	/**
	 * Atualiza os dados de um cliente existente.
	 * 
	 * <p>
	 * Processo de atualização segura:
	 * </p>
	 * <ol>
	 *   <li>Busca o cliente existente (valida se existe)</li>
	 *   <li>Verifica se o CPF não conflita com outro cliente</li>
	 *   <li>Atualiza apenas os campos permitidos</li>
	 *   <li>Preserva dados sensíveis como senha e perfis</li>
	 *   <li>Persiste as alterações</li>
	 * </ol>
	 * 
	 * @param id Identificador do cliente a ser atualizado
	 * @param objDto Novos dados do cliente
	 * @return Entidade Cliente atualizada
	 * @throws ObjectNotFoundException se o cliente não for encontrado
	 * @throws DataIntegrityViolationException se CPF conflitar com outro cliente
	 */
	public Cliente update(Integer id, ClienteDTO objDto) {
		Cliente oldObj = findById(id);

		if (!oldObj.getCpf().equals(objDto.getCpf()) && repository.findByCpf(objDto.getCpf()).isPresent()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}

		oldObj.setNome(objDto.getNome());
		oldObj.setEmail(objDto.getEmail());
		oldObj.setCpf(objDto.getCpf());

		return repository.save(oldObj);
	}

	/**
	 * Remove um cliente do sistema.
	 * 
	 * <p>
	 * Realiza a exclusão após validar a existência do cliente.
	 * </p>
	 * 
	 * <p>
	 * <strong>Atenção:</strong> A exclusão pode falhar se o cliente
	 * possuir chamados vinculados devido a restrições de integridade
	 * referencial do banco de dados.
	 * </p>
	 * 
	 * @param id Identificador do cliente a ser removido
	 * @throws ObjectNotFoundException se o cliente não for encontrado
	 * @throws DataIntegrityViolationException se cliente possuir chamados vinculados
	 */
	public void delete(Integer id) {
		Cliente obj = findById(id);
		repository.delete(obj);
	}
}

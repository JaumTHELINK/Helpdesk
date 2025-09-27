package com.turmaa.helpdesk.resources;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.turmaa.helpdesk.domain.Cliente;
import com.turmaa.helpdesk.domain.dtos.ClienteDTO;
import com.turmaa.helpdesk.service.ClienteService;

/**
 * Controlador REST para operações relacionadas à entidade Cliente.
 * 
 * <p>
 * Este controller expõe endpoints HTTP para gerenciar clientes no sistema,
 * incluindo operações CRUD (Create, Read, Update, Delete) completas.
 * </p>
 * 
 * <h3>Endpoints disponíveis:</h3>
 * <ul>
 *   <li>GET /clientes - Lista todos os clientes</li>
 *   <li>GET /clientes/{id} - Busca cliente por ID</li>
 *   <li>POST /clientes - Cria novo cliente</li>
 *   <li>PUT /clientes/{id} - Atualiza cliente existente</li>
 *   <li>DELETE /clientes/{id} - Remove cliente</li>
 * </ul>
 * 
 * <p>
 * Todos os dados são transferidos usando DTOs (Data Transfer Objects)
 * para desacoplar a API da estrutura interna das entidades.
 * </p>
 * 
 * @author Turma A
 * @version 1.0
 * @since 1.0
 * 
 * @see Cliente
 * @see ClienteDTO
 * @see ClienteService
 */
@RestController
@RequestMapping(value = "/clientes")
public class ClienteResource {

	/**
	 * Serviço responsável pela lógica de negócio relacionada aos clientes.
	 */
	@Autowired
	private ClienteService service;

	/**
	 * Busca um cliente específico por seu ID.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> GET /clientes/{id}
	 * </p>
	 * 
	 * @param id Identificador único do cliente
	 * @return ResponseEntity contendo o ClienteDTO encontrado
	 * @throws ObjectNotFoundException se o cliente não for encontrado
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<ClienteDTO> findById(@PathVariable Integer id) {
		Cliente obj = service.findById(id);
		return ResponseEntity.ok().body(new ClienteDTO(obj));
	}

	/**
	 * Lista todos os clientes cadastrados no sistema.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> GET /clientes
	 * </p>
	 * 
	 * @return ResponseEntity contendo lista de ClienteDTO
	 */
	@GetMapping
	public ResponseEntity<List<ClienteDTO>> findAll() {
		List<ClienteDTO> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}

	/**
	 * Cria um novo cliente no sistema.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> POST /clientes
	 * </p>
	 * 
	 * <p>
	 * Retorna status 201 (Created) com o location header apontando
	 * para o novo recurso criado.
	 * </p>
	 * 
	 * @param objDto Dados do cliente a ser criado (validados automaticamente)
	 * @return ResponseEntity com status 201 e o ClienteDTO criado
	 * @throws DataIntegrityViolationException se CPF ou email já existirem
	 */
	@PostMapping
	public ResponseEntity<ClienteDTO> create(@Valid @RequestBody ClienteDTO objDto) {
		Cliente newObj = service.create(objDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newObj.getId()).toUri();
		return ResponseEntity.created(uri).body(new ClienteDTO(newObj));
	}

	/**
	 * Atualiza os dados de um cliente existente.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> PUT /clientes/{id}
	 * </p>
	 * 
	 * @param id Identificador do cliente a ser atualizado
	 * @param objDto Novos dados do cliente (validados automaticamente)
	 * @return ResponseEntity contendo o ClienteDTO atualizado
	 * @throws ObjectNotFoundException se o cliente não for encontrado
	 * @throws DataIntegrityViolationException se CPF ou email conflitarem
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<ClienteDTO> update(@PathVariable Integer id, @Valid @RequestBody ClienteDTO objDto) {
		Cliente updatedObj = service.update(id, objDto);
		return ResponseEntity.ok().body(new ClienteDTO(updatedObj));
	}

	/**
	 * Remove um cliente do sistema.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> DELETE /clientes/{id}
	 * </p>
	 * 
	 * <p>
	 * Retorna status 204 (No Content) quando a exclusão é bem-sucedida.
	 * </p>
	 * 
	 * @param id Identificador do cliente a ser removido
	 * @return ResponseEntity com status 204 (No Content)
	 * @throws ObjectNotFoundException se o cliente não for encontrado
	 * @throws DataIntegrityViolationException se o cliente possui chamados vinculados
	 */
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}

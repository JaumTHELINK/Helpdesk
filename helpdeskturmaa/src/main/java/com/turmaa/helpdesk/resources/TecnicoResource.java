package com.turmaa.helpdesk.resources;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.turmaa.helpdesk.domain.Tecnico;
import com.turmaa.helpdesk.domain.dtos.TecnicoDTO;
import com.turmaa.helpdesk.service.TecnicoService;

/**
 * Controlador REST para operações relacionadas à entidade Técnico.
 * 
 * <p>
 * Este controller expõe endpoints HTTP para gerenciar técnicos no sistema,
 * incluindo operações CRUD (Create, Read, Update, Delete) completas.
 * </p>
 * 
 * <h3>Endpoints disponíveis:</h3>
 * <ul>
 *   <li>GET /tecnicos - Lista todos os técnicos</li>
 *   <li>GET /tecnicos/{id} - Busca técnico por ID</li>
 *   <li>POST /tecnicos - Cria novo técnico</li>
 *   <li>PUT /tecnicos/{id} - Atualiza técnico existente</li>
 *   <li>DELETE /tecnicos/{id} - Remove técnico</li>
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
 * @see Tecnico
 * @see TecnicoDTO
 * @see TecnicoService
 */
@RestController
@RequestMapping(value = "/tecnicos")
public class TecnicoResource {

	/**
	 * Serviço responsável pela lógica de negócio relacionada aos técnicos.
	 */
	@Autowired
	private TecnicoService service;

	/**
	 * Busca um técnico específico por seu ID.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> GET /tecnicos/{id}
	 * </p>
	 * 
	 * @param id Identificador único do técnico
	 * @return ResponseEntity contendo o TecnicoDTO encontrado
	 * @throws ObjectNotFoundException se o técnico não for encontrado
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<TecnicoDTO> findById(@PathVariable Integer id) {
		Tecnico obj = service.findById(id);
		return ResponseEntity.ok().body(new TecnicoDTO(obj));
	}

	/**
	 * Lista todos os técnicos cadastrados no sistema.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> GET /tecnicos
	 * </p>
	 * 
	 * @return ResponseEntity contendo lista de TecnicoDTO
	 */
	@GetMapping
	public ResponseEntity<List<TecnicoDTO>> findAll() {
		List<TecnicoDTO> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}

	/**
	 * Cria um novo técnico no sistema.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> POST /tecnicos
	 * </p>
	 * 
	 * <p>
	 * Retorna status 201 (Created) com o location header apontando
	 * para o novo recurso criado.
	 * </p>
	 * 
	 * @param objDto Dados do técnico a ser criado (validados automaticamente)
	 * @return ResponseEntity com status 201 e o TecnicoDTO criado
	 * @throws DataIntegrityViolationException se CPF ou email já existirem
	 */
	@PostMapping
	public ResponseEntity<TecnicoDTO> create(@Valid @RequestBody TecnicoDTO objDto) {
		Tecnico newObj = service.create(objDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newObj.getId()).toUri();
		return ResponseEntity.created(uri).body(new TecnicoDTO(newObj));
	}

	/**
	 * Atualiza os dados de um técnico existente.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> PUT /tecnicos/{id}
	 * </p>
	 * 
	 * @param id Identificador do técnico a ser atualizado
	 * @param objDto Novos dados do técnico (validados automaticamente)
	 * @return ResponseEntity contendo o TecnicoDTO atualizado
	 * @throws ObjectNotFoundException se o técnico não for encontrado
	 * @throws DataIntegrityViolationException se CPF ou email conflitarem
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<TecnicoDTO> update(@PathVariable Integer id, @Valid @RequestBody TecnicoDTO objDto) {
		Tecnico updatedObj = service.update(id, objDto);
		return ResponseEntity.ok().body(new TecnicoDTO(updatedObj));
	}

	/**
	 * Remove um técnico do sistema.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> DELETE /tecnicos/{id}
	 * </p>
	 * 
	 * <p>
	 * Retorna status 204 (No Content) quando a exclusão é bem-sucedida.
	 * </p>
	 * 
	 * @param id Identificador do técnico a ser removido
	 * @return ResponseEntity com status 204 (No Content)
	 * @throws ObjectNotFoundException se o técnico não for encontrado
	 * @throws DataIntegrityViolationException se o técnico possui chamados vinculados
	 */
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}

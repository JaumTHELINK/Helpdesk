package com.turmaa.helpdesk.resources;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.turmaa.helpdesk.domain.Chamado;
import com.turmaa.helpdesk.domain.dtos.ChamadoDTO;
import com.turmaa.helpdesk.service.ChamadoService;

/**
 * Controlador REST para operações relacionadas à entidade Chamado.
 * 
 * <p>
 * Este controller é o coração da API do sistema Helpdesk, gerenciando
 * todas as operações relacionadas aos chamados de suporte técnico.
 * </p>
 * 
 * <h3>Endpoints disponíveis:</h3>
 * <ul>
 *   <li>GET /chamados - Lista todos os chamados</li>
 *   <li>GET /chamados/{id} - Busca chamado por ID</li>
 *   <li>POST /chamados - Cria novo chamado</li>
 *   <li>PUT /chamados/{id} - Atualiza chamado existente</li>
 *   <li>DELETE /chamados/{id} - Remove chamado</li>
 * </ul>
 * 
 * <p>
 * Os chamados representam solicitações de suporte feitas pelos clientes
 * e atribuídas aos técnicos para resolução. Este controller permite
 * o gerenciamento completo do ciclo de vida dos chamados.
 * </p>
 * 
 * @author Turma A
 * @version 1.0
 * @since 1.0
 * 
 * @see Chamado
 * @see ChamadoDTO
 * @see ChamadoService
 */
@RestController
@RequestMapping(value = "/chamados")
public class ChamadoResource {

	/**
	 * Serviço responsável pela lógica de negócio relacionada aos chamados.
	 */
	@Autowired
	private ChamadoService service;

	/**
	 * Busca um chamado específico por seu ID.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> GET /chamados/{id}
	 * </p>
	 * 
	 * <p>
	 * Retorna todas as informações do chamado incluindo dados do técnico
	 * responsável e do cliente que abriu o chamado.
	 * </p>
	 * 
	 * @param id Identificador único do chamado
	 * @return ResponseEntity contendo o ChamadoDTO encontrado
	 * @throws ObjectNotFoundException se o chamado não for encontrado
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<ChamadoDTO> findById(@PathVariable Integer id) {
		Chamado obj = service.findById(id);
		return ResponseEntity.ok().body(new ChamadoDTO(obj));
	}

	/**
	 * Lista todos os chamados cadastrados no sistema.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> GET /chamados
	 * </p>
	 * 
	 * <p>
	 * Retorna uma visão geral de todos os chamados incluindo informações
	 * dos técnicos responsáveis e clientes, útil para dashboards e relatórios.
	 * </p>
	 * 
	 * @return ResponseEntity contendo lista de ChamadoDTO
	 */
	@GetMapping
	public ResponseEntity<List<ChamadoDTO>> findAll() {
		List<ChamadoDTO> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}

	/**
	 * Cria um novo chamado no sistema.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> POST /chamados
	 * </p>
	 * 
	 * <p>
	 * Cria um chamado vinculando um cliente a um técnico responsável.
	 * A data de abertura é definida automaticamente como a data atual.
	 * Retorna status 201 (Created) com o location header.
	 * </p>
	 * 
	 * @param objDto Dados do chamado a ser criado (validados automaticamente)
	 * @return ResponseEntity com status 201 e o ChamadoDTO criado
	 * @throws ObjectNotFoundException se técnico ou cliente não existirem
	 * @throws ValidationException se dados obrigatórios estiverem ausentes
	 */
	@PostMapping
	public ResponseEntity<ChamadoDTO> create(@Valid @RequestBody ChamadoDTO objDto) {
		Chamado newObj = service.create(objDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newObj.getId()).toUri();
		return ResponseEntity.created(uri).body(new ChamadoDTO(newObj));
	}

	/**
	 * Atualiza os dados de um chamado existente.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> PUT /chamados/{id}
	 * </p>
	 * 
	 * <p>
	 * Permite alteração de status, prioridade, observações, técnico responsável
	 * e outros dados do chamado. Útil para atualizar o progresso do atendimento.
	 * </p>
	 * 
	 * @param id Identificador do chamado a ser atualizado
	 * @param objDto Novos dados do chamado (validados automaticamente)
	 * @return ResponseEntity contendo o ChamadoDTO atualizado
	 * @throws ObjectNotFoundException se chamado, técnico ou cliente não existirem
	 * @throws ValidationException se dados obrigatórios estiverem inválidos
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<ChamadoDTO> update(@PathVariable Integer id, @Valid @RequestBody ChamadoDTO objDto) {
		Chamado updatedObj = service.update(id, objDto);
		return ResponseEntity.ok().body(new ChamadoDTO(updatedObj));
	}
	
	/**
	 * Remove um chamado do sistema.
	 * 
	 * <p>
	 * <strong>Endpoint:</strong> DELETE /chamados/{id}
	 * </p>
	 * 
	 * <p>
	 * Remove permanentemente um chamado do sistema. Esta operação deve ser
	 * usada com cuidado pois pode afetar relatórios e históricos.
	 * Retorna status 204 (No Content) quando bem-sucedida.
	 * </p>
	 * 
	 * @param id Identificador do chamado a ser removido
	 * @return ResponseEntity com status 204 (No Content)
	 * @throws ObjectNotFoundException se o chamado não for encontrado
	 */
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Integer id) {
	    service.delete(id);
	    return ResponseEntity.noContent().build();
	}

}

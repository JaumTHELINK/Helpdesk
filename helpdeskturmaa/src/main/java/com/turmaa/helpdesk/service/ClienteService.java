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

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;

	public Cliente findById(Integer id) {
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Cliente não encontrado! id: " + id));
	}

	public List<ClienteDTO> findAll() {
		return repository.findAll().stream()
				.map(obj -> new ClienteDTO(obj))
				.collect(Collectors.toList());
	}

	public Cliente create(ClienteDTO objDto) {
		if (repository.findByCpf(objDto.getCpf()).isPresent()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}
		return repository.save(new Cliente(objDto));
	}

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

	public void delete(Integer id) {
		Cliente obj = findById(id);
		repository.delete(obj);
	}
}

package com.turmaa.helpdesk.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turmaa.helpdesk.domain.Tecnico;
import com.turmaa.helpdesk.domain.dtos.TecnicoDTO;
import com.turmaa.helpdesk.repositories.TecnicoRepository;
import com.turmaa.helpdesk.service.exceptions.DataIntegrityViolationException;
import com.turmaa.helpdesk.service.exceptions.ObjectNotFoundException;

@Service
public class TecnicoService {

	@Autowired
	private TecnicoRepository repository;

	public Tecnico findById(Integer id) {
		Optional<Tecnico> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Técnico não encontrado! id: " + id));
	}

	public List<TecnicoDTO> findAll() {
		return repository.findAll().stream()
				.map(obj -> new TecnicoDTO(obj))
				.collect(Collectors.toList());
	}

	public Tecnico create(TecnicoDTO objDto) {
		if (repository.findByCpf(objDto.getCpf()).isPresent()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}
		return repository.save(new Tecnico(objDto));
	}

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

	public void delete(Integer id) {
		Tecnico obj = findById(id);
		repository.delete(obj);
	}
}

package com.turmaa.helpdesk.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turmaa.helpdesk.domain.Chamado;
import com.turmaa.helpdesk.domain.dtos.ChamadoDTO;
import com.turmaa.helpdesk.repositories.ChamadoRepository;
import com.turmaa.helpdesk.service.exceptions.ObjectNotFoundException;

@Service
public class ChamadoService {

	@Autowired
	private ChamadoRepository repository;

	public Chamado findById(Integer id) {
		Optional<Chamado> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Chamado não encontrado! id: " + id));
	}

	public List<ChamadoDTO> findAll() {
		return repository.findAll().stream()
				.map(obj -> new ChamadoDTO(obj))
				.collect(Collectors.toList());
	}

	public Chamado create(ChamadoDTO objDto) {
		return repository.save(new Chamado(objDto));
	}

	public Chamado update(Integer id, ChamadoDTO objDto) {
		findById(id);
		objDto.setId(id);
		return repository.save(new Chamado(objDto));
	}
}

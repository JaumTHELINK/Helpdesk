package com.turmaa.helpdesk.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turmaa.helpdesk.domain.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer>{

}

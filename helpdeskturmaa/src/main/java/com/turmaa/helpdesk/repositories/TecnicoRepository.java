package com.turmaa.helpdesk.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turmaa.helpdesk.domain.Tecnico;

/**
 * Repositório para a entidade Tecnico.
 * Estende JpaRepository para herdar métodos prontos de CRUD.
 */
@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Integer> {
    
    Optional<Tecnico> findByCpf(String cpf);

    Optional<Tecnico> findByEmail(String email);
}

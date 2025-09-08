package com.turmaa.helpdesk.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turmaa.helpdesk.domain.Cliente;

/**
 * Repositório para a entidade Cliente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByEmail(String email);
}

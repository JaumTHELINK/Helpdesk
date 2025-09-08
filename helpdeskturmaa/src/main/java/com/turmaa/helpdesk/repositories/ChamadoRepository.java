package com.turmaa.helpdesk.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turmaa.helpdesk.domain.Chamado;

/**
 * Repositório para a entidade Chamado.
 */
@Repository
public interface ChamadoRepository extends JpaRepository<Chamado, Integer> {
    // Aqui geralmente não precisamos de métodos extras,
    // mas você pode criar consultas personalizadas se necessário.
}

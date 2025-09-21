package com.turmaa.helpdesk.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.turmaa.helpdesk.domain.Pessoa;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {
    
    /**
     * Busca uma pessoa pelo e-mail.
     * 
     * @param email e-mail da pessoa
     * @return Optional contendo a pessoa se encontrada, ou vazio se não existir
     */
    Optional<Pessoa> findByEmail(String email);
}

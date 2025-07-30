package com.portfolio.portfolio_java.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.portfolio_java.models.Portafolio;

@Repository
public interface PortafolioRepository extends JpaRepository<Portafolio, Long>{

    Optional<Portafolio> findById(Long id);
    Optional<Portafolio> findByNombre(String nombre);
}

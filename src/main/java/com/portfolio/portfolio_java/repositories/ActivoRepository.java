package com.portfolio.portfolio_java.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.portfolio_java.models.Activo;

@Repository
public interface ActivoRepository extends  JpaRepository<Activo, Long>{

  Optional<Activo> findByNombre(String nombre);
}

package com.portfolio.portfolio_java.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.portfolio_java.models.Precio;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Long>{

    List<Precio> findByFechaBetween(LocalDate inicio, LocalDate fin);

    Precio findByActivoIdAndFecha(Long activoVentaId, LocalDate fechaVenta);
}

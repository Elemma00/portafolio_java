package com.portfolio.portfolio_java.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.portfolio.portfolio_java.models.Portafolio;
import com.portfolio.portfolio_java.models.Weight;

@Repository
public interface WeightRepository extends JpaRepository<Weight, Long> {

   List<Weight> findByPortafolio(Portafolio portafolio);

}
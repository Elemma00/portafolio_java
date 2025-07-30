package com.portfolio.portfolio_java.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "weights", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"portafolio_id", "activo_id"}))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Weight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portafolio_id", nullable = false)
    private Portafolio portafolio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activo_id", nullable = false)
    private Activo activo;

    @Column(name = "peso_inicial", nullable = false, precision = 10, scale = 6)
    private BigDecimal pesoInicial; // w_{i,0}

    private BigDecimal cantidadInicial; // c_{i,0}
}

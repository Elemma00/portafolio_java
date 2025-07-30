package com.portfolio.portfolio_java.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "portafolios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Portafolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "valor_inicial", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorInicial;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @OneToMany(mappedBy = "portafolio", cascade = CascadeType.ALL)
    private List<Weight> weights; 
    
}
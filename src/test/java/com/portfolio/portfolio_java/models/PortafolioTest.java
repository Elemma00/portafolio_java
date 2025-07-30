package com.portfolio.portfolio_java.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PortafolioTest {

    private Portafolio portafolio;

    @BeforeEach
    void setUp() {
        portafolio = new Portafolio();
    }

    @Test
    @DisplayName("Se prueba el constructor sin argumentos y los setters")
    void testNoArgsConstructorAndSetters() {
        portafolio.setId(1L);
        portafolio.setNombre("Mi Portafolio");
        portafolio.setValorInicial(new BigDecimal("10000.00"));
        portafolio.setFechaInicio(LocalDate.of(2024, 1, 1));
        portafolio.setWeights(Collections.emptyList());

        assertEquals(1L, portafolio.getId());
        assertEquals("Mi Portafolio", portafolio.getNombre());
        assertEquals(new BigDecimal("10000.00"), portafolio.getValorInicial());
        assertEquals(LocalDate.of(2024, 1, 1), portafolio.getFechaInicio());
        assertNotNull(portafolio.getWeights());
        assertTrue(portafolio.getWeights().isEmpty());
    }

    @Test
    @DisplayName("Se prueba el constructor con todos los argumentos")
    void testAllArgsConstructor() {
        Weight weight1 = new Weight();
        Weight weight2 = new Weight();
        Portafolio p = new Portafolio(
                2L,
                "Portafolio 2",
                new BigDecimal("5000.50"),
                LocalDate.of(2023, 6, 15),
                Arrays.asList(weight1, weight2)
        );

        assertEquals(2L, p.getId());
        assertEquals("Portafolio 2", p.getNombre());
        assertEquals(new BigDecimal("5000.50"), p.getValorInicial());
        assertEquals(LocalDate.of(2023, 6, 15), p.getFechaInicio());
        assertEquals(2, p.getWeights().size());
        assertTrue(p.getWeights().contains(weight1));
        assertTrue(p.getWeights().contains(weight2));
    }

    @Test
    @DisplayName("Test de método setWeights y getWeights")
    void testSetAndGetWeights() {
        Weight weight = new Weight();
        portafolio.setWeights(Arrays.asList(weight));
        assertEquals(1, portafolio.getWeights().size());
        assertSame(weight, portafolio.getWeights().get(0));
    }
}

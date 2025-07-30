package com.portfolio.portfolio_java.models;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WeightTest {

    @Test
    @DisplayName("Test sin argumentos, probando setters")
    void testNoArgsConstructorAndSetters() {
        Weight weight = new Weight();
        Portafolio portafolio = new Portafolio();
        Activo activo = new Activo();
        BigDecimal pesoInicial = new BigDecimal("0.123456");
        BigDecimal cantidadInicial = new BigDecimal("10");

        weight.setId(1L);
        weight.setPortafolio(portafolio);
        weight.setActivo(activo);
        weight.setPesoInicial(pesoInicial);
        weight.setCantidadInicial(cantidadInicial);

        assertEquals(1L, weight.getId());
        assertEquals(portafolio, weight.getPortafolio());
        assertEquals(activo, weight.getActivo());
        assertEquals(pesoInicial, weight.getPesoInicial());
        assertEquals(cantidadInicial, weight.getCantidadInicial());
    }

    @Test
    @DisplayName("Test con argumentos, probando constructor completo")
    void testAllArgsConstructor() {
        Portafolio portafolio = new Portafolio();
        Activo activo = new Activo();
        BigDecimal pesoInicial = new BigDecimal("0.654321");
        BigDecimal cantidadInicial = new BigDecimal("20.5");

        Weight weight = new Weight(2L, portafolio, activo, pesoInicial, cantidadInicial);

        assertEquals(2L, weight.getId());
        assertEquals(portafolio, weight.getPortafolio());
        assertEquals(activo, weight.getActivo());
        assertEquals(pesoInicial, weight.getPesoInicial());
        assertEquals(cantidadInicial, weight.getCantidadInicial());
    }
}

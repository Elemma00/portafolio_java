package com.portfolio.portfolio_java.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;


class PrecioTest {

    private Activo mockActivo;

    @BeforeEach
    void setUp() {
        mockActivo = mock(Activo.class);
    }

    @Test
    @DisplayName("Test sin args constructor y setters")
    void testNoArgsConstructorAndSetters() {
        Precio precio = new Precio();
        precio.setId(1L);
        precio.setActivo(mockActivo);
        precio.setFecha(LocalDate.of(2024, 6, 1));
        precio.setValor(new BigDecimal("123.4567"));

        assertEquals(1L, precio.getId());
        assertEquals(mockActivo, precio.getActivo());
        assertEquals(LocalDate.of(2024, 6, 1), precio.getFecha());
        assertEquals(new BigDecimal("123.4567"), precio.getValor());
    }

    @Test
    @DisplayName("Test con todos los args y getters")
    void testAllArgsConstructorAndGetters() {
        LocalDate fecha = LocalDate.of(2023, 12, 31);
        BigDecimal valor = new BigDecimal("999.9999");
        Precio precio = new Precio(2L, mockActivo, fecha, valor);

        assertEquals(2L, precio.getId());
        assertEquals(mockActivo, precio.getActivo());
        assertEquals(fecha, precio.getFecha());
        assertEquals(valor, precio.getValor());
    }

    @Test
    @DisplayName("Test de equals y hashCode")
    void testEqualsAndHashCode() {
        LocalDate fecha = LocalDate.now();
        BigDecimal valor = new BigDecimal("10.0000");
        Precio precio1 = new Precio(3L, mockActivo, fecha, valor);
        Precio precio2 = new Precio(3L, mockActivo, fecha, valor);

        assertEquals(precio1, precio2);
        assertEquals(precio1.hashCode(), precio2.hashCode());
    }

    @Test
    @DisplayName("Test de toString")
    void testToString() {
        Precio precio = new Precio(4L, mockActivo, LocalDate.of(2022, 1, 1), new BigDecimal("1.2345"));
        String str = precio.toString();
        assertTrue(str.contains("4"));
        assertTrue(str.contains("2022-01-01"));
        assertTrue(str.contains("1.2345"));
    }
}

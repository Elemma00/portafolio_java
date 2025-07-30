package com.portfolio.portfolio_java.models;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class ActivoTest {

    private Activo activo;

    @BeforeEach
    void setUp() {
        activo = new Activo();
    }

    @Test
    @DisplayName("Se prueba el contructor sin argumentos")
    void testNoArgsConstructor() {
        assertNotNull(activo);
        assertNull(activo.getId());
        assertNull(activo.getNombre());
        assertNull(activo.getPrecios());
    }

    @Test
    @DisplayName("Se prueba el constructor con todos los argumentos")
    void testAllArgsConstructor() {
        Precio precio1 = new Precio();
        Precio precio2 = new Precio();
        Activo activo2 = new Activo(1L, "ActivoTest", Arrays.asList(precio1, precio2));
        assertEquals(1L, activo2.getId());
        assertEquals("ActivoTest", activo2.getNombre());
        assertEquals(2, activo2.getPrecios().size());
    }

    @Test
    @DisplayName("Test de los setters y getters")
    void testSettersAndGetters() {
        activo.setId(10L);
        activo.setNombre("TestNombre");
        Precio precio = new Precio();
        activo.setPrecios(Collections.singletonList(precio));

        assertEquals(10L, activo.getId());
        assertEquals("TestNombre", activo.getNombre());
        assertEquals(1, activo.getPrecios().size());
        assertSame(precio, activo.getPrecios().get(0));
    }
}

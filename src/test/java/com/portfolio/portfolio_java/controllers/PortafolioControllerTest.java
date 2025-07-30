package com.portfolio.portfolio_java.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.portfolio.portfolio_java.services.PortafolioService;

/*
 * Estos test no usan casos esperados previamente calculados, 
 * solo verifica que el controlador procesa correctamente las solicitudes y devuelve respuestas válidas.
 */

@WebMvcTest(PortafolioController.class)
@ExtendWith(MockitoExtension.class)
class PortafolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PortafolioService portafolioService;

    private List<Map<String, Object>> mockResult;

    @BeforeEach
    void setUp() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        mockResult = Collections.singletonList(map);
    }

    @Test
    @DisplayName("Test de procesamiento de consulta para evaluacion")
    void testEvolucionPortafolio() throws Exception {
        Long id = 1L;
        LocalDate fechaInicio = LocalDate.of(2022, 10, 1);
        LocalDate fechaFin = LocalDate.of(2023, 10, 1);

        when(portafolioService.evaluarResultado(id, fechaInicio, fechaFin)).thenReturn(mockResult);

        mockMvc.perform(get("/api/portafolio/{id}", id)
                .param("fecha_inicio", fechaInicio.toString())
                .param("fecha_fin", fechaFin.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("value"));
    }

    @Test
    @DisplayName("Test de consulta para venta de activos")
    void testHistorialVenta() throws Exception {
        Long id = 2L;
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);
        LocalDate fechaVenta = LocalDate.of(2023, 6, 15);
        Long activoVentaId = 10L;
        BigDecimal montoVenta = new BigDecimal("1000");

        when(portafolioService.venderActivo(
                ArgumentMatchers.eq(id),
                ArgumentMatchers.eq(fechaInicio),
                ArgumentMatchers.eq(fechaFin),
                ArgumentMatchers.eq(fechaVenta),
                ArgumentMatchers.eq(activoVentaId),
                ArgumentMatchers.eq(montoVenta)
        )).thenReturn(mockResult);

        mockMvc.perform(get("/api/portafolio/{id}/historialventa", id)
                .param("fecha_inicio", fechaInicio.toString())
                .param("fecha_fin", fechaFin.toString())
                .param("fecha_venta", fechaVenta.toString())
                .param("activo_venta_id", activoVentaId.toString())
                .param("monto_venta", montoVenta.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("value"));
    }

    @Test
    @DisplayName("Test de consulta para compra de activos")
    void testHistorialCompra() throws Exception {
        Long id = 3L;
        LocalDate fechaInicio = LocalDate.of(2023, 1, 1);
        LocalDate fechaFin = LocalDate.of(2023, 12, 31);
        LocalDate fechaCompra = LocalDate.of(2023, 7, 20);
        Long activoCompraId = 20L;
        BigDecimal montoCompra = new BigDecimal("2000.75");

        when(portafolioService.comprarActivo(
                ArgumentMatchers.eq(id),
                ArgumentMatchers.eq(fechaInicio),
                ArgumentMatchers.eq(fechaFin),
                ArgumentMatchers.eq(fechaCompra),
                ArgumentMatchers.eq(activoCompraId),
                ArgumentMatchers.eq(montoCompra)
        )).thenReturn(mockResult);

        mockMvc.perform(get("/api/portafolio/{id}/historialcompra", id)
                .param("fecha_inicio", fechaInicio.toString())
                .param("fecha_fin", fechaFin.toString())
                .param("fecha_compra", fechaCompra.toString())
                .param("activo_compra_id", activoCompraId.toString())
                .param("monto_compra", montoCompra.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("value"));
    }
}

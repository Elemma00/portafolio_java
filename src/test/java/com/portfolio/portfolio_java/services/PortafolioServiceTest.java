package com.portfolio.portfolio_java.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.portfolio.portfolio_java.models.Activo;
import com.portfolio.portfolio_java.models.Portafolio;
import com.portfolio.portfolio_java.models.Precio;
import com.portfolio.portfolio_java.models.Weight;
import com.portfolio.portfolio_java.repositories.PortafolioRepository;
import com.portfolio.portfolio_java.repositories.PrecioRepository;
import com.portfolio.portfolio_java.repositories.WeightRepository;

class PortafolioServiceTest {

    @Mock
    private PortafolioRepository portafolioRepository;
    @Mock
    private PrecioRepository precioRepository;
    @Mock
    private WeightRepository weightRepository;

    @InjectMocks
    private PortafolioService portafolioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* 
     * Estas son funciones auxiliares que hice para crear mocks de los objetos 
     */
    private Portafolio mockPortafolio(Long id, LocalDate fechaInicio, BigDecimal valorInicial, List<Weight> weights) {
        Portafolio portafolio = mock(Portafolio.class);
        when(portafolio.getId()).thenReturn(id);
        when(portafolio.getFechaInicio()).thenReturn(fechaInicio);
        when(portafolio.getValorInicial()).thenReturn(valorInicial);
        when(portafolio.getWeights()).thenReturn(weights);
        return portafolio;
    }

    private Weight mockWeight(Long activoId, String nombre, BigDecimal cantidad) {
        Activo activo = mock(Activo.class);
        when(activo.getId()).thenReturn(activoId);
        when(activo.getNombre()).thenReturn(nombre);
        Weight weight = mock(Weight.class);
        when(weight.getActivo()).thenReturn(activo);
        when(weight.getCantidadInicial()).thenReturn(cantidad);
        return weight;
    }

    private Precio mockPrecio(Long activoId, LocalDate fecha, BigDecimal valor) {
        Activo activo = mock(Activo.class);
        when(activo.getId()).thenReturn(activoId);
        Precio precio = mock(Precio.class);
        when(precio.getActivo()).thenReturn(activo);
        when(precio.getFecha()).thenReturn(fecha);
        when(precio.getValor()).thenReturn(valor);
        return precio;
    }

    @Test
    @DisplayName("Test de evaluación de portafolio sin movimientos")
    void testEvaluarSinMovimientos() {
        Long portafolioId = 1L;
        LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
        LocalDate fechaFin = LocalDate.of(2024, 1, 2);

        Weight weight1 = mockWeight(10L, "HOLA", new BigDecimal("10"));
        Weight weight2 = mockWeight(20L, "APPLE", new BigDecimal("5"));
        List<Weight> weights = Arrays.asList(weight1, weight2);

        Portafolio portafolio = mockPortafolio(portafolioId, fechaInicio, new BigDecimal("100000"), weights);

        when(portafolioRepository.findById(portafolioId)).thenReturn(Optional.of(portafolio));
        when(weightRepository.findByPortafolio(portafolio)).thenReturn(weights);

        Precio precio1d1 = mockPrecio(10L, fechaInicio, new BigDecimal("10"));
        Precio precio2d1 = mockPrecio(20L, fechaInicio, new BigDecimal("20"));
        Precio precio1d2 = mockPrecio(10L, fechaFin, new BigDecimal("12"));
        Precio precio2d2 = mockPrecio(20L, fechaFin, new BigDecimal("18"));

        when(precioRepository.findByFechaBetween(fechaInicio, fechaFin))
                .thenReturn(Arrays.asList(precio1d1, precio2d1, precio1d2, precio2d2));

        List<Map<String, Object>> result = portafolioService.evaluarResultado(portafolioId, fechaInicio, fechaFin);

        assertEquals(2, result.size());
        assertEquals(fechaInicio, result.get(0).get("fecha"));
        assertEquals(fechaFin, result.get(1).get("fecha"));
    }

    @Test
    @DisplayName("Test de evaluación de portafolio con una ventaa")
    void testEvaluarResultadoVenta() {
        Long portafolioId = 1L;
        LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
        LocalDate fechaFin = LocalDate.of(2024, 1, 2);

        Weight weight1 = mockWeight(10L, "AMZN", new BigDecimal("2"));
        List<Weight> weights = Collections.singletonList(weight1);
        Portafolio portafolio = mockPortafolio(portafolioId, fechaInicio, new BigDecimal("1000"), weights);

        when(portafolioRepository.findById(portafolioId)).thenReturn(Optional.of(portafolio));
        when(weightRepository.findByPortafolio(portafolio)).thenReturn(weights);

        Precio precioVentaD1 = mockPrecio(10L, fechaInicio, new BigDecimal("10"));
        Precio precioVentaD2 = mockPrecio(10L, fechaFin, new BigDecimal("12"));
        when(precioRepository.findByActivoIdAndFecha(10L, fechaInicio)).thenReturn(precioVentaD1);
        when(precioRepository.findByFechaBetween(fechaInicio, fechaFin))
                .thenReturn(Arrays.asList(precioVentaD1, precioVentaD2));

        portafolioService.venderActivo(portafolioId, fechaInicio, fechaFin, fechaInicio, 10L, new BigDecimal("20"));

        verify(weight1).setCantidadInicial(new BigDecimal("0.00000000"));
        verify(weightRepository).save(weight1);
    }

    @Test
    void testEvaluarResultadoCompra() {
        Long portafolioId = 1L;
        LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
        LocalDate fechaFin = LocalDate.of(2024, 1, 2);

        Weight weight1 = mockWeight(10L, "CC", new BigDecimal("2"));
        List<Weight> weights = Collections.singletonList(weight1);
        Portafolio portafolio = mockPortafolio(portafolioId, fechaInicio, new BigDecimal("1000"), weights);

        when(portafolioRepository.findById(portafolioId)).thenReturn(Optional.of(portafolio));
        when(weightRepository.findByPortafolio(portafolio)).thenReturn(weights);

        Precio precioCompraD1 = mockPrecio(10L, fechaInicio, new BigDecimal("10"));
        Precio precioCompraD2 = mockPrecio(10L, fechaFin, new BigDecimal("12"));
        when(precioRepository.findByActivoIdAndFecha(10L, fechaInicio)).thenReturn(precioCompraD1);
        when(precioRepository.findByFechaBetween(fechaInicio, fechaFin))
                .thenReturn(Arrays.asList(precioCompraD1, precioCompraD2));

        portafolioService.comprarActivo(portafolioId, fechaInicio, fechaFin, fechaInicio, 10L, new BigDecimal("30"));

        verify(weight1).setCantidadInicial(new BigDecimal("5.00000000"));;
        verify(weightRepository).save(weight1);
    }

    @Test
    void testEvaluarResultadoCompraYVenta() {
        Long portafolioId = 1L;
        LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
        LocalDate fechaFin = LocalDate.of(2024, 1, 2);

        Weight weight1 = mockWeight(10L, "CLP", new BigDecimal("2"));
        Weight weight2 = mockWeight(20L, "EEUU", new BigDecimal("3"));
        List<Weight> weights = Arrays.asList(weight1, weight2);
        Portafolio portafolio = mockPortafolio(portafolioId, fechaInicio, new BigDecimal("1000"), weights);

        when(portafolioRepository.findById(portafolioId)).thenReturn(Optional.of(portafolio));
        when(weightRepository.findByPortafolio(portafolio)).thenReturn(weights);

        Precio precioA1 = mockPrecio(10L, fechaInicio, new BigDecimal("10"));
        Precio precioA2 = mockPrecio(10L, fechaFin, new BigDecimal("12"));
        Precio precioB1 = mockPrecio(20L, fechaInicio, new BigDecimal("20"));
        Precio precioB2 = mockPrecio(20L, fechaFin, new BigDecimal("18"));

        when(precioRepository.findByActivoIdAndFecha(10L, fechaInicio)).thenReturn(precioA1);
        when(precioRepository.findByActivoIdAndFecha(20L, fechaInicio)).thenReturn(precioB1);
        when(precioRepository.findByFechaBetween(fechaInicio, fechaFin))
                .thenReturn(Arrays.asList(precioA1, precioB1, precioA2, precioB2));

        portafolioService.venderActivo(portafolioId, fechaInicio, fechaFin, fechaInicio, 10L, new BigDecimal("20"));
        verify(weight1).setCantidadInicial(new BigDecimal("0.E-8"));
        verify(weightRepository).save(weight1);

        portafolioService.comprarActivo(portafolioId, fechaInicio, fechaFin, fechaInicio, 20L, new BigDecimal("20"));
        verify(weight2).setCantidadInicial(new BigDecimal("4.00000000"));
        verify(weightRepository).save(weight2);
    }
}

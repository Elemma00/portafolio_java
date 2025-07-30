package com.portfolio.portfolio_java.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.portfolio.portfolio_java.services.PortafolioService;

/* 
 * Controlador para gestionar las operaciones relacionadas con el portafolio
 * Este controlador no tiene un manejo de excepciones específico, lo deje así para efectos
 * demostrativos y desarrollo más agil.
 */

@RestController
@RequestMapping("/api/portafolio")
public class PortafolioController {

    @Autowired
    private PortafolioService portafolioService;

    /**
     * Endpoint para evaluar el resultado del portafolio en un rango de fechas
     * @param id ID del portafolio
     * @param fechaInicio Fecha de inicio del análisis
     * @param fechaFin Fecha de fin del análisis
     * @return JSON con la evolución del portafolio entre las fechas dadas.
     */
    @GetMapping("/{id}")
    public List<Map<String, Object>> evolucionPortafolio(
            @PathVariable Long id,
            @RequestParam("fecha_inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fecha_fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        return portafolioService.evaluarResultado(id, fechaInicio, fechaFin);
    }

    
    @GetMapping("/{id}/historialventa")
    public List<Map<String, Object>> historialVenta(
            @PathVariable Long id,
            @RequestParam("fecha_inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fecha_fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam("fecha_venta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVenta,
            @RequestParam("activo_venta_id") Long activoVentaId,
            @RequestParam("monto_venta") BigDecimal montoVenta
    ) {
        return portafolioService.venderActivo(id, fechaInicio, fechaFin, fechaVenta, activoVentaId, montoVenta);
    }

    @GetMapping("/{id}/historialcompra")
    public List<Map<String, Object>> historialCompra(
            @PathVariable Long id,
            @RequestParam("fecha_inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fecha_fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam("fecha_compra") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCompra,
            @RequestParam("activo_compra_id") Long activoCompraId,
            @RequestParam("monto_compra") BigDecimal montoCompra
    ) {
        return portafolioService.comprarActivo(id, fechaInicio, fechaFin, fechaCompra, activoCompraId, montoCompra);
    }
}

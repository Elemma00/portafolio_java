package com.portfolio.portfolio_java.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.portfolio.portfolio_java.models.Portafolio;
import com.portfolio.portfolio_java.models.Precio;
import com.portfolio.portfolio_java.models.Weight;
import com.portfolio.portfolio_java.repositories.PortafolioRepository;
import com.portfolio.portfolio_java.repositories.PrecioRepository;
import com.portfolio.portfolio_java.repositories.WeightRepository;

@Service
public class PortafolioService {

    @Autowired
    private PortafolioRepository portafolioRepository;
    @Autowired
    private PrecioRepository precioRepository;
    @Autowired
    private WeightRepository weightRepository;

    public List<Map<String, Object>> evaluarResultado(Long id, LocalDate fechaInicio, LocalDate fechaFin) {

        Portafolio portafolio = portafolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portafolio no encontrado"));

        Map<Long, BigDecimal> cantidadesPorActivo = getCantidadesPorActivo(portafolio);

        // Obtener precios por fecha 
        Map<LocalDate, Map<Long, BigDecimal>> preciosPorFecha = getPreciosPorFecha(fechaInicio, fechaFin);

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Map.Entry<LocalDate, Map<Long, BigDecimal>> entry : preciosPorFecha.entrySet()) {
            LocalDate fecha = entry.getKey();
            Map<Long, BigDecimal> preciosActivos = entry.getValue();

            // x_{i,t} y V_t
            Map<Long, BigDecimal> montosPorActivo = new HashMap<>();
            BigDecimal valorTotal = BigDecimal.ZERO;
            for (Map.Entry<Long, BigDecimal> precioActivo : preciosActivos.entrySet()) {
                Long activoId = precioActivo.getKey();
                BigDecimal precio = precioActivo.getValue();
                BigDecimal cantidad = cantidadesPorActivo.getOrDefault(activoId, BigDecimal.ZERO);
                BigDecimal monto = precio.multiply(cantidad);
                montosPorActivo.put(activoId, monto);
                valorTotal = valorTotal.add(monto);
            }

            // Calcular w_{i,t}
            Map<Long, BigDecimal> weightsPorActivo = new HashMap<>();
            for (Map.Entry<Long, BigDecimal> montoActivo : montosPorActivo.entrySet()) {
                BigDecimal w = valorTotal.compareTo(BigDecimal.ZERO) > 0
                        ? montoActivo.getValue().divide(valorTotal, 6, BigDecimal.ROUND_HALF_UP)
                        : BigDecimal.ZERO;
                weightsPorActivo.put(montoActivo.getKey(), w);
            }
            
            if (fecha.equals(portafolio.getFechaInicio())) {
                valorTotal = portafolio.getValorInicial().setScale(6, BigDecimal.ROUND_HALF_UP);
            } else {
                valorTotal = valorTotal.setScale(6, BigDecimal.ROUND_HALF_UP);
            }

            List<Map<String, Object>> activos = datosPorActivo(portafolio, cantidadesPorActivo, montosPorActivo,
                    weightsPorActivo);

            Map<String, Object> fila = new HashMap<>();
            fila.put("fecha", fecha);
            fila.put("valor_total", valorTotal);
            fila.put("activos", activos);

            resultado.add(fila);
        }

        return resultado;
    }

    private List<Map<String, Object>> datosPorActivo(Portafolio portafolio, Map<Long, BigDecimal> cantidadesPorActivo, Map<Long, BigDecimal> montosPorActivo, Map<Long, BigDecimal> weightsPorActivo) {
        List<Map<String, Object>> activos = new ArrayList<>();
        for (Long activoId : montosPorActivo.keySet()) {
            Map<String, Object> info = new HashMap<>();

            String nombre = portafolio.getWeights().stream()
                    .filter(a -> a.getActivo().getId().equals(activoId))
                    .findFirst()
                    .map(a -> a.getActivo().getNombre())
                    .orElse("Activo " + activoId);

            info.put("id", activoId);
            info.put("nombre", nombre);
            info.put("weight", weightsPorActivo.get(activoId));
            info.put("cantidad", cantidadesPorActivo.get(activoId));
            info.put("monto", montosPorActivo.get(activoId));
            activos.add(info);
        }
        return activos;
    }

    public List<Map<String, Object>> venderActivo(
            Long portafolioId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            LocalDate fechaVenta,
            Long activoVentaId,
            BigDecimal montoVenta
    ) {
        Portafolio portafolio = portafolioRepository.findById(portafolioId)
                .orElseThrow(() -> new RuntimeException("Portafolio no encontrado"));

        List<Weight> weights = weightRepository.findByPortafolio(portafolio);
        Weight weightVenta = weights.stream()
                .filter(w -> w.getActivo().getId().equals(activoVentaId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activo no encontrado en el portafolio"));

        Precio precioVenta = precioRepository.findByActivoIdAndFecha(activoVentaId, fechaVenta);
        if (precioVenta == null) {
            throw new RuntimeException("Precio venta no encontrado");
        }

        BigDecimal cantidadVenta = montoVenta.divide(precioVenta.getValor(), 8, BigDecimal.ROUND_HALF_UP);
        weightVenta.setCantidadInicial(weightVenta.getCantidadInicial().subtract(cantidadVenta));
        weightRepository.save(weightVenta);

        return evaluarResultado(portafolioId, fechaInicio, fechaFin);
    }

    public List<Map<String, Object>> comprarActivo(
            Long portafolioId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            LocalDate fechaCompra,
            Long activoCompraId,
            BigDecimal montoCompra
    ) {
        Portafolio portafolio = portafolioRepository.findById(portafolioId)
                .orElseThrow(() -> new RuntimeException("Portafolio no encontrado"));

        List<Weight> weights = weightRepository.findByPortafolio(portafolio);
        Weight weightCompra = weights.stream()
                .filter(w -> w.getActivo().getId().equals(activoCompraId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Activo no encontrado en el portafolio"));

        Precio precioCompra = precioRepository.findByActivoIdAndFecha(activoCompraId, fechaCompra);
        if (precioCompra == null) {
            throw new RuntimeException("Precio compra no encontrado");
        }

        BigDecimal cantidadCompra = montoCompra.divide(precioCompra.getValor(), 8, BigDecimal.ROUND_HALF_UP);
        weightCompra.setCantidadInicial(weightCompra.getCantidadInicial().add(cantidadCompra));
        weightRepository.save(weightCompra);

        return evaluarResultado(portafolioId, fechaInicio, fechaFin);
    }

    private Map<LocalDate, Map<Long, BigDecimal>> getPreciosPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Precio> precios = precioRepository.findByFechaBetween(fechaInicio, fechaFin);

        Map<LocalDate, Map<Long, BigDecimal>> preciosPorFecha = new TreeMap<>();
        for (Precio p : precios) {
            preciosPorFecha
                    .computeIfAbsent(p.getFecha(), f -> new HashMap<>())
                    .put(p.getActivo().getId(), p.getValor());
        }
        return preciosPorFecha;
    }

    private Map<Long, BigDecimal> getCantidadesPorActivo(Portafolio portafolio) {
        List<Weight> weights = weightRepository.findByPortafolio(portafolio);
        Map<Long, BigDecimal> cantidadesPorActivo = new HashMap<>();
        for (Weight w : weights) {
            cantidadesPorActivo.put(w.getActivo().getId(), w.getCantidadInicial());
        }
        return cantidadesPorActivo;
    }

}

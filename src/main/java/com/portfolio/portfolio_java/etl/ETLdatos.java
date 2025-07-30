package com.portfolio.portfolio_java.etl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.portfolio.portfolio_java.models.Activo;
import com.portfolio.portfolio_java.models.Portafolio;
import com.portfolio.portfolio_java.models.Precio;
import com.portfolio.portfolio_java.models.Weight;
import com.portfolio.portfolio_java.repositories.ActivoRepository;
import com.portfolio.portfolio_java.repositories.PortafolioRepository;
import com.portfolio.portfolio_java.repositories.PrecioRepository;
import com.portfolio.portfolio_java.repositories.WeightRepository;
import com.portfolio.portfolio_java.util.ExcelUtils;

/**
 * Clase encargada de realizar el proceso ETL desde datos.xlsx 
 * OBS: estoy usando una base de datos SQL en memoria H2 para desarrollo y pruebas
 */
@Component
public class ETLdatos {

    @Autowired
    private ActivoRepository activoRepositorio;
    @Autowired
    private PortafolioRepository portafolioRepositorio;
    @Autowired
    private PrecioRepository precioRepositorio;
    @Autowired
    private WeightRepository weightRepositorio;

    /**
     * Se realiza secuencialmente el proceso ETL
     */
    @Transactional
    public void extraerDatos() {
        try (InputStream datosExcel = new ClassPathResource("assets/datos.xlsx").getInputStream(); Workbook libroExcel = new XSSFWorkbook(datosExcel)) {
            limpiarDatos();
            crearPortafolios();
            cargarPesos(libroExcel);
            cargarPrecios(libroExcel);
        } catch (IOException e) {
            throw new RuntimeException("Error en proceso ETL", e);
        }
    }

    /**
     * limpia la base de datos cada vez que reinicie la app
     **/
    private void limpiarDatos() {
        precioRepositorio.deleteAll();
        weightRepositorio.deleteAll();
        activoRepositorio.deleteAll();
        portafolioRepositorio.deleteAll();
    }

    private void crearPortafolios() {
        LocalDate fechaInicio = LocalDate.of(2022, 2, 15);
        BigDecimal valorInicial = new BigDecimal("1000000000");

        Portafolio portafolio1 = crearPortafolio("Portafolio 1", valorInicial, fechaInicio);
        Portafolio portafolio2 = crearPortafolio("Portafolio 2", valorInicial, fechaInicio);

        portafolioRepositorio.save(portafolio1);
        portafolioRepositorio.save(portafolio2);
    }

    private Portafolio crearPortafolio(String nombre, BigDecimal valorInicial, LocalDate fechaInicio) {
        Portafolio portafolio = new Portafolio();
        portafolio.setNombre(nombre);
        portafolio.setValorInicial(valorInicial);
        portafolio.setFechaInicio(fechaInicio);
        return portafolio;
    }

    private void cargarPesos(Workbook libroExcel) {
        Sheet hojaPesos = libroExcel.getSheet("Weights");
        Sheet hojaPrecios = libroExcel.getSheet("Precios");
        validarHojas(hojaPesos, hojaPrecios);

        Portafolio portafolio1 = obtenerPortafolio("Portafolio 1");
        Portafolio portafolio2 = obtenerPortafolio("Portafolio 2");

        List<String> nombresActivos = obtenerNombresActivos(hojaPrecios);
        Map<String, Double> preciosIniciales = obtenerPreciosIniciales(hojaPrecios, nombresActivos);

        Map<String, Activo> mapaActivos = new HashMap<>();

        for (int fila = 1; fila <= hojaPesos.getLastRowNum(); fila++) {
            Row filaActual = hojaPesos.getRow(fila);
            if (filaActual == null || ExcelUtils.isRowEmpty(filaActual)) {
                continue;
            }

            String nombreActivo = ExcelUtils.getCellStringValue(filaActual.getCell(1));
            if (nombreActivo == null || nombreActivo.trim().isEmpty()) {
                continue;
            }

            Activo activo = mapaActivos.computeIfAbsent(nombreActivo, n -> crearActivo(n));
            Double precioInicial = preciosIniciales.get(nombreActivo);

            crearPeso(filaActual.getCell(2), portafolio1, activo, precioInicial);
            crearPeso(filaActual.getCell(3), portafolio2, activo, precioInicial);
        }
        System.out.println("Cargados pesos y cantidades iniciales para " + mapaActivos.size() + " activos");
    }

    private void validarHojas(Sheet hojaPesos, Sheet hojaPrecios) {
        if (hojaPesos == null) {
            throw new RuntimeException("Hoja 'Weights' no encontrada");
        }
        if (hojaPrecios == null) {
            throw new RuntimeException("Hoja 'Precios' no encontrada");
        }
    }

    private Portafolio obtenerPortafolio(String nombre) {
        return portafolioRepositorio.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException(nombre + " no encontrado"));
    }

    private List<String> obtenerNombresActivos(Sheet hojaPrecios) {
        Row encabezadoPrecios = hojaPrecios.getRow(0);
        List<String> nombresActivos = new ArrayList<>();
        for (int columna = 1; columna < encabezadoPrecios.getLastCellNum(); columna++) {
            String nombreActivo = ExcelUtils.getCellStringValue(encabezadoPrecios.getCell(columna));
            if (nombreActivo != null && !nombreActivo.trim().isEmpty()) {
                nombresActivos.add(nombreActivo.trim());
            }
        }
        return nombresActivos;
    }

    private Map<String, Double> obtenerPreciosIniciales(Sheet hojaPrecios, List<String> nombresActivos) {
        Row filaPreciosIniciales = hojaPrecios.getRow(1);
        Map<String, Double> preciosIniciales = new HashMap<>();
        for (int columna = 1; columna <= nombresActivos.size(); columna++) {
            String nombreActivo = nombresActivos.get(columna - 1);
            Double precio = ExcelUtils.getCellNumericValue(filaPreciosIniciales.getCell(columna));
            preciosIniciales.put(nombreActivo, precio);
        }
        return preciosIniciales;
    }

    private Activo crearActivo(String nombre) {
        Activo nuevoActivo = new Activo();
        nuevoActivo.setNombre(nombre);
        return activoRepositorio.save(nuevoActivo);
    }

    private void crearPeso(Cell celdaPeso, Portafolio portafolio, Activo activo, Double precioInicial) {
        Double valorPeso = ExcelUtils.getCellNumericValue(celdaPeso);
        if (valorPeso != null && precioInicial != null && precioInicial > 0) {
            Weight peso = new Weight();
            peso.setPortafolio(portafolio);
            peso.setActivo(activo);
            peso.setPesoInicial(BigDecimal.valueOf(valorPeso));
            BigDecimal cantidadInicial = calcularCantidadInicial(valorPeso, portafolio.getValorInicial(), precioInicial);
            peso.setCantidadInicial(cantidadInicial);
            weightRepositorio.save(peso);
        }
    }

    /**
     * Calcula la cantidad inicial de un activo en un portafolio, usando la ecuacion del peso y despejando
     */
    private BigDecimal calcularCantidadInicial(Double peso, BigDecimal valorInicial, Double precioInicial) {
        return BigDecimal.valueOf(peso)
                .multiply(valorInicial)
                .divide(BigDecimal.valueOf(precioInicial), 6, BigDecimal.ROUND_HALF_UP);
    }

    private void cargarPrecios(Workbook libroExcel) {
        Sheet hojaPrecios = libroExcel.getSheet("Precios");
        if (hojaPrecios == null) {
            throw new RuntimeException("Hoja 'Precios' no encontrada");
        }

        List<String> nombresActivos = obtenerNombresActivos(hojaPrecios);
        List<Precio> listaPrecios = new ArrayList<>();

        for (int fila = 1; fila <= hojaPrecios.getLastRowNum(); fila++) {
            Row filaActual = hojaPrecios.getRow(fila);
            if (filaActual == null || ExcelUtils.isRowEmpty(filaActual)) {
                continue;
            }

            LocalDate fecha = ExcelUtils.parseDateCell(filaActual.getCell(0));
            if (fecha == null) {
                continue;
            }

            agregarPreciosPorFila(filaActual, nombresActivos, fecha, listaPrecios);
        }
        precioRepositorio.saveAll(listaPrecios);
        System.out.println("Guardados " + listaPrecios.size() + " precios");
    }

    private void agregarPreciosPorFila(Row fila, List<String> nombresActivos, LocalDate fecha, List<Precio> listaPrecios) {
        for (int columna = 1; columna <= nombresActivos.size(); columna++) {
            if (columna - 1 >= nombresActivos.size()) {
                continue;
            }
            Double valorPrecio = ExcelUtils.getCellNumericValue(fila.getCell(columna));
            if (valorPrecio == null || valorPrecio <= 0) {
                continue;
            }

            String nombreActivo = nombresActivos.get(columna - 1);
            Activo activo = activoRepositorio.findByNombre(nombreActivo).orElse(null);
            if (activo == null) {
                continue;
            }

            Precio precio = new Precio();
            precio.setActivo(activo);
            precio.setFecha(fecha);
            precio.setValor(BigDecimal.valueOf(valorPrecio));
            listaPrecios.add(precio);
        }
    }
}

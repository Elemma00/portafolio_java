# Pregunta Portafolio en Java

## ¿Cómo utilizar?

### Iniciar la app

```sh
mvn spring-boot:run  
```
Se inicializará en el puerto 8080

---

A continuación se muestran los comandos cURL para probar la API.

### Consultar evolución del portafolio

**Endpoint:** Consultar la evolución del portafolio 1 entre las fechas dadas:

```sh
curl --location 'localhost:8080/api/portafolio/1?fecha_inicio=2022-02-15&fecha_fin=2023-02-10'
```

Para el portafolio 2, utilice `'api/portafolio/2'`.

---

### Venta de activo

**Endpoint:** Registrar la venta de un activo:

```sh
curl --location 'localhost:8080/api/portafolio/1/historialventa?fecha_inicio=2022-02-15&fecha_fin=2023-02-10&fecha_venta=2022-02-15&activo_venta_id=1&monto_venta=200000000'
```

---

### Compra de activos

**Endpoint:** Registrar la compra de un activo:

```sh
curl --location 'localhost:8080/api/portafolio/1/historialcompra?fecha_inicio=2022-02-15&fecha_fin=2023-02-10&fecha_compra=2022-02-15&activo_compra_id=2&monto_compra=200000000'
```

---

## Visualización de gráficos

Para ver los gráficos del valor total y los pesos del portafolio entre la primera y última fecha de los datos, ejecute:

```sh
cd ./src/pythongraficos/
python graficos.py
```

> **Nota:** Para el gráfico de pesos, solo se muestran los primeros 3 activos para no sobrecargar el gráfico.
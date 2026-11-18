# gastos-api

API REST chica para llevar gastos personales, hecha con Spring Boot 3 (Java 21), Spring Data JPA y H2 en archivo.
**Es un proyecto de práctica**: no tiene autenticación ni está pensado para producción.

## Qué hace

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/gastos` | Crea un gasto (`descripcion`, `categoria`, `monto` > 0, `fecha`). Valida los campos (400 si falla). |
| `GET` | `/gastos` | Lista gastos. Filtros opcionales: `?categoria=comida` o `?desde=2026-11-01&hasta=2026-11-30`. |
| `PUT` | `/gastos/{id}` | Actualiza un gasto (404 si no existe). |
| `DELETE` | `/gastos/{id}` | Borra un gasto (204). |
| `GET` | `/gastos/resumen/{AAAA-MM}` | Total del mes y total por categoría, de mayor a menor. |

Los datos se guardan en `./data/gastos` (H2 en archivo), así que persisten entre ejecuciones.

## Cómo correrlo

Requiere Java 21 y Maven.

```bash
mvn test                      # 2 tests de integración (H2 en memoria)
mvn package
java -jar target/gastos-api-0.1.0.jar    # escucha en http://localhost:8080
```

## Ejemplo

```bash
curl -X POST localhost:8080/gastos -H 'Content-Type: application/json' \
  -d '{"descripcion":"Super","categoria":"comida","monto":100.5,"fecha":"2026-11-05"}'
curl localhost:8080/gastos/resumen/2026-11
# {"mes":"2026-11","total":100.50,"porCategoria":{"comida":100.50}}
```

## Estructura

- `Gasto`: entidad con validaciones.
- `GastoRepository`: consultas por categoría, por rango de fechas y totales agrupados.
- `GastoController`: los endpoints de arriba.

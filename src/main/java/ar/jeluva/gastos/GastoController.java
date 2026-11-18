package ar.jeluva.gastos;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/gastos")
public class GastoController {

    private final GastoRepository repo;

    public GastoController(GastoRepository repo) {
        this.repo = repo;
    }

    /** Lista gastos; filtros opcionales ?categoria=comida o ?desde=2026-01-01&hasta=2026-01-31 */
    @GetMapping
    public List<Gasto> listar(@RequestParam(required = false) String categoria,
                              @RequestParam(required = false) LocalDate desde,
                              @RequestParam(required = false) LocalDate hasta) {
        if (categoria != null) return repo.findByCategoriaIgnoreCaseOrderByFechaDesc(categoria);
        if (desde != null && hasta != null) return repo.findByFechaBetweenOrderByFechaDesc(desde, hasta);
        return repo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Gasto crear(@Valid @RequestBody Gasto gasto) {
        return repo.save(gasto);
    }

    @PutMapping("/{id}")
    public Gasto actualizar(@PathVariable Long id, @Valid @RequestBody Gasto datos) {
        Gasto g = buscar(id);
        g.setDescripcion(datos.getDescripcion());
        g.setCategoria(datos.getCategoria());
        g.setMonto(datos.getMonto());
        g.setFecha(datos.getFecha());
        return repo.save(g);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        repo.delete(buscar(id));
    }

    /** Resumen de un mes (formato 2026-11): total y total por categoría, de mayor a menor. */
    @GetMapping("/resumen/{mes}")
    public Map<String, Object> resumen(@PathVariable String mes) {
        YearMonth ym;
        try {
            ym = YearMonth.parse(mes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mes inválido, usá el formato AAAA-MM");
        }
        List<GastoRepository.TotalCategoria> filas = repo.totalesPorCategoria(ym.atDay(1), ym.atEndOfMonth());
        BigDecimal total = filas.stream().map(GastoRepository.TotalCategoria::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var porCategoria = new java.util.LinkedHashMap<String, BigDecimal>();
        filas.forEach(f -> porCategoria.put(f.getCategoria(), f.getTotal()));
        return Map.of("mes", mes, "total", total, "porCategoria", porCategoria);
    }

    private Gasto buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el gasto " + id));
    }
}

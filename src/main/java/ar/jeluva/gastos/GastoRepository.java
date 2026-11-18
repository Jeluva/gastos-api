package ar.jeluva.gastos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByCategoriaIgnoreCaseOrderByFechaDesc(String categoria);

    List<Gasto> findByFechaBetweenOrderByFechaDesc(LocalDate desde, LocalDate hasta);

    interface TotalCategoria {
        String getCategoria();
        BigDecimal getTotal();
    }

    @Query("select g.categoria as categoria, sum(g.monto) as total from Gasto g "
            + "where g.fecha between :desde and :hasta group by g.categoria order by sum(g.monto) desc")
    List<TotalCategoria> totalesPorCategoria(LocalDate desde, LocalDate hasta);
}

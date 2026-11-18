package ar.jeluva.gastos;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test")
@AutoConfigureMockMvc
class GastoControllerTest {

    @Autowired MockMvc mvc;

    private void crear(String desc, String cat, String monto, String fecha) throws Exception {
        mvc.perform(post("/gastos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"descripcion\":\"%s\",\"categoria\":\"%s\",\"monto\":%s,\"fecha\":\"%s\"}"
                        .formatted(desc, cat, monto, fecha)))
                .andExpect(status().isCreated());
    }

    @Test
    void resumenAgrupaPorCategoria() throws Exception {
        crear("Super", "comida", "100.50", "2026-03-05");
        crear("Cena", "comida", "50", "2026-03-10");
        crear("SUBE", "transporte", "20", "2026-03-11");
        mvc.perform(get("/gastos/resumen/2026-03"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(170.5))
                .andExpect(jsonPath("$.porCategoria.comida").value(150.5));
    }

    @Test
    void rechazaMontoNegativo() throws Exception {
        mvc.perform(post("/gastos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"descripcion\":\"x\",\"categoria\":\"y\",\"monto\":-1,\"fecha\":\"2026-03-05\"}"))
                .andExpect(status().isBadRequest());
    }
}

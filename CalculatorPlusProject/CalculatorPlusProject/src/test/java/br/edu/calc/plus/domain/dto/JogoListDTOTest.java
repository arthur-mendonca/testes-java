package br.edu.calc.plus.domain.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JogoListDTOTest {

    // Valida que getDataFormatada formata a data no padrão esperado.
    @Test
    @DisplayName("getDataFormatada deve formatar data em dd/MM/yyyy hh:mm:ss")
    void getDataFormatada_deveFormatarCorretamente() {
        LocalDateTime data = LocalDateTime.of(2024, 5, 10, 10, 20, 30);
        JogoListDTO dto = new JogoListDTO(1, data, 0.0, 0, 0, 0);
        assertEquals("10/05/2024 10:20:30", dto.getDataFormatada());
    }
}
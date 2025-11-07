package br.edu.calc.plus.domain.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RankingDTOTest {

    @Test
    @DisplayName("getBonificacao deve retornar bonusTotal")
    void getBonificacao_deveRetornarBonusTotal() {
        RankingDTO dto = RankingDTO.builder()
                .idUser(1)
                .nome("User-1")
                .bonusTotal(123.45)
                .competicoes(5)
                .tempoTotal(100)
                .build();

        assertEquals(123.45, dto.getBonificacao());
    }

    @Test
    @DisplayName("getNome deve retornar nome e suportar null")
    void getNome_deveRetornarNome() {
        RankingDTO dto = RankingDTO.builder().idUser(1).nome("Teste User-1").build();
        assertEquals("Teste User-1", dto.getNome());

        RankingDTO vazio = RankingDTO.builder().build();
        assertNull(vazio.getNome());
    }
}
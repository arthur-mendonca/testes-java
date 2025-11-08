package br.edu.calc.plus.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.calc.plus.util.Util;

public class PartidaModelTest {

    // Conta acertos/erros com uma lista de jogos corretos e incorretos.
    @Test
    @DisplayName("getAcertos/getErros devem refletir jogos corretos e incorretos")
    void partida_contagemAcertosErros() {
        Partida p = new Partida(1, LocalDateTime.now(), 0.0, 0);

        Jogo correto = new Jogo(null, 2, 3, EOperator.multiplicacao, 6, 6, 0.0);
        Jogo incorreto = new Jogo(null, 5, 4, EOperator.subtracao, 1, 2, 0.0);

        List<Jogo> jogos = new ArrayList<>();
        jogos.add(correto);
        jogos.add(incorreto);
        p.setJogoList(jogos);

        assertEquals(1, p.getAcertos());
        assertEquals(1, p.getErros());
    }

    // Valida formatação da data da partida conforme utilitário.
    @Test
    @DisplayName("getDataFormatada deve usar padrão dd/MM/yyyy hh:mm:ss")
    void partida_formatacaoData() {
        LocalDateTime data = LocalDateTime.of(2024, 5, 10, 10, 20, 30);
        Partida p = new Partida(1, data, 0.0, 0);

        assertEquals(Util.formatarData(data), p.getDataFormatada());
    }

    // Valida formatação monetária da bonificação da partida.
    @Test
    @DisplayName("getBonificacaoFormatada deve usar padrão monetário de Util")
    void partida_formatacaoBonificacao() {
        Partida p = new Partida(1, LocalDateTime.now(), 1234.5, 0);
        String esperado = Util.formatarMoeda(1234.5);
        assertEquals(esperado, p.getBonificacaoFormatada());
    }
}
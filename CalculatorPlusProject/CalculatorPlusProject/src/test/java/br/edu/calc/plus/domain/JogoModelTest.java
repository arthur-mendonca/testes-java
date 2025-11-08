package br.edu.calc.plus.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JogoModelTest {

    @Test
    @DisplayName("estaCerto deve retornar true quando resposta == resultado")
    void jogo_estaCerto_true() {
        Jogo j = new Jogo(null, 7, 3, EOperator.subtracao, 4, 4, 0.0);
        assertTrue(j.estaCerto());
        assertTrue(j.isCorrect());
    }

    @Test
    @DisplayName("estaCerto deve retornar false quando resposta != resultado")
    void jogo_estaCerto_false() {
        Jogo j = new Jogo(null, 2, 3, EOperator.soma, 5, 6, 0.0);
        assertFalse(j.estaCerto());
        assertFalse(j.isCorrect());
    }
}
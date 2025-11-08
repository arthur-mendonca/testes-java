package br.edu.calc.plus.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.edu.calc.plus.domain.EOperator;
import br.edu.calc.plus.domain.Jogo;
import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.domain.Usuario;

@DataJpaTest
public class JogoRepoIntegrationTest {

    @Autowired
    private JogoRepo jogoRepo;

    @Autowired
    private PartidaRepo partidaRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    private Usuario novoUsuario(String nome, String login) {
        return new Usuario(null, nome, login, login+"@mail.com", "Abcdef1@", "Cidade", LocalDate.of(1990, 1, 1));
    }

    // Garante que consultas de acertos/erros refletem jogos corretos e incorretos persistidos.
    @Test
    @DisplayName("getAllAcertos e getAllErros devem refletir jogos corretos e incorretos")
    void repoJogo_contagensAcertosErros() {
        Usuario u = usuarioRepo.save(novoUsuario("Jogador Test", "jogador01"));

        Partida p = new Partida(null, LocalDateTime.now(), 0.0, 0);
        p.setUsuario(u);
        p = partidaRepo.save(p);

        // Jogo correto (resultado == resposta)
        Jogo j1 = new Jogo(null, 2, 3, EOperator.multiplicacao, 6, 6, 0.0);
        j1.setPartida(p);
        jogoRepo.save(j1);

        // Jogo incorreto
        Jogo j2 = new Jogo(null, 5, 4, EOperator.subtracao, 1, 2, 0.0);
        j2.setPartida(p);
        jogoRepo.save(j2);

        assertEquals(1L, jogoRepo.getAllAcertos());
        assertEquals(1L, jogoRepo.getAllErros());
    }
}
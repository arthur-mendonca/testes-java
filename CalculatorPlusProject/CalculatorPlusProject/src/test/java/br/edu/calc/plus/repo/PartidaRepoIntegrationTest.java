package br.edu.calc.plus.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.domain.Usuario;

@DataJpaTest
public class PartidaRepoIntegrationTest {

    @Autowired
    private PartidaRepo partidaRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    private Usuario novoUsuario(String nome, String login) {
        return new Usuario(null, nome, login, login+"@mail.com", "Abcdef1@", "Cidade", LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("findByUsuarioId deve listar partidas do usuário e findByIdAndUsuarioId deve localizar a partida específica")
    void repositorioPartida_buscasPorUsuario() {
        Usuario u = usuarioRepo.save(novoUsuario("Ciclano Test", "ciclano01"));

        Partida p = new Partida(null, LocalDateTime.now(), 0.0, 0);
        p.setUsuario(u);
        p = partidaRepo.save(p);

        List<Partida> lista = partidaRepo.findByUsuarioId(u.getId());
        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals(p.getId(), lista.get(0).getId());

        Partida encontrada = partidaRepo.findByIdAndUsuarioId(p.getId(), u.getId());
        assertNotNull(encontrada);
        assertEquals(p.getId(), encontrada.getId());

        // usuário diferente não deve encontrar
        Usuario outro = usuarioRepo.save(novoUsuario("Beltrano Test", "beltrano02"));
        Partida inexistente = partidaRepo.findByIdAndUsuarioId(p.getId(), outro.getId());
        assertNull(inexistente);
    }
}
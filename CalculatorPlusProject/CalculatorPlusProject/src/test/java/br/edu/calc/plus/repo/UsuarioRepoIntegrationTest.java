package br.edu.calc.plus.repo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.edu.calc.plus.domain.Usuario;

@DataJpaTest
public class UsuarioRepoIntegrationTest {

    @Autowired
    private UsuarioRepo usuarioRepo;

    private Usuario novoUsuario(String nome, String login) {
        return new Usuario(null, nome, login, login+"@mail.com", "Abcdef1@", "Cidade", LocalDate.of(1990, 1, 1));
    }

    // Confere que findByNome e findByLogin localizam o usuário persistido.
    @Test
    @DisplayName("findByNome e findByLogin devem localizar usuário cadastrado")
    void findByNomeELgin_deveFuncionar() {
        Usuario u = novoUsuario("Fulano Test", "fulano123");
        usuarioRepo.save(u);

        Optional<Usuario> byNome = usuarioRepo.findByNome("Fulano Test");
        Optional<Usuario> byLogin = usuarioRepo.findByLogin("fulano123");

        assertTrue(byNome.isPresent());
        assertTrue(byLogin.isPresent());
        assertEquals(byNome.get().getId(), byLogin.get().getId());
        assertEquals("Fulano Test", byNome.get().getNome());
        assertEquals("fulano123", byLogin.get().getLogin());
    }
}
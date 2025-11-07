package br.edu.calc.plus.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UsuarioModelTest {

    private Usuario novoUsuarioComSenha(String senha) {
        Usuario u = new Usuario(null, "Nome Teste", "loginTest", "email@test.com", senha, "Cidade", LocalDate.of(2000, 1, 1));
        return u;
    }

    private Usuario novoUsuarioComLogin(String login) {
        Usuario u = new Usuario(null, "Nome Teste", login, "email@test.com", "Abcdef1@", "Cidade", LocalDate.of(2000, 1, 1));
        return u;
    }

    @Test
    @DisplayName("senhaValida deve aceitar senha forte com requisitos mínimos")
    void senhaValida_deveAceitarSenhaForte() {
        Usuario u = novoUsuarioComSenha("Abcdef1@");
        assertTrue(u.senhaValida());
    }

    @Test
    @DisplayName("senhaValida deve falhar para senha muito curta")
    void senhaValida_deveFalharSenhaCurta() {
        Usuario u = novoUsuarioComSenha("A1@a");
        assertFalse(u.senhaValida());
    }

    @Test
    @DisplayName("senhaValida deve falhar quando faltar letra maiúscula")
    void senhaValida_deveFalharSemMaiuscula() {
        Usuario u = novoUsuarioComSenha("abcdef1@");
        assertFalse(u.senhaValida());
    }

    @Test
    @DisplayName("senhaValida deve falhar quando faltar dígito")
    void senhaValida_deveFalharSemDigito() {
        Usuario u = novoUsuarioComSenha("Abcdefg@");
        assertFalse(u.senhaValida());
    }

    @Test
    @DisplayName("senhaValida deve falhar quando faltar caractere especial")
    void senhaValida_deveFalharSemEspecial() {
        Usuario u = novoUsuarioComSenha("Abcdef12");
        assertFalse(u.senhaValida());
    }

    @Test
    @DisplayName("loginValida deve aceitar logins entre 6 e 15 caracteres sem espaço")
    void loginValida_deveAceitarIntervalo() {
        assertTrue(novoUsuarioComLogin("user12").loginValida());
        assertTrue(novoUsuarioComLogin("abcdefghijklmno").loginValida());
    }

    @Test
    @DisplayName("loginValida deve falhar para login curto, longo ou com espaço")
    void loginValida_deveFalharCasosInvalidos() {
        assertFalse(novoUsuarioComLogin("user").loginValida()); // curto
        assertFalse(novoUsuarioComLogin("thisloginiswaytoolong").loginValida()); // longo
        assertFalse(novoUsuarioComLogin("user 123").loginValida()); // contém espaço
    }
}
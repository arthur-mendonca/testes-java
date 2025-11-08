package br.edu.calc.plus.service;

import br.edu.calc.plus.domain.Usuario;
import br.edu.calc.plus.domain.dto.UserDTO;
import br.edu.calc.plus.repo.UsuarioRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Teste de Integração (CTI) para o RF-02 (Unicidade).
 * Valida a @Column(unique=true) da entidade Usuario
 * quando o UsuarioService tenta salvar.
 *
 * @SpringBootTest: Carrega o contexto COMPLETO do Spring Boot.
 * @Transactional: Garante que cada teste rode em uma transação que
 *                 será DESFEITA (rollback) ao final. O banco de dados
 *                 permanece limpo após cada teste.
 */
@SpringBootTest
@Transactional
class CreateUsuarioValidationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepo usuarioRepo;

    /**
     * Helper para criar um DTO válido
     */
    private UserDTO criarDtoValido(String email, String login) {
        UserDTO dto = new UserDTO();
        dto.setNome("Nome Teste CTI");
        dto.setLogin(login);
        dto.setEmail(email);
        dto.setSenha("SenhaValida123");
        dto.setCidade("Cidade Teste");
        dto.setNascimento(LocalDate.of(2000, 1, 1));
        return dto;
    }

    @BeforeEach
    void setUp() {
        // Limpa o repositório ANTES de cada teste
        usuarioRepo.deleteAll();
    }

    // Teste de integração: salva usuário válido único e confirma persistência.
    @Test
    @DisplayName("CTI-04 (RF-02): Deve cadastrar usuário com sucesso (dados válidos e únicos)")
    void deveCadastrarUsuarioComSucesso() {
        // Arrange
        // 1. Cria um DTO com dados válidos e únicos
        UserDTO dtoValido = criarDtoValido("usuario.sucesso@teste.com", "loginSucesso");

        // Act
        // 2. Tenta salvar. O assertDoesNotThrow confirma "sem exceções"
        Assertions.assertDoesNotThrow(() -> {
            usuarioService.save(dtoValido);
        }, "O cadastro falhou com uma exceção inesperada (ex: DataIntegrityViolation).");

        // Assert
        // 3. Verifica se foi "persistido no banco"
        Usuario usuarioSalvo = usuarioRepo.findByLogin("loginSucesso").orElse(null);

        Assertions.assertNotNull(usuarioSalvo, "O usuário não foi encontrado no banco após o save.");
        Assertions.assertEquals("loginSucesso", usuarioSalvo.getLogin(), "O login salvo no banco está incorreto.");
        Assertions.assertEquals("Nome Teste CTI", usuarioSalvo.getNome(), "O nome salvo no banco está incorreto.");
    }
}
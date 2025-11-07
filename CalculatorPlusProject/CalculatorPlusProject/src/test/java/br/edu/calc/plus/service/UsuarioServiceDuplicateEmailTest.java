package br.edu.calc.plus.service;

import br.edu.calc.plus.domain.dto.UserDTO;
import br.edu.calc.plus.repo.UsuarioRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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
class UsuarioServiceDuplicateEmailTest {

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
        // (O @Transactional já faz isso, mas é uma garantia extra)
        usuarioRepo.deleteAll();
    }

    // =================================================================
    // ESTE É O TESTE CTI-01 QUE VOCÊ PEDIU
    // =================================================================
    @Test
    @DisplayName("CTI-01 (RF-02): Deve lançar DataIntegrityViolationException ao salvar EMAIL duplicado")
    void deveLancarExcecaoAoSalvarEmailDuplicado() {
        // Arrange
        // 1. Cria e salva o primeiro usuário
        UserDTO dto1 = criarDtoValido("email.duplicado@teste.com", "loginUnico1");
        usuarioService.save(dto1); // Salva no banco

        // 2. Cria o segundo usuário com o MESMO email
        UserDTO dto2 = criarDtoValido("email.duplicado@teste.com", "loginUnico2");

        // Act & Assert
        // Espera que o service.save() quebre com a exceção do banco (p. 6)
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioService.save(dto2);
        }, "Deveria ter lançado DataIntegrityViolationException por email duplicado.");
    }

    // =================================================================
    // ESTE É O TESTE CTI-02 (BÔNUS, MESMA LÓGICA)
    // =================================================================
    @Test
    @DisplayName("CTI-02 (RF-02): Deve lançar DataIntegrityViolationException ao salvar LOGIN duplicado")
    void deveLancarExcecaoAoSalvarLoginDuplicado() {
        // Arrange
        // 1. Cria e salva o primeiro usuário
        UserDTO dto1 = criarDtoValido("email.unico1@teste.com", "loginDuplicado");
        usuarioService.save(dto1); // Salva no banco

        // 2. Cria o segundo usuário com o MESMO login
        UserDTO dto2 = criarDtoValido("email.unico2@teste.com", "loginDuplicado");

        // Act & Assert
        // Espera que o service.save() quebre com a exceção do banco (p. 6)
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioService.save(dto2);
        }, "Deveria ter lançado DataIntegrityViolationException por login duplicado.");
    }
}
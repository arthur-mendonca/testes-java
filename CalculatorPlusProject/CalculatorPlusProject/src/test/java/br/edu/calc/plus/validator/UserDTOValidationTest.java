package br.edu.calc.plus.validator;

import br.edu.calc.plus.domain.dto.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Teste de Unidade (CTU) para as regras de validação do RNF-04
 */
class UserDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Configura o validador padrão do Java (o mesmo que o Spring usa)
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Helper para criar um DTO válido
     */
    private UserDTO criarDtoValido() {
        UserDTO dto = new UserDTO();
        dto.setNome("Nome Valido");
        dto.setLogin("loginvalido");
        dto.setEmail("email@valido.com");
        dto.setSenha("SenhaValida123"); // 13 chars
        dto.setCidade("Cidade Valida");
        dto.setNascimento(LocalDate.of(2000, 1, 1));
        return dto;
    }

    // Validação: senha com menos de 8 caracteres deve gerar violação @Size.
    @Test
    @DisplayName("CTU-02 (RNF-04): Deve falhar (retornar violação) para senha CURTA (< 8)")
    void deveFalharParaSenhaCurta() {
        // Arrange
        UserDTO dto = criarDtoValido();
        dto.setSenha("1234567"); // 7 caracteres. Inválido

        // Act
        // Roda a validação
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);

        // Assert
        Assertions.assertFalse(violations.isEmpty(), "Deveria ter falhado por senha curta");

        // Pega a mensagem da violação
        String mensagem = violations.iterator().next().getMessage();
        Assertions.assertEquals("A senha deve ter entre 8 e 16 caracteres", mensagem);
    }

    // Validação: senha com mais de 16 caracteres deve gerar violação @Size.
    @Test
    @DisplayName("CTU-02.1 (RNF-04): Deve falhar (retornar violação) para senha LONGA (> 16)")
    void deveFalharParaSenhaLonga() {
        // Arrange
        UserDTO dto = criarDtoValido();
        dto.setSenha("12345678901234567"); // 17 caracteres. Inválido

        // Act
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);

        // Assert
        Assertions.assertFalse(violations.isEmpty(), "Deveria ter falhado por senha longa");
        String mensagem = violations.iterator().next().getMessage();
        Assertions.assertEquals("A senha deve ter entre 8 e 16 caracteres", mensagem);
    }

    // Validação: senha em branco deve gerar violações @NotBlank e @Size.
    @Test
    @DisplayName("CTU-02.2 (RNF-04): Deve falhar (retornar violação) para senha EM BRANCO")
    void deveFalharParaSenhaEmBranco() {
        // Arrange
        UserDTO dto = criarDtoValido();
        dto.setSenha(""); // Inválido

        // Act
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);

        // Assert
        Assertions.assertFalse(violations.isEmpty(), "Deveria ter falhado por senha em branco");

        // Testa se AMBAS as mensagens de erro (@NotBlank e @Size) apareceram
        Set<String> mensagens = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        Assertions.assertTrue(mensagens.contains("A senha é obrigatória"));
        Assertions.assertTrue(mensagens.contains("A senha deve ter entre 8 e 16 caracteres"));
    }

    // Validação: senha no intervalo permitido não deve gerar violações.
    @Test
    @DisplayName("CTU-03 (RNF-04): Deve passar (0 violações) para senha com tamanho válido")
    void devePassarParaSenhaComTamanhoValido() {
        // Arrange
        UserDTO dtoValido = criarDtoValido(); // Senha "SenhaValida123" (13 chars)

        // Act
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dtoValido);

        // Assert
        Assertions.assertTrue(violations.isEmpty(), "Não deveria ter nenhuma violação de validação");
    }
}
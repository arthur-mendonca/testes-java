package br.edu.calc.plus.service;

import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.domain.Usuario;
import br.edu.calc.plus.domain.dto.RankingDTO;
import br.edu.calc.plus.repo.PartidaRepo;
import br.edu.calc.plus.repo.UsuarioRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Teste de Integração (CTI) para o RF-06 (Ranking).
 *
 * @SpringBootTest: Carrega o contexto COMPLETO do Spring Boot.
 * @Transactional: Garante que cada teste rode em uma transação que
 *                 será DESFEITA (rollback) ao final. O banco de dados
 *                 permanece limpo após cada teste.
 */
@SpringBootTest
@Transactional
class RankingIntegrationTest {

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private PartidaRepo partidaRepo;

    private Usuario criarUsuario(String apelido, String email) {
        Usuario user = new Usuario();
        user.setNome(apelido);
        user.setLogin(apelido);
        user.setEmail(email);
        user.setSenha("SenhaForte123");
        user.setCidade("Cidade Teste");
        user.setDataNascimento(LocalDate.of(2000, 1, 1));
        return usuarioRepo.save(user);
    }

    private void criarPartida(Usuario user, double bonificacao) {
        Partida p = new Partida(null, LocalDateTime.now(), bonificacao, 0);
        p.setUsuario(user);
        partidaRepo.save(p);
    }

    @BeforeEach
    void setUp() {
        partidaRepo.deleteAll();
        usuarioRepo.deleteAll();
    }

    // Integração: testa se o ranking retorna os 20 usuários com maior bônus total
    @Test
    @DisplayName("CTI-05 (RF-06): Deve retornar o Top 20 ordenado por bônus total")
    void deveRetornarTop20OrdenadoCorretamente() {

        // 1. Arrange: Inserir 25 usuários com pontuações variadas
        for (int i = 1; i <= 25; i++) {
            Usuario user = criarUsuario("User-" + i, "user" + i + "@teste.com");

            double bonus = 101 - i;
            if (i == 1) {
                criarPartida(user, 50.0);
                criarPartida(user, 50.0);
            } else {
                criarPartida(user, bonus);
            }
        }

        List<RankingDTO> ranking = partidaService.getRanking();

        Assertions.assertEquals(20, ranking.size(), "O ranking não retornou o Top 20.");

        // 3.2. Deve estar ordenada decrescentemente
        RankingDTO primeiroLugar = ranking.get(0);
        RankingDTO vigesimoLugar = ranking.get(19);

        // (User-1 deve ter 100.0)
        Assertions.assertEquals("User-1", primeiroLugar.getNome());
        Assertions.assertEquals(100.0, primeiroLugar.getBonificacao(), 0.001);

        // (User-20 deve ter 81.0)
        Assertions.assertEquals("User-20", vigesimoLugar.getNome());
        Assertions.assertEquals(81.0, vigesimoLugar.getBonificacao(), 0.001);

        // 3.3. Garante que o User-21 (com 80.0) não está na lista
        List<Object> apelidosNoTop20 = ranking.stream()
                .map(RankingDTO::getNome)
                .collect(Collectors.toList());

        Assertions.assertFalse(apelidosNoTop20.contains("User-21"),
                "User-21 (posição 21) não deveria estar no Top 20.");
    }
}
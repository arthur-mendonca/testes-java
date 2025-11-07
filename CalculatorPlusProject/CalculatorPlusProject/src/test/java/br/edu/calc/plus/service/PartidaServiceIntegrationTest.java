package br.edu.calc.plus.service;

import java.time.LocalDate;

import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.domain.Usuario;
import br.edu.calc.plus.repo.PartidaRepo;
import br.edu.calc.plus.repo.UsuarioRepo;
import org.junit.jupiter.api.Assertions;

@SpringBootTest
@Transactional
public class PartidaServiceIntegrationTest {
    @Autowired
    private PartidaService partidaService;

    @Autowired
    private UsuarioRepo usuarioRepo; // Usado para criar o usuário de teste

    @Autowired
    private PartidaRepo partidaRepo; // Usado para limpar, se necessário

    private Usuario usuarioDeTeste;

    @BeforeEach
    void setUp() {
        // Limpa o banco (garantido pelo @Transactional, mas é boa prática)
        partidaRepo.deleteAll();
        usuarioRepo.deleteAll();

        // Arrange (Organizar)
        // Criamos um usuário real no banco DE TESTE
        usuarioDeTeste = new Usuario();
        usuarioDeTeste.setLogin("testeCti03");
        usuarioDeTeste.setEmail("testeCti03@teste.com");
        usuarioDeTeste.setNome("Teste CTI 03");
        usuarioDeTeste.setSenha("SenhaForte123");
        usuarioDeTeste.setCidade("São Paulo");
        usuarioDeTeste.setDataNascimento(LocalDate.of(2000, 1, 1));
        usuarioRepo.save(usuarioDeTeste);
    }

    @Test
    @DisplayName("CTI-03 (RF-04): Deve bloquear segunda competição no dia (Teste de BD)")
    void deveBloquearSegundaCompeticaoNoDia() throws Exception {

        // 1. Verifica (no BD) se o usuário NUNCA competiu
        boolean competiuAntes = partidaService.userJaCompetiuHoje(usuarioDeTeste.getId());
        Assertions.assertFalse(competiuAntes, "Usuário não deveria ter competido ainda (BD real).");

        // 2. Tenta iniciar a PRIMEIRA partida (Isso SALVA no BD)
        Partida p1 = partidaService.iniciarPartida(usuarioDeTeste.getId());
        Assertions.assertNotNull(p1, "A primeira partida não foi iniciada (BD real).");

        // --- TESTA O BLOQUEIO ---

        // 3. Verifica (no BD) se o sistema AGORA detecta a competição
        boolean competiuDepois = partidaService.userJaCompetiuHoje(usuarioDeTeste.getId());
        Assertions.assertTrue(competiuDepois, "Sistema falhou em detectar a primeira competição (BD real).");
    }
}

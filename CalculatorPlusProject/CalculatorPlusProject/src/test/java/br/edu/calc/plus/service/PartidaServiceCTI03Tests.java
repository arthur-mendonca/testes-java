package br.edu.calc.plus.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.edu.calc.plus.repo.JogoRepo;
import br.edu.calc.plus.repo.PartidaRepo;
import br.edu.calc.plus.repo.UsuarioRepo;

// RODAR ESTE TESTE
// mvn -Dtest=PartidaServiceUnitTest test
class PartidaServiceUnitTest {

    private PartidaService service;

    private PartidaRepo pDao;
    private JogoRepo jDao;
    private UsuarioRepo uDao;
    private JogoService jogoService;

    @BeforeEach
    void setUp() throws Exception {
        service = new PartidaService();

        pDao = Mockito.mock(PartidaRepo.class);
        jDao = Mockito.mock(JogoRepo.class);
        uDao = Mockito.mock(UsuarioRepo.class);
        jogoService = Mockito.mock(JogoService.class);

        // Injeta mocks nos campos privados via reflexão
        inject(service, "pDao", pDao);
        inject(service, "jDao", jDao);
        inject(service, "uDao", uDao);
        inject(service, "jogoService", jogoService);
    }

    private static void inject(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    // Simula usuário que já competiu hoje e espera retorno true da regra.
    @Test
    @DisplayName("CTI-03 (RF-04): Deve identificar que o usuário já competiu hoje")
    void userJaCompetiuHoje_DeveRetornarTrue_QuandoUsuarioJaCompetiuHoje() {
        // Arrange: simula que o usuário já competiu hoje
        Mockito.when(pDao.getUsuarioCompetil(Mockito.anyInt(), Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)))
                .thenReturn(1L);

        // Act: consulta a regra no serviço
        boolean jaCompetiu = service.userJaCompetiuHoje(123);

        // Assert: a regra deve indicar bloqueio no mesmo dia
        assertTrue(jaCompetiu);
    }
}
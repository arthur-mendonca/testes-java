package br.edu.calc.plus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.calc.plus.domain.EOperator;
import br.edu.calc.plus.domain.Jogo;
import br.edu.calc.plus.domain.Partida;
import br.edu.calc.plus.repo.JogoRepo;
import br.edu.calc.plus.repo.PartidaRepo;
import br.edu.calc.plus.repo.UsuarioRepo;

class PartidaServiceCoreUnitTests {

    private PartidaService service;

    private PartidaRepo pDao;
    private JogoRepo jDao;
    private UsuarioRepo uDao;
    private JogoService jogoService;

    @BeforeEach
    void setUp() throws Exception {
        service = new PartidaService();

        pDao = mock(PartidaRepo.class);
        jDao = mock(JogoRepo.class);
        uDao = mock(UsuarioRepo.class);
        jogoService = mock(JogoService.class);

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

    // Testa savePartida: acerto aumenta bonificação e persiste Jogo/Partida.
    @Test
    @DisplayName("savePartida: adiciona bônus quando resposta correta e persiste Jogo/Partida")
    void savePartida_deveAdicionarBonusQuandoCorreto() throws Exception {
        Partida p = new Partida(77, LocalDateTime.now(), 100.0, 0);
        Jogo j = new Jogo(1, 5, 5, EOperator.soma, 10, 10, 2.0);
        List<Jogo> jogos = new ArrayList<>();
        jogos.add(j);
        p.setJogoList(jogos);

        when(pDao.findByIdAndUsuarioId(77, 123)).thenReturn(p);

        Partida result = service.savePartida(77, 123, 1, 1, 10.0);

        assertNotNull(result);
        assertEquals(102.0, p.getBonificacao(), 0.0001);
        verify(jDao, times(1)).save(j);
        verify(pDao, times(1)).save(p);
    }

    // Testa savePartida: erro reduz bonificação pela metade e persiste Jogo/Partida.
    @Test
    @DisplayName("savePartida: reduz bônus pela metade quando resposta errada e persiste Jogo/Partida")
    void savePartida_deveReduzirBonusQuandoErrado() throws Exception {
        Partida p = new Partida(77, LocalDateTime.now(), 100.0, 0);
        Jogo j = new Jogo(1, 5, 5, EOperator.soma, 10, 10, 2.0);
        List<Jogo> jogos = new ArrayList<>();
        jogos.add(j);
        p.setJogoList(jogos);

        when(pDao.findByIdAndUsuarioId(77, 123)).thenReturn(p);

        Partida result = service.savePartida(77, 123, 1, 1, 11.0);

        assertNotNull(result);
        assertEquals(99.0, p.getBonificacao(), 0.0001);
        verify(jDao, times(1)).save(j);
        verify(pDao, times(1)).save(p);
    }

    // Testa FinalizaPartida: calcula tempo e dobra bônus no acerto perfeito.
    @Test
    @DisplayName("FinalizaPartida: calcula tempo e dobra bonificação para acerto perfeito")
    void finalizaPartida_deveCalcularTempoEDobrarBonusQuandoPerfeito() throws Exception {
        Partida p = new Partida(77, LocalDateTime.now(), 50.0, 0);
        Jogo j1 = new Jogo(null, 2, 3, EOperator.multiplicacao, 6, 6, 0.0);
        Jogo j2 = new Jogo(null, 10, 2, EOperator.divisao, 5, 5, 0.0);
        List<Jogo> jogos = new ArrayList<>();
        jogos.add(j1);
        jogos.add(j2);
        p.setJogoList(jogos);

        when(pDao.findByIdAndUsuarioId(77, 123)).thenReturn(p);

        long n = 2L;
        LocalDateTime inicio = LocalDateTime.now().minusSeconds(n);

        Partida result = service.FinalizaPartida(77, 123, inicio);

        assertNotNull(result);
        assertTrue(p.getTempo() >= n, "Tempo deveria ser pelo menos " + n + " segundos");
        assertEquals(100.0, p.getBonificacao(), 0.0001);
        verify(pDao, times(1)).save(p);
    }

    // Testa getPartida: retorna partida e inicializa jogoList para evitar lazy.
    @Test
    @DisplayName("getPartida: retorna partida e acessa jogoList para evitar lazy")
    void getPartida_deveRetornarEInicializarLista() throws Exception {
        Partida p = spy(new Partida(77, LocalDateTime.now(), 0.0, 0));
        p.setJogoList(new ArrayList<>());

        when(pDao.findByIdAndUsuarioId(77, 123)).thenReturn(p);

        Partida result = service.getPartida(77, 123);

        assertNotNull(result);
        assertEquals(p, result);
        verify(p, times(1)).getJogoList();
    }
}
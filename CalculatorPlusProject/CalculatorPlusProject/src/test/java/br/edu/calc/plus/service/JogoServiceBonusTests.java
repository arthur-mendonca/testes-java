package br.edu.calc.plus.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.calc.plus.domain.EOperator;
import br.edu.calc.plus.repo.JogoRepo;
import org.mockito.Mockito;

// PARA RODAR
// mvn -Dtest=JogoServiceBonusTests test
class JogoServiceBonusTests {

    private JogoService jogoService;

    @BeforeEach
    void setUp() throws Exception {
        // Instancia o serviço sem contexto Spring
        jogoService = new JogoService();
        // Cria mock do JogoRepo para evitar NullPointer no bonusUsuario()
        JogoRepo jRepoMock = Mockito.mock(JogoRepo.class);
        Mockito.when(jRepoMock.getAllAcertosUser(Mockito.anyInt())).thenReturn(0L);
        // Injeta o mock no campo privado 'jDao' via reflexão
        Field f = JogoService.class.getDeclaredField("jDao");
        f.setAccessible(true);
        f.set(jogoService, jRepoMock);
    }

    /** Helper para acessar o método privado bonusOperacao(EOperator) */
    private double AcessarBonusOperacao(EOperator operador) throws Exception {
        Method metodoBonusOperacao = JogoService.class.getDeclaredMethod(
                "bonusOperacao",
                EOperator.class);
        metodoBonusOperacao.setAccessible(true);
        return (double) metodoBonusOperacao.invoke(jogoService, operador);
    }

    // Valida cálculo de bônus para divisão: base 1.00 vira 1.90.
    @Test
    @DisplayName("CTU-01 (RF-05): calcular bônus de DIVISAO = 1.90 para base 1.00")
    void calcularBonusDivisaoRetornaUmPontoNoventa() throws Exception {
        // Arrange
        double valorBase = 1.00;
        // Act
        double bonusPercentual = AcessarBonusOperacao(EOperator.divisao);
        double resultado = valorBase * (1 + bonusPercentual);
        // Assert
        assertEquals(1.90, resultado, 0.0001);
    }

    // Verifica bônus percentual de multiplicação (50%).
    @Test
    @DisplayName("CTU-01.1 (RF-05): Deve calcular bônus de 50% para MULTIPLICACAO")
    void deveCalcularBonusMultiplicacao() throws Exception {
        // Arrange
        EOperator operacao = EOperator.multiplicacao;
        double resultadoEsperado = 0.5;

        // Act
        double resultadoReal = AcessarBonusOperacao(operacao);

        // Assert
        Assertions.assertEquals(resultadoEsperado, resultadoReal, 0.001,
                "O cálculo do bônus de multiplicação (50%) está incorreto.");
    }

    // Verifica bônus percentual de subtração (20%).
    @Test
    @DisplayName("CTU-01.2 (RF-05): Deve calcular bônus de 20% para SUBTRACAO")
    void deveCalcularBonusSubtracao() throws Exception {
        // Arrange
        EOperator operacao = EOperator.subtracao;
        double resultadoEsperado = 0.2;

        // Act
        double resultadoReal = AcessarBonusOperacao(operacao);

        // Assert
        Assertions.assertEquals(resultadoEsperado, resultadoReal, 0.001,
                "O cálculo do bônus de subtração (20%) está incorreto.");
    }

    // Verifica bônus percentual de adição (10%).
    @Test
    @DisplayName("CTU-01.3 (RF-05): Deve calcular bônus de 10% para ADICAO")
    void deveCalcularBonusAdicao() throws Exception {
        // Arrange
        EOperator operacao = EOperator.soma;
        double resultadoEsperado = 0.1;

        // Act
        double resultadoReal = AcessarBonusOperacao(operacao);

        // Assert
        Assertions.assertEquals(resultadoEsperado, resultadoReal, 0.001,
                "O cálculo do bônus de adição (10%) está incorreto.");
    }

    /**
     * CTU-04 (RF-05): Validar proteção contra Divisão por Zero.
     **/
    // Assegura que criarJogosAleatorio não lança ArithmeticException (divisão por zero).
    @Test
    @DisplayName("CTU-04 (RF-05): Não deve lançar DivisaoPorZero (ArithmeticException)")
    void criarJogosAleatorio_NaoDeveLancarExcecaoDivisaoPorZero() {
        // Arrange
        int idUsuario = 1;
        int numeroDeTestes = 1000; // Roda a criação de jogos 1000 vezes

        // Act & Assert
        // 'assertDoesNotThrow' para garantir que nenhuma
        // ArithmeticException (ou outra) seja lançada durante a execução.
        Assertions.assertDoesNotThrow(() -> {
            for (int i = 0; i < numeroDeTestes; i++) {
                jogoService.criarJogosAleatorio(10, idUsuario);
            }
        }, "O método 'criarJogosAleatorio' lançou uma exceção inesperada, " +
                "possivelmente uma ArithmeticException (divisão por zero).");

        // Se este teste FALHAR, precisa corrigir 'criarJogosAleatorio'
        // para garantir que 'v2' não seja 0 quando a operação for 'divisao'.
    }
}
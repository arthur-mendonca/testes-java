package br.edu.calc.plus.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.calc.plus.domain.EOperator;
import br.edu.calc.plus.domain.Jogo;
import br.edu.calc.plus.domain.Partida;

import br.edu.calc.plus.service.JogoService;
import br.edu.calc.plus.service.PartidaService;
import br.edu.calc.plus.util.LogadoUtil;
import br.edu.calc.plus.repo.UsuarioRepo;
import br.edu.calc.plus.config.security.user.UserDetailsServiceImpl;

@WebMvcTest(controllers = CompeticaoController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class CompeticaoControllerWebTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartidaService partidaService;

    @MockBean
    private JogoService jogoService;

    @MockBean
    private LogadoUtil logadoUtil;

    @MockBean
    private UsuarioRepo usuarioRepo;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private RestTemplateBuilder restTemplateBuilder;

    @Test
    @DisplayName("Controller: bloqueia nova competição quando já competiu hoje")
    void deveBloquearQuandoJaCompetiuHoje() throws Exception {
        Mockito.when(logadoUtil.getIdUserLogado(Mockito.any())).thenReturn(123);
        Mockito.when(partidaService.userJaCompetiuHoje(123)).thenReturn(true);

        mockMvc.perform(get("/competicao/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competicao"))
                .andExpect(flash().attributeExists("success"));
    }

    // Principal fake para satisfazer layout com expressão #authentication.getPrincipal().nome
    static class UsuarioPrincipalFake {
        private final String nome;
        UsuarioPrincipalFake(String nome) { this.nome = nome; }
        public String getNome() { return nome; }
    }

    @Test
    @DisplayName("Controller: detalhe exibe view 'detalheJogo' com partida no modelo")
    void deveExibirDetalhePartidaQuandoEncontrada() throws Exception {
        // Arrange
        Mockito.when(logadoUtil.getIdUserLogado(Mockito.any())).thenReturn(123);

        Partida p = new Partida(5, LocalDateTime.now(), 10.0, 60);
        List<Jogo> jogos = new ArrayList<>();
        Jogo j = new Jogo(null, 2, 3, EOperator.multiplicacao, 6, 6, 0.0);
        jogos.add(j);
        p.setJogoList(jogos);

        Mockito.when(partidaService.getPartida(5, 123)).thenReturn(p);

        UsuarioPrincipalFake principal = new UsuarioPrincipalFake("Usuario Teste");
        TestingAuthenticationToken auth = new TestingAuthenticationToken(principal, null);

        // Act & Assert
        mockMvc.perform(get("/competicao/{id}/detalhe", 5).with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("detalheJogo"))
                .andExpect(model().attributeExists("partida"));
    }

    @Test
    @DisplayName("Controller: detalhe redireciona com erro quando partida não encontrada")
    void deveRedirecionarQuandoPartidaNaoEncontrada() throws Exception {
        // Arrange
        Mockito.when(logadoUtil.getIdUserLogado(Mockito.any())).thenReturn(123);
        Mockito.when(partidaService.getPartida(5, 123)).thenThrow(new Exception("partida não encontrada "));

        UsuarioPrincipalFake principal = new UsuarioPrincipalFake("Usuario Teste");
        TestingAuthenticationToken auth = new TestingAuthenticationToken(principal, null);

        // Act & Assert
        mockMvc.perform(get("/competicao/{id}/detalhe", 5).with(authentication(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competicao"))
                .andExpect(flash().attributeExists("error"));
    }

}
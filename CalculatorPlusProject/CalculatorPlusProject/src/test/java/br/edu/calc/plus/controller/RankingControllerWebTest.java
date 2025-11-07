package br.edu.calc.plus.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import br.edu.calc.plus.domain.dto.RankingDTO;
import br.edu.calc.plus.service.PartidaService;
import br.edu.calc.plus.util.LogadoUtil;
import br.edu.calc.plus.config.security.user.UserDetailsServiceImpl;
import br.edu.calc.plus.config.security.user.UserLogado;
import br.edu.calc.plus.repo.UsuarioRepo;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = RankingController.class)
public class RankingControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartidaService partidaService;

    @MockBean
    private LogadoUtil logadoUtil; // apenas para satisfazer injeção

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UsuarioRepo usuarioRepo;

    @MockBean
    private RestTemplateBuilder restTemplateBuilder;

    @BeforeEach
    void setupRestTemplate() {
        when(restTemplateBuilder.build()).thenReturn(new RestTemplate());
    }

    @Test
    @DisplayName("GET /ranking deve popular modelo e retornar a view")
    void getRanking_deveRetornarView() throws Exception {
        List<RankingDTO> lista = List.of(RankingDTO.builder().idUser(1).nome("User-1").bonusTotal(10).competicoes(1).tempoTotal(50).build());
        when(partidaService.getRanking()).thenReturn(lista);

        UserLogado principal = new UserLogado(
                1,
                "User-1",
                "user1@mail.com",
                "user",
                "SenhaValida123",
                java.util.Set.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))
        );

        mockMvc.perform(get("/ranking").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("ranking"))
                .andExpect(model().attributeExists("lista"))
                .andExpect(model().attribute("lista", hasSize(1)));
    }
}
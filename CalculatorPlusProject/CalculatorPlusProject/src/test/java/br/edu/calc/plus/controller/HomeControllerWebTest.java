package br.edu.calc.plus.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import br.edu.calc.plus.service.JogoService;
import br.edu.calc.plus.service.PartidaService;
import br.edu.calc.plus.service.UsuarioService;
import br.edu.calc.plus.util.LogadoUtil;
import br.edu.calc.plus.config.security.user.UserDetailsServiceImpl;
import br.edu.calc.plus.repo.UsuarioRepo;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = HomeController.class)
public class HomeControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private PartidaService partidaService;

    @MockBean
    private JogoService jogoService;

    @MockBean
    private LogadoUtil logadoUtil;

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
    @DisplayName("GET /home deve popular métricas e retornar view home")
    void getHome_devePopularMetricas() throws Exception {
        when(usuarioService.getCountUsers()).thenReturn(42L);
        when(partidaService.getPremioUsers()).thenReturn("Cr$ 1,234.00");
        when(jogoService.getAllErros()).thenReturn("+3k");
        when(jogoService.getAllAcertos()).thenReturn(999L);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("numeroUsers", 42L))
                .andExpect(model().attribute("premiacaoUsers", "Cr$ 1,234.00"))
                .andExpect(model().attribute("numeroErros", "+3k"))
                .andExpect(model().attribute("numeroAcertos", 999L));
    }
}
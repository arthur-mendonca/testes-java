package br.edu.calc.plus.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

}
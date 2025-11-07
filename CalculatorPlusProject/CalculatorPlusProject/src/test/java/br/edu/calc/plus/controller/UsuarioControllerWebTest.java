package br.edu.calc.plus.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import br.edu.calc.plus.service.UsuarioService;
import br.edu.calc.plus.config.security.user.UserDetailsServiceImpl;
import br.edu.calc.plus.repo.UsuarioRepo;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = UsuarioController.class)
public class UsuarioControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private PasswordEncoder passwordEncoder;

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
    @DisplayName("GET /user deve retornar a view de cadastro")
    void getUser_deveRetornarCadastro() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"));
    }

    @Test
    @DisplayName("POST /user válido deve redirecionar para /home")
    void postUser_valido_deveRedirecionarHome() throws Exception {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pwd");

        mockMvc.perform(post("/user").with(csrf())
                .param("nome", "Usuario Teste")
                .param("login", "user123")
                .param("senha", "Password1@")
                .param("email", "user123@mail.com")
                .param("cidade", "Cidade")
                .param("nascimento", "2000-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(usuarioService, times(1)).save(any());
    }

    @Test
    @DisplayName("POST /user inválido deve retornar a própria view de cadastro")
    void postUser_invalido_deveRetornarCadastro() throws Exception {
        // senha muito curta viola @Size
        mockMvc.perform(post("/user").with(csrf())
                .param("nome", "Usuario Teste")
                .param("login", "user123")
                .param("senha", "123")
                .param("email", "user123@mail.com")
                .param("cidade", "Cidade")
                .param("nascimento", "2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"));
    }
}
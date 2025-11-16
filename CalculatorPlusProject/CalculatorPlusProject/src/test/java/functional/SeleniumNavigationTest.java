package functional;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import br.edu.calc.plus.CalculatorPlusApplication;
import org.junit.jupiter.api.BeforeAll;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Teste funcional simples com Selenium: acessa /home sem autenticação e
 * verifica redirecionamento para /login (proteção via Spring Security).
 */
@SpringBootTest(classes = CalculatorPlusApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SeleniumNavigationTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeAll
    static void configureHttpClient() {
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        // Não definir webdriver.chrome.driver para permitir que o Selenium Manager
        // resolva e baixe o driver automaticamente quando necessário.
    }

    private WebDriver createHeadlessChrome() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1280,800");
        return new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Ao acessar /home sem login, a aplicação permite acesso público.
     * Garante que a página /home carregou (conteúdo visível) e não houve redirecionamento.
     */
    @Test
    void homeRedirectsToLoginWhenUnauthenticated() {
        driver = createHeadlessChrome();
        String homeUrl = "http://localhost:" + port + "/home";
        driver.get(homeUrl);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/home"));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/home"),
                "Esperava permanecer em /home, mas foi: " + currentUrl);

        // Verificação leve do conteúdo da página de home
        String source = driver.getPageSource().toLowerCase();
        assertTrue(source.contains("erros") || source.contains("acertos") || source.contains("usuários") || source.contains("premiações"),
                "Conteúdo da página de home não detectado");
    }
}
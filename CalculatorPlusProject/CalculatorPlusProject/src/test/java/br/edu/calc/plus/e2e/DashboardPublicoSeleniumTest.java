package br.edu.calc.plus.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DashboardPublicoSeleniumTest {

    @LocalServerPort
    private int port;
    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void setUp() throws MalformedURLException {
        // Configura o Chrome remoto via Selenium (container Docker)
        ChromeOptions options = new ChromeOptions();
        // Deixe sem headless para poder visualizar via VNC (porta 7900)
        // options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        // URL do Selenium Grid (container)
        String gridUrl = System.getProperty("selenium.remote.url",
                System.getenv().getOrDefault("SELENIUM_REMOTE_URL", "http://localhost:4444/wd/hub"));

        driver = new RemoteWebDriver(new URL(gridUrl), options);

        // 6. Configura o tempo de espera
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // 7. Define a URL base acessível de dentro do container Docker
        // No Windows/macOS, o hostname especial para alcançar o host é 'host.docker.internal'
        String hostForContainer = System.getProperty("app.host", System.getenv().getOrDefault("APP_HOST", "host.docker.internal"));
        baseUrl = "http://" + hostForContainer + ":" + port + "/";
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("CTA-01 (RF-01/RF-03): Deve exibir o Dashboard Público corretamente (Selenium Docker)")
    void deveExibirDashboardPublico() {
        driver.get(baseUrl);

        // 1. Valida o Título
        String expectedTitle = "Calculator Dashboard by Tabajara";
        String actualTitle = driver.getTitle();
        Assertions.assertEquals(expectedTitle, actualTitle, "O <title> da página está incorreto.");

        // 2. Valida os KPIs
        String kpiUsuariosXPath = "//p[@class='card-category' and text()='Usuários']";
        Assertions.assertTrue(driver.findElement(By.xpath(kpiUsuariosXPath)).isDisplayed(),
                "O KPI 'Usuários' não está visível.");

        String kpiPremiacoesXPath = "//p[@class='card-category' and text()='Premiações']";
        Assertions.assertTrue(driver.findElement(By.xpath(kpiPremiacoesXPath)).isDisplayed(),
                "O KPI 'Premiações' não está visível.");

        // 3. Valida Botões de Login/Cadastro
        Assertions.assertTrue(driver.findElement(By.cssSelector("a[href='/login']")).isDisplayed(),
                "O botão 'Login' (link href='/login') não está visível.");
        Assertions.assertTrue(driver.findElement(By.cssSelector("a[href='/user']")).isDisplayed(),
                "O botão 'Cadastro' (link href='/user') não está visível.");

        // 4. Valida Ausência de Menus Restritos (RF-01)
        int menusCompeticao = driver.findElements(By.cssSelector("a[href='/competicao']")).size();
        Assertions.assertEquals(0, menusCompeticao, "O menu 'Competição' está visível para usuário não logado.");
    }
}
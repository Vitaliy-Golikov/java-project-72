package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.util.DateUtils;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import hexlet.code.repository.UrlRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import io.javalin.testtools.JavalinTest;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;

public class AppTest {
    private Javalin app;

    private static MockWebServer mockServer;
    private static String mockBaseUrl;

    @BeforeAll
    public static void setUpMockServer() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        mockBaseUrl = mockServer.url("/").toString();
        mockBaseUrl = mockBaseUrl.substring(0, mockBaseUrl.length() - 1);
    }

    @AfterAll
    public static void tearDownMockServer() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        app = App.getApp();
        UrlRepository.removeAll();
        UrlCheckRepository.removeAll();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.rootPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Проверить");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Сайты");
        });
    }

    @Test
    public void testUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("https://some-domain.org:8080/example/path");
            UrlRepository.save(url);

            var response = client.get(NamedRoutes.urlPath("1"));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://some-domain.org:8080");
        });
    }


    @Test
    public void testUrlNotFound() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath("1"));
            assertThat(response.code()).isEqualTo(404);
        });
    }


    @Test
    public void testAddNewUrlFollowsRedirectAndShowsFlash() {
        JavalinTest.test(app, (server, client) -> {

            var httpClient = createHttpClient();

            var requestBody = new okhttp3.FormBody.Builder()
                    .add("url", "https://www.example2.com")
                    .build();

            var request = new okhttp3.Request.Builder()
                    .url("http://localhost:" + server.port() + NamedRoutes.urlsPath())
                    .post(requestBody)
                    .build();

            try (var response = httpClient.newCall(request).execute()) {
                String body = response.body().string();
                assertThat(response.code()).isEqualTo(200);
                assertThat(body).contains("Страница успешно добавлена");
                assertThat(body).contains("https://www.example2.com");
            }
        });
    }

    @Test
    public void testAddExistingUrlFollowsRedirectAndShowsFlash() {
        JavalinTest.test(app, (server, client) -> {
            var name = "https://some-domain.org:8080/example/path";
            var expectedName = "https://some-domain.org:8080";
            var url = new Url(expectedName);
            UrlRepository.save(url);

            var httpClient = createHttpClient();

            var requestBody = new okhttp3.FormBody.Builder()
                    .add("url", name)
                    .build();

            var request = new okhttp3.Request.Builder()
                    .url("http://localhost:" + server.port() + NamedRoutes.urlsPath())
                    .post(requestBody)
                    .build();

            try (var response = httpClient.newCall(request).execute()) {
                String body = response.body().string();
                assertThat(response.code()).isEqualTo(200);
                assertThat(body).contains("Страница уже существует");
                assertThat(body).contains(expectedName);
            }
        });
    }


    @Test
    public void testIncorrectUrlAndShowsFlash() {
        JavalinTest.test(app, (server, client) -> {
            var name = "https://some space domain.org";

            var httpClient = createHttpClient();

            var requestBody = new okhttp3.FormBody.Builder()
                    .add("url", name)
                    .build();

            var request = new okhttp3.Request.Builder()
                    .url("http://localhost:" + server.port() + NamedRoutes.urlsPath())
                    .post(requestBody)
                    .build();

            try (var response = httpClient.newCall(request).execute()) {
                String body = response.body().string();
                assertThat(response.code()).isEqualTo(200);
                assertThat(body).contains("Некорректный URL");
            }
        });
    }

    @Test
    public void testCheckUrlShowsOnUrlPage() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            final String expectedTitle = "Test Page Title";
            final String expectedDescription = "Test description content";
            final String expectedH1 = "Test H1 Header";
            final String mainContent = "Main content";

            final String htmlTemplate = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>%s</title>
                    <meta name="description" content="%s">
                </head>
                <body>
                    <h1>%s</h1>
                    <p>%s</p>
                </body>
                </html>
                """;

            final String htmlContent = String.format(
                    htmlTemplate,
                    expectedTitle,
                    expectedDescription,
                    expectedH1,
                    mainContent
            );

            mockServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "text/html")
                    .setBody(htmlContent));

            var httpClient = createHttpClient();

            var url = new Url(mockBaseUrl);
            UrlRepository.save(url);


            List<Url> urls = UrlRepository.getEntities();
            Long urlId = urls.get(0).getId();

            var requestCheckBody = new okhttp3.FormBody.Builder()
                    .build();

            var requestCheck = new okhttp3.Request.Builder()
                    .url("http://localhost:" + server.port() + NamedRoutes.urlPathCheck(urlId))
                    .post(requestCheckBody)
                    .build();


            try (var response = httpClient.newCall(requestCheck).execute()) {
                String urlWithCheckPageBody = response.body().string();
                assertThat(response.code()).isEqualTo(200);
                assertThat(urlWithCheckPageBody).contains(expectedTitle);
                assertThat(urlWithCheckPageBody).contains(expectedH1);
                assertThat(urlWithCheckPageBody).contains(expectedDescription);
                assertThat(urlWithCheckPageBody).contains("Страница успешно проверена");
            }

            List<UrlCheck> checks = UrlCheckRepository.findByUrlId(urlId);
            assertThat(checks)
                    .as("Должна существовать ровно одна проверка для URL")
                    .hasSize(1);

            UrlCheck savedCheck = checks.get(0);
            assertThat(savedCheck.getTitle())
                    .as("Title в БД должен совпадать с ожидаемым")
                    .isEqualTo(expectedTitle);
            assertThat(savedCheck.getH1())
                    .as("H1 в БД должен совпадать с ожидаемым")
                    .isEqualTo(expectedH1);
            assertThat(savedCheck.getDescription())
                    .as("Description в БД должен совпадать с ожидаемым")
                    .isEqualTo(expectedDescription);

        });
    }

    @Test
    public void testLatestCheckShowsOnUrlsPage() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            final String expectedTitle = "Test Page Title";
            final String expectedDescription = "Test description content";
            final String expectedH1 = "Test H1 Header";
            final String mainContent = "Main content";

            final String htmlTemplate = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>%s</title>
                    <meta name="description" content="%s">
                </head>
                <body>
                    <h1>%s</h1>
                    <p>%s</p>
                </body>
                </html>
                """;

            final String htmlContent = String.format(
                    htmlTemplate,
                    expectedTitle,
                    expectedDescription,
                    expectedH1,
                    mainContent
            );

            mockServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "text/html")
                    .setBody(htmlContent));

            mockServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "text/html")
                    .setBody(htmlContent));

            var httpClient = createHttpClient();

            var url = new Url(mockBaseUrl);
            UrlRepository.save(url);

            List<Url> urls = UrlRepository.getEntities();
            Long urlId = urls.get(0).getId();

            var requestCheckBody = new okhttp3.FormBody.Builder()
                    .build();

            var requestCheck = new okhttp3.Request.Builder()
                    .url("http://localhost:" + server.port() + NamedRoutes.urlPathCheck(urlId))
                    .post(requestCheckBody)
                    .build();

            httpClient.newCall(requestCheck).execute();
            Thread.sleep(2000);
            httpClient.newCall(requestCheck).execute();

            var expectedCreatedAt = UrlCheckRepository.findLatestByUrlId(urlId).get().getCreatedAt();

            try (var response = client.get(NamedRoutes.urlsPath())) {
                String urlWithCheckPageBody = response.body().string();
                assertThat(response.code()).isEqualTo(200);
                assertThat(urlWithCheckPageBody).contains(DateUtils.format(expectedCreatedAt));
            }
        });
    }

    private okhttp3.OkHttpClient createHttpClient() {
        return new okhttp3.OkHttpClient.Builder()
                .cookieJar(new okhttp3.JavaNetCookieJar(new java.net.CookieManager()))
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }
}
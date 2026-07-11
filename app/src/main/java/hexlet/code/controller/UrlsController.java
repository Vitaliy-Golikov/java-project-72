package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.urls.UrlListItem;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;

import kong.unirest.core.Unirest;
import kong.unirest.core.HttpResponse;

import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {

    private static final int MAX_LENGTH = 250;
    private static final int TRUNCATE_LENGTH = MAX_LENGTH - 3; // 247

    private static String truncate(String value) {
        if (value == null) {
            return null;
        }
        if (value.length() <= MAX_LENGTH) {
            return value;
        }
        return value.substring(0, TRUNCATE_LENGTH) + "...";
    }

    public static void index(Context ctx) throws SQLException {
        List<UrlListItem> items = UrlRepository.getAllWithLastChecks();
        System.out.println("=== UrlsController.index() ===");
        System.out.println("Items count: " + items.size());
        for (var item : items) {
            System.out.println("Item: " + item.getName() + ", status: " + (item.getLastCheck() != null ? item.getLastCheck().getStatusCode() : "null"));
        }
        var page = new UrlsPage(items);
        consumeFlashToPage(ctx, page);
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        System.out.println("Show URL ID: " + id);

        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        System.out.println("URL found: " + url.getName() + " with ID: " + url.getId());

        var checks = UrlCheckRepository.findByUrlId(id);
        System.out.println("Loaded " + checks.size() + " checks for URL ID: " + id);

        var page = new UrlPage(url, checks);
        consumeFlashToPage(ctx, page);
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var name = ctx.formParam("url");
        System.out.println("Creating URL: " + name);

        if (name == null || name.trim().isEmpty()) {
            ctx.sessionAttribute("flashType", "danger");
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        String normalizedUrl;
        try {
            normalizedUrl = normalizeUrl(name);
            System.out.println("Normalized URL: " + normalizedUrl);
        } catch (MalformedURLException | URISyntaxException e) {
            ctx.sessionAttribute("flashType", "danger");
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        var existingUrl = UrlRepository.findByName(normalizedUrl);
        if (existingUrl.isPresent()) {
            System.out.println("URL already exists: " + existingUrl.get().getId());
            ctx.sessionAttribute("flashType", "success");
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.redirect(NamedRoutes.urlPath(String.valueOf(existingUrl.get().getId())));
            return;
        }

        var url = new Url(normalizedUrl);
        UrlRepository.save(url);
        System.out.println("Saved URL with ID: " + url.getId());

        var savedUrl = UrlRepository.findByName(normalizedUrl).get();

        ctx.sessionAttribute("flashType", "success");
        ctx.sessionAttribute("flash", "Страница успешно добавлена");

        ctx.redirect(NamedRoutes.urlPath(String.valueOf(savedUrl.getId())));
    }

    public static void check(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        System.out.println("Checking URL ID: " + id);

        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        System.out.println("Checking URL: " + url.getName());

        try {
            UrlCheck checkedUrl = checkUrl(url);
            System.out.println("Check result: Status=" + checkedUrl.getStatusCode() +
                    ", Title=" + checkedUrl.getTitle() +
                    ", H1=" + checkedUrl.getH1() +
                    ", Description=" + checkedUrl.getDescription());

            UrlCheckRepository.save(checkedUrl);
            System.out.println("Check saved successfully with ID: " + checkedUrl.getId());

            var savedChecks = UrlCheckRepository.findByUrlId(id);
            System.out.println("After save, found " + savedChecks.size() + " checks for URL ID: " + id);

            ctx.sessionAttribute("flashType", "success");
            ctx.sessionAttribute("flash", "Страница успешно проверена");
        } catch (UnirestException e) {
            System.err.println("UnirestException: " + e.getMessage());
            ctx.sessionAttribute("flashType", "danger");
            ctx.sessionAttribute("flash", "Некорректный адрес");
        } catch (RuntimeException e) {
            System.err.println("RuntimeException: " + e.getMessage());
            ctx.sessionAttribute("flashType", "warning");
            ctx.sessionAttribute("flash", "Страница доступна, но не удалось извлечь метаданные");
        }

        ctx.redirect(NamedRoutes.urlPath(String.valueOf(id)));
    }

    private static UrlCheck checkUrl(Url url) throws UnirestException {
        UrlCheck urlCheck = new UrlCheck(500, url);
        var urlString = url.getName();
        System.out.println("Fetching URL: " + urlString);

        try {
            HttpResponse<String> response = Unirest.get(urlString)
                    .header("User-Agent", "Mozilla/5.0")
                    .asString();

            urlCheck.setStatusCode(response.getStatus());
            System.out.println("Response status: " + response.getStatus());

            String body = response.getBody();
            if (body == null || body.trim().isEmpty()) {
                System.out.println("Body is empty for URL: " + urlString);
                return urlCheck;
            }

            Document doc = Jsoup.parse(body, urlString);

            String title = doc.title();
            String trimmedTitle = title.trim();
            String truncatedTitle = truncate(trimmedTitle);
            System.out.println("Raw title length: " + trimmedTitle.length());
            System.out.println("Truncated title: " + truncatedTitle);
            System.out.println("Truncated title length: " + (truncatedTitle != null ? truncatedTitle.length() : 0));
            System.out.println("Truncated title ends with ...: " + (truncatedTitle != null && truncatedTitle.endsWith("...")));
            urlCheck.setTitle(!trimmedTitle.isEmpty() ? truncatedTitle : null);

            Element h1Element = doc.selectFirst("h1");
            if (h1Element != null) {
                String h1Text = h1Element.text();
                String truncatedH1 = truncate(h1Text);
                System.out.println("Raw h1 length: " + h1Text.length());
                System.out.println("Truncated h1: " + truncatedH1);
                System.out.println("Truncated h1 length: " + (truncatedH1 != null ? truncatedH1.length() : 0));
                System.out.println("Truncated h1 ends with ...: " + (truncatedH1 != null && truncatedH1.endsWith("...")));
                urlCheck.setH1(truncatedH1);
            } else {
                System.out.println("H1 element not found");
                urlCheck.setH1(null);
            }

            Element descriptionMeta = doc.selectFirst("meta[name=description], meta[property=og:description]");
            if (descriptionMeta != null) {
                String desc = descriptionMeta.attr("content");
                String truncatedDesc = truncate(desc);
                System.out.println("Raw description length: " + desc.length());
                System.out.println("Truncated description: " + truncatedDesc);
                System.out.println("Truncated description length: " + (truncatedDesc != null ? truncatedDesc.length() : 0));
                System.out.println("Truncated description ends with ...: " + (truncatedDesc != null && truncatedDesc.endsWith("...")));
                urlCheck.setDescription(truncatedDesc);
            } else {
                System.out.println("Description meta not found");
                urlCheck.setDescription(null);
            }

        } catch (Exception e) {
            System.err.println("Error parsing URL: " + e.getMessage());
            e.printStackTrace();
        }

        return urlCheck;
    }

    private static void consumeFlashToPage(Context ctx, BasePage page) {
        var flash = ctx.consumeSessionAttribute("flash");
        var flashType = ctx.consumeSessionAttribute("flashType");

        if (flash != null) {
            page.setFlash(flash.toString());
        }
        if (flashType != null) {
            page.setFlashType(flashType.toString());
        }
    }

    private static String normalizeUrl(String input) throws MalformedURLException, URISyntaxException {
        URI uri = new URI(input);
        URL url = uri.toURL();

        String protocol = url.getProtocol();
        String host = url.getHost();
        int port = url.getPort();

        StringBuilder normalized = new StringBuilder();
        normalized.append(protocol).append("://").append(host);

        if (port != -1) {
            normalized.append(":").append(port);
        }

        System.out.println("Normalized from '" + input + "' to '" + normalized.toString() + "'");
        return normalized.toString();
    }
}
package hexlet.code.dto.urls;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public final class UrlListItem {
    private final Long id;
    private final String name;
    private final LocalDateTime createdAt;
    private final UrlCheck lastCheck;

    private UrlListItem(Long id, String name, LocalDateTime createdAt, UrlCheck lastCheck) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.lastCheck = lastCheck;
    }

    public static UrlListItem fromUrl(Url url, UrlCheck lastCheck) {
        System.out.println("=== UrlListItem.fromUrl() ===");
        System.out.println("URL ID: " + url.getId());
        System.out.println("URL Name: " + url.getName());
        System.out.println("LastCheck status: " + (lastCheck != null ? lastCheck.getStatusCode() : "null"));

        return new UrlListItem(
                url.getId(),
                url.getName(),
                url.getCreatedAt(),
                lastCheck
        );
    }
}
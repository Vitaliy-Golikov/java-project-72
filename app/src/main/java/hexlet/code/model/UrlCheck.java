package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UrlCheck {

    private Long id;
    private int statusCode;
    private String h1;
    private String title;
    private String description;
    private Url url;
    private LocalDateTime createdAt;

    public UrlCheck(int statusCode, Url url) {
        this.statusCode = statusCode;
        this.url = url;
    }

}
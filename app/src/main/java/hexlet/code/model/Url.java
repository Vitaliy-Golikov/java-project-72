package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "urlChecks")
public final class Url {

    private Long id;
    private String name;
    private LocalDateTime createdAt = LocalDateTime.now();

    @Getter
    private List<UrlCheck> urlChecks = new ArrayList<>();

    public Url(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public Url(String name, LocalDateTime createdAt) {
        this.name = name;
        this.createdAt = createdAt;
        this.urlChecks = new ArrayList<>();
    }

    public void addUrlCheck(UrlCheck check) {
        urlChecks.add(check);
        check.setUrl(this);
    }


}
package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;

import java.util.List;

@Getter
public class UrlPage extends BasePage {
    private Url url;
    private List<UrlCheck> checks;

    public UrlPage(Url url) {
        this.url = url;
    }

    public UrlPage(Url url, List<UrlCheck> checks) {
        this.url = url;
        this.checks = checks;
    }
}

package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import lombok.Getter;

import java.util.List;

@Getter
public class UrlsPage extends BasePage {
    private final List<UrlListItem> items;

    public UrlsPage(List<UrlListItem> items) {
        this.items = items;
    }

}
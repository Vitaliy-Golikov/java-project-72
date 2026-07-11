@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.DateUtils
@import hexlet.code.util.NamedRoutes

@param UrlsPage page

@template.layout.page(
title = "Сайты",
page = page,
content = @`
    <h1>Сайты</h1>
    <table class="table table-bordered table-hover mt-2">
        <thead>
        <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Последняя проверка</th>
            <th>Код ответа</th>
        </tr>
        </thead>
        <tbody>
        @if(!page.getItems().isEmpty())
            @for(var urlListItem : page.getItems())
                <tr>
                    <td>${urlListItem.getId()}</td>
                    <td><a href="${NamedRoutes.urlPath(urlListItem.getId())}">${urlListItem.getName()}</a></td>
                    @if(urlListItem.getLastCheck() != null)
                        <td>${DateUtils.format(urlListItem.getLastCheck().getCreatedAt())}</td>
                        <td>${urlListItem.getLastCheck().getStatusCode()}</td>
                    @else
                        <td></td>
                        <td></td>
                    @endif
                </tr>
            @endfor
        @endif
        </tbody>
    </table>
`
)
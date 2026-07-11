@import hexlet.code.dto.urls.UrlPage
@import hexlet.code.util.DateUtils
@import hexlet.code.util.NamedRoutes

@param UrlPage page

@template.layout.page(
title = "Сайт",
page = page,
content = @`
    <div class="container-lg mt-5">
        <h1>Сайт: ${page.getUrl().getName()}</h1>

        <table class="table table-bordered table-hover mt-3">
            <tbody>
            <tr>
                <td>ID</td>
                <td>${page.getUrl().getId()}</td>
            </tr>
            <tr>
                <td>Имя</td>
                <td>${page.getUrl().getName()}</td>
            </tr>
            <tr>
                <td>Дата создания</td>
                <td>${DateUtils.format(page.getUrl().getCreatedAt())}</td>
            </tr>
            </tbody>
        </table>

        <h2 class="mt-5">Проверки</h2>
        <form method="post" action="${NamedRoutes.urlPathCheck(page.getUrl().getId())}">
            <button type="submit" class="btn btn-primary">Запустить проверку</button>
        </form>

        <table class="table table-bordered table-hover mt-3" data-test="checks">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th class="col-1">Код ответа</th>
                <th>h1</th>               <!-- ← Поменяли местами -->
                <th>title</th>            <!-- ← Теперь title после h1 -->
                <th>description</th>
                <th class="col-2">Дата проверки</th>
            </tr>
            </thead>
            <tbody>
            @if(page.getChecks() != null && !page.getChecks().isEmpty())
                @for(var urlCheck : page.getChecks())
                    <tr>
                        <td class="col-1">${urlCheck.getId()}</td>
                        <td>${urlCheck.getStatusCode()}</td>
                        <td>${urlCheck.getH1() != null ? urlCheck.getH1() : ""}</td>
                        <td>${urlCheck.getTitle() != null ? urlCheck.getTitle() : ""}</td>
                        <td>${urlCheck.getDescription() != null ? urlCheck.getDescription() : ""}</td>
                        <td class="col-2">${DateUtils.format(urlCheck.getCreatedAt())}</td>
                    </tr>
                @endfor
            @else
                <tr>
                    <td colspan="6" class="text-center">Нет проверок для этого сайта</td>
                </tr>
            @endif
            </tbody>
        </table>
    </div>
`
)
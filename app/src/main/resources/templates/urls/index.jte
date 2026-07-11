@import java.util.Map
@import hexlet.code.model.Url
@param List<Url> urls
@param Map<String, Object> flash

@layout(title = "Сайты", flash = flash)
<h1>Сайты</h1>

<table class="table table-bordered table-hover mt-2">
    <thead>
    <tr>
        <th>ID</th>
        <th>Имя</th>
        <th>Дата добавления</th>
    </tr>
    </thead>
    <tbody>
    @for (Url url : urls)
    <tr>
        <td>${url.getId()}</td>
        <td>
            <a href="/urls/${url.getId()}">${url.getName()}</a>
        </td>
        <td>${url.getCreatedAt()}</td>
    </tr>
    @end
    </tbody>
</table>
@end
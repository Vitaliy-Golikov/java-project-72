@import java.util.Map
@import hexlet.code.model.Url
@param Url url
@param Map<String, Object> flash

@layout(title = "Сайт", flash = flash)
<h1>Сайт: ${url.getName()}</h1>

<div class="table-responsive">
    <table class="table table-bordered table-hover">
        <tbody>
        <tr>
            <th>ID</th>
            <td>${url.getId()}</td>
        </tr>
        <tr>
            <th>Имя</th>
            <td>${url.getName()}</td>
        </tr>
        <tr>
            <th>Дата добавления</th>
            <td>${url.getCreatedAt()}</td>
        </tr>
        </tbody>
    </table>
</div>
@end
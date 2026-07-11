@import java.util.Map
@param String title
@param Map<String, Object> flash

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-md navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="/">Анализатор страниц</a>
        <a class="navbar-link text-white" href="/urls">Сайты</a>
    </div>
</nav>

<main class="container">
    @if (flash != null && flash.containsKey("flash"))
        <div class="alert alert-${flash.getOrDefault("flashType", "info")}" role="alert">
            ${flash.get("flash")}
        </div>
    @endif

    ${@content}
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
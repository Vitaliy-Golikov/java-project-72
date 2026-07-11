package gg.jte.generated.ondemand;
import java.util.Map;
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,1,1,25,25,25,25,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Map<String, Object> flash) {
		jteOutput.writeContent("\r\n@layout(title = \"Анализатор страниц\", flash = flash)\r\n<div class=\"row\">\r\n    <div class=\"col-12 col-md-10 col-lg-8 mx-auto border rounded-3 bg-light p-5\">\r\n        <h1 class=\"display-3\">Анализатор страниц</h1>\r\n        <p class=\"lead\">Бесплатно проверяйте сайты на SEO пригодность</p>\r\n        <form action=\"/urls\" method=\"post\" class=\"row\">\r\n            <div class=\"col-8\">\r\n                <label for=\"url-name\" class=\"visually-hidden\">Url для проверки</label>\r\n                <input\r\n                        id=\"url-name\"\r\n                        type=\"text\"\r\n                        name=\"url\"\r\n                        class=\"form-control form-control-lg\"\r\n                        placeholder=\"https://www.example.com\"\r\n                >\r\n            </div>\r\n            <div class=\"col-2\">\r\n                <input type=\"submit\" class=\"btn btn-primary btn-lg ms-3 px-5 text-uppercase mx-3\" value=\"Проверить\">\r\n            </div>\r\n        </form>\r\n    </div>\r\n</div>\r\n@end");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Map<String, Object> flash = (Map<String, Object>)params.get("flash");
		render(jteOutput, jteHtmlInterceptor, flash);
	}
}

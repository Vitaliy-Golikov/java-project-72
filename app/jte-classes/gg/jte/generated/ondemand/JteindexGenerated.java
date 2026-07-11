package gg.jte.generated.ondemand;
import hexlet.code.dto.RootPage;
import hexlet.code.util.NamedRoutes;
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,3,3,3,5,5,9,9,14,14,14,14,14,14,14,14,14,32,32,32,32,32,3,3,3,3};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, RootPage page) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.layout.JtepageGenerated.render(jteOutput, jteHtmlInterceptor, "Анализатор страниц", page, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n    <div class=\"row\">\r\n        <div class=\"col-12 col-md-10 col-lg-8 mx-auto border rounded-3 bg-light p-5\">\r\n            <h1 class=\"display-3\">Анализатор страниц</h1>\r\n            <p class=\"lead\">Бесплатно проверяйте сайты на SEO пригодность</p>\r\n            <form");
				var __jte_html_attribute_0 = NamedRoutes.urlsPath();
				if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
					jteOutput.writeContent(" action=\"");
					jteOutput.setContext("form", "action");
					jteOutput.writeUserContent(__jte_html_attribute_0);
					jteOutput.setContext("form", null);
					jteOutput.writeContent("\"");
				}
				jteOutput.writeContent(" method=\"post\" class=\"row\">\r\n                <div class=\"col-8\">\r\n                    <label for=\"url-name\" class=\"visually-hidden\">Url для проверки</label>\r\n                    <input\r\n                            id=\"url-name\"\r\n                            type=\"text\"\r\n                            name=\"url\"\r\n                            class=\"form-control form-control-lg\"\r\n                            placeholder=\"https://www.example.com\"\r\n                            required\r\n                    >\r\n                </div>\r\n                <div class=\"col-2\">\r\n                    <input type=\"submit\" class=\"btn btn-primary btn-lg ms-3 px-5 text-uppercase mx-3\" value=\"Проверить\">\r\n                </div>\r\n            </form>\r\n        </div>\r\n    </div>\r\n");
			}
		});
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		RootPage page = (RootPage)params.get("page");
		render(jteOutput, jteHtmlInterceptor, page);
	}
}

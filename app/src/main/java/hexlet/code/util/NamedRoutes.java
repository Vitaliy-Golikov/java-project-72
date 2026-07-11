package hexlet.code.util;

public class NamedRoutes {

    public static String urlsPath() {
        return "/urls";
    }

    public static String urlPath(String id) {
        return "/urls/" + id;
    }

    public static String urlPath(Long id) {
        return urlPath(String.valueOf(id));
    }

    public static String rootPath() {
        return "/";
    }

    public static String urlPathCheck(String id) {
        return "/urls/" + id + "/checks";
    }

    public static String urlPathCheck(Long id) {
        return urlPathCheck(String.valueOf(id));
    }

}
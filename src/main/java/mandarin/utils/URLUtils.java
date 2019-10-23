package mandarin.utils;

import org.springframework.http.HttpRequest;
import org.springframework.web.util.UriComponentsBuilder;

public class URLUtils {
    public static String replaceQuery(String url, String key, String value) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        builder.replaceQueryParam(key, value);
        return builder.toUriString();
    }

    public static String replaceQuery(HttpRequest request, String key, String value) {
        return replaceQuery(request.getURI().toString(), key, value);
    }
}

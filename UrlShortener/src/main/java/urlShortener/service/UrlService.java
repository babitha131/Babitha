package urlShortener.service;

public interface UrlService {
    String findUrlById(String id);

    void storeUrl(String id, String url);
}

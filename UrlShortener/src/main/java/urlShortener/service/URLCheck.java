package urlShortener.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class URLCheck implements UrlService{
    private Map<String, String> urlnew = new ConcurrentHashMap<>();

    @Override
    public String findUrlById(String id) {
        return urlnew.get(id);
    }

    @Override
    public void storeUrl(String id, String url) {
        urlnew.put(id, url);
    }
}

package urlShortener;

import com.google.common.hash.Hashing;

import urlShortener.InputUrl;
import urlShortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Controller
public class UrlController {
    @Autowired
    private UrlService urlservice;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String showForm(InputUrl request) {
        return "shortener";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void redirectToUrl(@PathVariable String id, HttpServletResponse resp) throws Exception {
        final String url = urlservice.findUrlById(id);
        if (url != null) {
            resp.addHeader("Location", url);
            resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @RequestMapping(value="/", method = RequestMethod.POST)
    public ModelAndView shortenUrl(HttpServletRequest httpRequest,
                                   @Valid InputUrl request,
                                   BindingResult bindingResult) {
        String url = request.getUrl();
        if (!isUrlValid(url)) {
            bindingResult.addError(new ObjectError("url", "Invalid url format: " + url));
        }

        ModelAndView modelAndView = new ModelAndView("shortener");
        if (!bindingResult.hasErrors()) {
            final String id = Hashing.murmur3_32()
                .hashString(url, StandardCharsets.UTF_8).toString();
            urlservice.storeUrl(id, url);
            String requestUrl = httpRequest.getRequestURL().toString();
            String prefix = requestUrl.substring(0, requestUrl.indexOf(httpRequest.getRequestURI(),
                "http://".length()));

            modelAndView.addObject("shortenedUrl", prefix + "/" + id);
        }
        return modelAndView;
    }

    private boolean isUrlValid(String url) {
        boolean valid = true;
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            valid = false;
        }
        return valid;
    }
}

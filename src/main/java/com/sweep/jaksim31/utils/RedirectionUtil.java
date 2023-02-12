package com.sweep.jaksim31.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@Data
@NoArgsConstructor
public class RedirectionUtil {

    private String homeUrl;

    public RedirectionUtil(@Value("${home.url}") String homeUrl){
        this.homeUrl = homeUrl;
    }

    public HttpHeaders getLocationHeader() throws URISyntaxException {
        URI redirectUri = new URI(homeUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        return httpHeaders;
    }

}

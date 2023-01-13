package com.sweep.jaksim31.controller.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@FeignClient(name = "downloadImage", url= "ANY-VALUE")
public interface DownloadImageFeign {
    @GetMapping(value = "{url}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    ResponseEntity<byte[]> getImage(URI uri);
}



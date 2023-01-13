package com.sweep.jaksim31.controller.feign;


import com.sweep.jaksim31.controller.feign.config.UploadImageFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "uploadImage", url= "${kakao.upload-storage.url}", configuration = UploadImageFeignConfig.class)
public interface UploadImageFeign {
    @RequestMapping(method = RequestMethod.PUT,  value = "{path}", headers = {""})
    ResponseEntity<Void> uploadFile(@PathVariable("path") String path, @RequestBody byte[] imgByte);

    @RequestMapping(method = RequestMethod.GET, value = "{file}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    ResponseEntity<byte[]> getFile(@PathVariable("file") String file);
}

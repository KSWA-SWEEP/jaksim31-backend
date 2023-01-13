package com.sweep.jaksim31.controller.feign;


import com.sweep.jaksim31.controller.feign.config.MakeObjectDirectoryFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "makeDirectory", url= "${kakao.upload-storage.url}", configuration = MakeObjectDirectoryFeignConfig.class)
public interface MakeObjectDirectoryFeign {
    @RequestMapping(method = RequestMethod.PUT,  value = "{path}", headers = {""})
    ResponseEntity<Void> makeDir(@PathVariable("path") String path);
}

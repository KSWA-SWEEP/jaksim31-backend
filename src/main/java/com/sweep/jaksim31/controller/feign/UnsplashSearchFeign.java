package com.sweep.jaksim31.controller.feign;


import com.sweep.jaksim31.controller.feign.config.UnsplashSearchFeignConfig;
import org.json.simple.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

@FeignClient(name = "unsplash", url = "any-value", configuration = UnsplashSearchFeignConfig.class)
public interface UnsplashSearchFeign {

    @GetMapping("/")
    ResponseEntity<JSONObject> getImageUrl(URI uri);

}

//{"id":"9dRjj5_XeMg","created_at":"2019-05-13T11:22:25Z","updated_at":"2023-01-05T20:07:27Z","promoted_at":null,"width":3766,"height":5649,"color":"#d9d9c0","blur_hash":"LZGuE+$jDjkp}@a0RQtks:nPIpW:","description":null,"alt_description":"man wearing white shirt holding Sprite bottle","urls":{"raw":"https://images.unsplash.com/photo-1557746534-7e6ca4397ff5?ixid=MnwzOTYyMzZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE2NzI5ODM3MTk\u0026ixlib=rb-4.0.3","full":"https://images.unsplash.com/photo-1557746534-7e6ca4397ff5?crop=entropy\u0026cs=tinysrgb\u0026fm=jpg\u0026ixid=MnwzOTYyMzZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE2NzI5ODM3MTk\u0026ixlib=rb-4.0.3\u0026q=80","regular":"https://images.unsplash.com/photo-1557746534-7e6ca4397ff5?crop=entropy\u0026cs=tinysrgb\u0026fit=max\u0026fm=jpg\u0026ixid=MnwzOTYyMzZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE2NzI5ODM3MTk\u0026ixlib=rb-4.0.3\u0026q=80\u0026w=1080","small":"https://images.unsplash.com/photo-1557746534-7e6ca4397ff5?crop=entropy\u0026cs=tinysrgb\u0026fit=max\u0026fm=jpg\u0026ixid=MnwzOTYyMzZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE2NzI5ODM3MTk\u0026ixlib=rb-4.0.3\u0026q=80\u0026w=400","thumb":"https://images.unsplash.com/photo-1557746534-7e6ca4397ff5?crop=entropy\u0026cs=tinysrgb\u0026fit=max\u0026fm=jpg\u0026ixid=MnwzOTYyMzZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE2NzI5ODM3MTk\u0026ixlib=rb-4.0.3\u0026q=80\u0026w=200","small_s3":"https://s3.us-west-2.amazonaws.com/images.unsplash.com/small/photo-1557746534-7e6ca4397ff5"},"links":{"self":"https://api.unsplash.com/photos/9dRjj5_XeMg","html":"https://unsplash.com/photos/9dRjj5_XeMg","download":"https://unsplash.com/photos/9dRjj5_XeMg/download?ixid=MnwzOTYyMzZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE2NzI5ODM3MTk","download_location":"https://api.unsplash.com/photos/9dRjj5_XeMg/download?ixid=MnwzOTYyMzZ8MHwxfHJhbmRvbXx8fHx8fHx8fDE2NzI5ODM3MTk"},"likes":45,"liked_by_user":false,"current_user_collections":[],"sponsorship":null,"topic_submissions":{},"user":{"id":"f4ucoUmILWM","updated_at":"2023-01-05T17:48:39Z","username":"gift_habeshaw","name":"Gift Habeshaw","first_name":"Gift","last_name":"Habeshaw","twitter_username":"GiftBHabeshaw","portfolio_url":null,"bio":"Am :- photographer \r\n         graphics design\r\n         Editor \r\n         cinematographer \r\nContact me on +251922453732\r\n@ Addis abeba Ethiopia ","location":"Addis Abeba, Ethiopia","links":{"self":"https://api.unsplash.com/users/gift_habeshaw","html":"https://unsplash.com/@gift_habeshaw","photos":"https://api.unsplash.com/users/gift_habeshaw/photos","likes":"https://api.unsplash.com/users/gift_habeshaw/likes","portfolio":"https://api.unsplash.com/users/gift_habeshaw/portfolio","following":"https://api.unsplash.com/users/gift_habeshaw/following","followers":"https://api.unsplash.com/users/gift_habeshaw/followers"},"profile_image":{"small":"https://images.unsplash.com/profile-1565428858497-02236d6f662e?ixlib=rb-4.0.3\u0026crop=faces\u0026fit=crop\u0026w=32\u0026h=32","medium":"https://images.unsplash.com/profile-1565428858497-02236d6f662e?ixlib=rb-4.0.3\u0026crop=faces\u0026fit=crop\u0026w=64\u0026h=64","large":"https://images.unsplash.com/profile-1565428858497-02236d6f662e?ixlib=rb-4.0.3\u0026crop=faces\u0026fit=crop\u0026w=128\u0026h=128"},"instagram_username":"gift_habeshaw","total_collections":6,"total_likes":91,"total_photos":763,"accepted_tos":true,"for_hire":true,"social":{"instagram_username":"gift_habeshaw","portfolio_url":null,"twitter_username":"GiftBHabeshaw","paypal_email":null}},"exif":{"make":"Canon","model":"Canon EOS 5D Mark III","name":"Canon, EOS 5D Mark III","exposure_time":"1/2000","aperture":"1.8","focal_length":"50.0","iso":125},"location":{"name":"Ethiopia , Addis abeba, Ethiopia","city":"Addis abeba","country":"Ethiopia","position":{"latitude":null,"longitude":null}},"meta":{"index":true},"public_domain":false,"tags":[{"type":"search","title":"addis abeba"},{"type":"search","title":"ethiopia"},{"type":"search","title":"sprite"},{"type":"search","title":"fashon"},{"type":"search","title":"promotion"},{"type":"search","title":"coca"},{"type":"search","title":"coca-cola"},{"type":"search","title":"enjoy"},{"type":"search","title":"portriat"},{"type":"search","title":"soda"},{"type":"search","title":"beverage"},{"type":"search","title":"drink"},{"type":"search","title":"human"},{"type":"landing_page","title":"person","source":{"ancestry":{"type":{"slug":"images","pretty_slug":"Images"},"category":{"slug":"people","pretty_slug":"People"}},"title":"People images \u0026 pictures","subtitle":"Download free people images","description":"Human faces speak to us in a way that language cannot. Everyone recognize a smile, a frown, tears. Unsplash has the finest selection of people images on the web: high-def and curated for quality. Family, friends, men, women, Unsplash has photos for all.","meta_title":"People Pictures [HQ] | Download Free Images on Unsplash","meta_description":"Choose from hundreds of free people pictures. Download HD people photos for free on Unsplash.","cover_photo":{"id":"PmNjS6b3XP4","created_at":"2017-04-20T22:04:07Z","updated_at":"2022-12-01T07:02:54Z","promoted_at":"2017-04-21T16:00:49Z","width":4630,"height":3087,"color":"#a6d9d9","blur_hash":"LjI=x%:QUbv#NHWVa}kCt7jFjZfQ","description":"Summer in France with baby","alt_description":"woman carrying baby while walking","urls":{"raw":"https://images.unsplash.com/photo-1492725764893-90b379c2b6e7?ixlib=rb-4.0.3","full":"https://images.unsplash.com/photo-1492725764893-90b379c2b6e7?ixlib=rb-4.0.3\u0026q=80\u0026cs=tinysrgb\u0026fm=jpg\u0026crop=entropy","regular":"https://images.unsplash.com/photo-1492725764893-90b379c2b6e7?ixlib=rb-4.0.3\u0026w=1080\u0026fit=max\u0026q=80\u0026fm=jpg\u0026crop=entropy\u0026cs=tinysrgb","small":"https://images.unsplash.com/photo-1492725764893-90b379c2b6e7?ixlib=rb-4.0.3\u0026w=400\u0026fit=max\u0026q=80\u0026fm=jpg\u0026crop=entropy\u0026cs=tinysrgb","thumb":"https://images.unsplash.com/photo-1492725764893-90b379c2b6e7?ixlib=rb-4.0.3\u0026w=200\u0026fit=max\u0026q=80\u0026fm=jpg\u0026crop=entropy\u0026cs=tinysrgb","small_s3":"https://s3.us-west-2.amazonaws.com/images.unsplash.com/small/photo-1492725764893-90b379c2b6e7"},"links":{"self":"https://api.unsplash.com/photos/PmNjS6b3XP4","html":"https://unsplash.com/photos/PmNjS6b3XP4","download":"https://unsplash.com/photos/PmNjS6b3XP4/download","download_location":"https://api.unsplash.com/photos/PmNjS6b3XP4/download"},"likes":2594,"liked_by_user":false,"current_user_collections":[],"sponsorship":null,"topic_submissions":{"current-events":{"status":"approved","approved_on":"2021-03-01T12:52:57Z"}},"premium":false,"user":{"id":"7S_pCRiCiQo","updated_at":"2022-11-30T22:34:06Z","username":"thedakotacorbin","name":"Dakota Corbin","first_name":"Dakota","last_name":"Corbin","twitter_username":"thedakotacorbin","portfolio_url":null,"bio":"Husband | Father | Creator","location":"Utah, United States","links":{"self":"https://api.unsplash.com/users/thedakotacorbin","html":"https://unsplash.com/@thedakotacorbin","photos":"https://api.unsplash.com/users/thedakotacorbin/photos","likes":"https://api.unsplash.com/users/thedakotacorbin/likes","portfolio":"https://api.unsplash.com/users/thedakotacorbin/portfolio","following":"https://api.unsplash.com/users/thedakotacorbin/following","followers":"https://api.unsplash.com/users/thedakotacorbin/followers"},"profile_image":{"small":"https://images.unsplash.com/profile-1623795199834-f8109281554dimage?ixlib=rb-4.0.3\u0026crop=faces\u0026fit=crop\u0026w=32\u0026h=32","medium":"https://images.unsplash.com/profile-1623795199834-f8109281554dimage?ixlib=rb-4.0.3\u0026crop=faces\u0026fit=crop\u0026w=64\u0026h=64","large":"https://images.unsplash.com/profile-1623795199834-f8109281554dimage?ixlib=rb-4.0.3\u0026crop=faces\u0026fit=crop\u0026w=128\u0026h=128"},"instagram_username":"thedakotacorbin","total_collections":0,"total_likes":1,"total_photos":44,"accepted_tos":true,"for_hire":true,"social":{"instagram_username":"thedakotacorbin","portfolio_url":null,"twitter_username":"thedakotacorbin","paypal_email":null}}}}},{"type":"search","title":"bottle"},{"type":"search","title":"beer"},{"type":"search","title":"alcohol"},{"type":"search","title":"finger"}],"tags_preview":[{"type":"search","title":"addis abeba"},{"type":"search","title":"ethiopia"},{"type":"search","title":"sprite"}],"views":7216837,"downloads":5659,"topics":[]}
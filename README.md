# jaksim31-backend
```json
📦src
 ┣ 📂main
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂sweep
 ┃ ┃ ┃ ┃ ┗ 📂jaksim31
 ┃ ┃ ┃ ┃ ┃ ┣ 📂aop
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TimeTraceAop.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂auth
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CustomLoginIdPasswordAuthProvider.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CustomLoginIdPasswordAuthToken.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CustomUserDetailsService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtAccessDeniedHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtAuthenticationEntryPoint.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtFilter.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TokenProvider.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UsernamePasswordAuthenticationToken.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AppConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SecurityConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SwaggerConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜WebConfig.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂feign
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ApiTokenRefreshFeignConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜EmotionAnalysisFeignConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ExtractKeywordFeignConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜KakaoApiTokenRefreshFeignConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MakeObjectDirectoryFeignConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TranslationFeignConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UnsplashSearchFeignConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UploadImageFeignConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ApiTokenRefreshFeign.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CheckObjectDirectoryFeign.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DownloadImageFeign.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜EmotionAnalysisFeign.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ExtractKeywordFeign.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜KakaoApiTokenRefreshFeign.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MakeObjectDirectoryFeign.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TranslationFeign.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UnsplashSearchFeign.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UploadImageFeign.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DiaryApiController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜MembersApiController.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂domain
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂auth
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Authority.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthorityRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜MemberAuth.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂diary
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Diary.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜DiaryRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂members
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MemberRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Members.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂token
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RefreshToken.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RefreshTokenRepository.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂diary
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DiaryAnalysisRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DiaryAnalysisResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DiaryInfoResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DiarySaveRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜DiaryThumbnailRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂login
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜LoginReqDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂member
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MemberCheckLoginIdRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MemberCheckPasswordRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MemberInfoResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MemberRemoveRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MemberSaveRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MemberSaveResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜MemberUpdateRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂tokakao
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜EmotionAnalysisRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜EmotionAnalysisResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ExtractedKeywordResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TranslationRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TranslationResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂token
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TokenRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜TokenResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ApiResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ApiResponseHeader.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂handler
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ApiExceptionHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ErrorResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂type
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthorityExceptionType.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DiaryExceptionType.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtExceptionType.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MemberExceptionType.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ObjectStorageExceptionType.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜BadRequestException.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜BaseExceptionType.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜BizException.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂impl
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DiaryServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜MemberServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜DiaryService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜MemberService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂utils
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CookieUtil.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜EmotionType.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜HeaderUtil.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜SecurityUtil.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜Jaksim31Application.java
 ┃ ┗ 📂resources
 ┃ ┃ ┣ 📜application.yml
 ┃ ┃ ┗ 📜logback-spring.xml
 ┗ 📂test
 ┃ ┗ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂sweep
 ┃ ┃ ┃ ┃ ┗ 📂jaksim31
 ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthServiceImplTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜FormduoApplicationTests.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜IntegrationTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜MemberServiceImplTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜QboxServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📜SurveyRespServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜SurveyServiceTest.java
```

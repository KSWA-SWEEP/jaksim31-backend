**main :** ![main branch](https://github.com/kswa-sweep/jaksim31-backend/actions/workflows/backend.yml/badge.svg?branch=main) <br>
**develop :** ![develop branch](https://github.com/kswa-sweep/jaksim31-backend/actions/workflows/backend.yml/badge.svg?branch=develop)

### 📖 프로젝트 소개
▶️ [Jaksim31 Project Documentation(Notion)](https://pond-front-241.notion.site/Project-Documentation-2992e631827f4804a07f605dac2fa527)

### 🌱 개요

작심삼일 다이어리 서비스 api 서버 개발

- **개발 기간 : 22.12.8 ~ 23.02.12**
- **Version**
    - Java 11
    - Spring boot 2.5.3
    - Mongodb 4.2
- **주요 기능**
    - Spring security, JWT를 이용한 회원 인증 관리
    - 회원, 일기, 토큰 데이터 관리
    - Redis를 이용한 Remote 캐시 적용
    - Openfeign을 이용한 외부 API 호출
    - OpenAPI를 이용한 api 문서화 및 테스트
    - Exception Handler를 이용한 통합 및 개별 예외처리
    - Spring-boot-validation을 이용한 데이터 검증처리
    - JUnit을 이용한 단위 테스트
    - Spring-boot-test-starter를 이용한 통합 테스트

### 🌱 Architecture

![image](https://user-images.githubusercontent.com/64013256/222890285-30b62148-2934-4e9e-8dc2-c63b4ffa47a7.png)

### 🌱 License

![image](https://user-images.githubusercontent.com/64013256/222890277-a81b697c-e336-4c48-9e8e-b6402f0dda37.png)

---

### 🌱Dependency

**[development]**

- Spring-boot
    - spring-boot-starter-web
    - spring-boot-starter-validation:3.0.1
    - spring-boot-starter-security
    - spring-boot-devtools
    - spring-boot-maven-plugin
    - spring-boot-starter-actuator
    - spring-boot-starter-aop
- Spring-security
    - spring-security-test
- Spring-cloud
    - spring-cloud-openfeign:3.0.3
- Springdoc
    - springdoc-openapi:1.6.14
- lombok
- Serialization
    - jackson-databind
    - jackson-modules-java8
    - gson:2.10
    - json-simple:1.1.1
- JWT
    - jjwt-api:0.11.5
    - jjwt-impl:0.11.5
    - jjwt-jackson:0.11.5
- datasource
    - spring-boot-starter-data-elasticsearch
    - spring-boot-starter-data-mongodb
    - spring-boot-starter-data-redis
    - de.flapdoodle.embed.mongo
    - embedded-redis:0.7.2

**[test]**

- Spring-boot-starter-test
- sonar-maven-plugin:3.4.0
- jacoco-maven-plugin:0.8.5
- junit
- mockito-inline

---

### 🌱Directory
```markdown
📦jaksim31-backend-main
 ┣ 📂.github // 깃허브 액션 워크플로우
 ┃ ┗ 📂workflows
 ┣ 📂.mvn
 ┃ ┗ 📂wrapper
 ┣ 📂jaksim31-properties
 ┣ 📂scripts
 ┣ 📂src
 ┃ ┣ 📂main
 ┃ ┃ ┗ 📂java
 ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┗ 📂sweep
 ┃ ┃ ┃ ┃ ┃ ┗ 📂jaksim31
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂adapter
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂cache // 캐시 Adapter 및 Serializer 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂aop // aop 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂auth // jwt 인증 관련 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config // 설정 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂cache // 캐시 설정 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller // 컨트롤러 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂feign // 외부 api요청을 위한 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂config // api 요청 관련 설정 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂domain // 엔티티 & 레포지토리 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂auth // 인증 관련
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂diary // 다이어리 관련
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂members // 회원정보 관련
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂token // 토큰 관련
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto // DTO 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂diary // 다이어리 관련
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂extractkeyword // 키워드 추출 관련
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂validator // 다이어리 데이터 Validator
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂login // 로그인 관련
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂validator // 로그인 데이터 Validator
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂member // 회원 관련
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂validator // 회원 데이터 Validator
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂tokakao // 카카오 api 관련
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂enums // enum 모음
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂exception // exception 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂handler // exception handler
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service // Service 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂impl // Service 구현 클래스
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂utils // 유틸 클래스
 ┃ ┗ 📂test
 ┃ ┃ ┗ 📂java
 ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┃ ┗ 📂sweep
 ┃ ┃ ┃ ┃ ┃ ┗ 📂jaksim31
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂auth // 토큰 관련 통합 테스트
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config // 테스트 환경 설정
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller // 컨트롤러 단위 테스트
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂domain // 레포지터리 단위 테스트
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂diary
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂members
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂token
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂integration // 통합테스트
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service // 서비스 단위 테스트
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂impl
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂util // 테스트 시간 측정을 위한 유틸
 ┣ 📜.gitignore
 ┣ 📜.gitmodules // git submodule 정보
 ┣ 📜Dockerfile
 ┣ 📜LICENSE
 ┣ 📜README.md
 ┣ 📜docker-compose.yml
 ┣ 📜lombok.config
 ┣ 📜mvnw
 ┣ 📜mvnw.cmd
 ┗ 📜pom.xml
```
### 🌱 실행방법

- local

```bash
./mvnw clean install -dspring.profiles.active=local -P local # 빌드
java -jar -Dspring.profiles.active=local -Duser.timezone=Asia/Seoul ./target/jaksim31-0.0.1.jar # 구동
```

- production

```bash
./mvnw clean install -dspring.profiles.active=prod -P prod # 빌드
java -jar -Dspring.profiles.active=prod -Duser.timezone=Asia/Seoul ./target/jaksim31-0.0.1.jar # 구동
```

### 🌱 코드 주석 컨벤션

1. 변경이력 사항(클래스 상단)
    
    ![image](https://user-images.githubusercontent.com/64013256/222890539-0e0f8b52-33fe-4d9b-b571-73920297c317.png)
    
2. 메소드 주석 (메소드 상단)
    
    ![image](https://user-images.githubusercontent.com/64013256/222890554-46a435b1-260a-46e6-b33e-c210bb496351.png)
    

### 🌱 인증 처리 구현

1. 커스텀한 보안 설정을 위해 `SecurityFilterChain` Bean 생성
    
    ![image](https://user-images.githubusercontent.com/64013256/222890529-f0595e70-28bb-400e-8bec-47de064d5a95.png)
    
2. `OncePerRequestFilter`를 상속받아 커스텀 filter 생성
    - 모든 요청에 대해 filter를 거치도록 설정
    
    ![image](https://user-images.githubusercontent.com/64013256/222890518-cebb00c3-b56c-441c-ac02-7f2967bac938.png)
    
### 🌱 요청 데이터에 대한 Validator 구현
    
- Spring validator 사용하여 모든 Request body에 대해 검증 로직 구현
1. 커스텀 Validator 구현
        
    ![image](https://user-images.githubusercontent.com/64013256/222890511-7d499904-78ae-4ce2-b521-6cb49b608fbd.png)
        
2. 컨트롤러에 적용
        
    ![image](https://user-images.githubusercontent.com/64013256/222890490-587777fa-65f5-4d99-9a36-c3fb41ea0929.png)
        
    
### 🌱 통합 예외 처리
    
- 유지보수 고려 Exception Hanlder를 사용하여 한 곳에서 예외처리
        
    ![image](https://user-images.githubusercontent.com/64013256/222890478-059bb0c3-861c-4266-a0a0-80a54c3276d4.png)
        
- 유지보수 고려 응답메세지, 응답코드, Http 상태코드 통합 관리
        
    ![image](https://user-images.githubusercontent.com/64013256/222890476-2aef1db0-412b-44bc-9c84-aca8f7484971.png)
        
    ![image](https://user-images.githubusercontent.com/64013256/222890469-1dfe17c4-379b-45cb-957a-3948f674f4f5.png)
        
    
### 🌱 Redis를 이용한 캐시 적용
    
- Write/Update에 비해 Read 작업이 많이 발생할 것 같은 데이터에 대해 캐싱 적용, 각 캐시 데이터에 Expire time 적용
- Write/Update/Delete에 작업 발생 시 캐시 삭제, Read 작업 발생 시 캐시 조회
1. 환경별 레디스 아키텍처에 따른 캐시 설정 메소드 작성 (Local - `Standalone`, Prod-`Master/Slave`)
        
    ![image](https://user-images.githubusercontent.com/64013256/222890458-5e62e95b-69e7-4e91-a1fc-527a05a8b4bd.png)
        
2. Cache 작업을 위한 CacheAdapter 작성
    - 캐시 데이터 별 작성
        
    ![image](https://user-images.githubusercontent.com/64013256/222890447-e0b4d949-9d1e-4cb8-88e5-1f3db8bc3c3e.png)
        
        adapter/cache/DiaryPagingCacheAdapter.java
        
    - put: 데이터 삽입 및 갱신
    - get: 데이터 조회
    - delete: 데이터 삭제
    - findAndDelete: 해당 key를 포함하는 데이터를 찾은 후 삭제 (작업 시간 고려하여 10개씩 끊어서 스캔하도록 설정)
3. 서비스 로직에 캐시 적용
    - 필요한 기능에 따라 Annotation 또는 RedisTemplate 사용
        
    ![image](https://user-images.githubusercontent.com/64013256/222890438-715eebc2-6a01-4a51-bec9-0e31281496ec.png)
        
    service/DiaryServiceImple.java 일부 발췌
        
    
### 🌱 Mongodb Aggregation을 이용하여 사용자 일기의 감정 통계 api 구현
    
- spring.data.mongodb 라이브러리 사용
1. 해당 사용자의 일기 검색
2. 감정별로 일기 데이터 Grouping 및 카운트 응답
        
    ![image](https://user-images.githubusercontent.com/64013256/222890430-b1973729-6e39-48d5-bf52-228cf91d7ba5.png)
        
    
### 🌱 ElasticSerach와 연동하여 일기 검색 구현
    
- Spring-boot-elasticsearch 라이브러리 사용
1. Elastic Search와 연동
        
    ![image](https://user-images.githubusercontent.com/64013256/222890410-290abc72-9c9f-4528-8205-e1928c8ec23d.png)
        
    config/ElasticSearchConfig.java
        
2. 검색 조건 설정 및 elastic search 검색 api 호출
    - `검색어`, `날짜`, `감정`에 대한 조건 설정
        
    ![image](https://user-images.githubusercontent.com/64013256/222890390-28a80363-9be5-4e7d-a627-b80c76d9c709.png)
        
    domain/diary/DiarySearchQueryRepository.java
        
    
### 🌱 OpenApi를 이용한 api 호출 테스트 및 관리

![image](https://user-images.githubusercontent.com/64013256/222894508-f0b57c58-dc67-4040-856e-bd3f77cf9134.png)
    

▶️ [Swagger UI](https://jaksim31.xyz/swagger-ui/index.html)
      
    
### 🌱 설정 파일 관리
    
- 보안 고려 git submodule을 이용하여 private repository에 별도로 관리
        
    ![image](https://user-images.githubusercontent.com/64013256/222890362-49590233-f0cb-4453-926c-37cd15e01765.png)
        
    Jaksim31 백엔드 레포지토리 (Public)
        
    ![image](https://user-images.githubusercontent.com/64013256/222890345-5a3fd1fd-cffc-42f9-8cbe-a2e9ae8db991.png)
        
    Jaksim31 설정정보 레포지토리 (Private)

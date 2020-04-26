# mycoupon-jwt-app
Spring boot REST API 기반의 쿠폰 앱<br>
Spring security를 이용한 JWT 인증 / 인가를 사용한 유저 관리 

## Using
- framework : Spring boot 2.2.6 
- build tool : gradle 6.3
- dependency :
```
Spring boot data JPA
Spring boot starter security
java-jwt
lombok
h2-database
```
- test : Junit4, JUnitParams

## Project structure
```
-mycoupon
    |   MycouponApplication.java
    |
    +---config
    |   |   AppConfig.java
    |   |   CustomControllerAdvice.java
    |   |   SecurityConfig.java
    |   |
    |   \---security
    |       |   CustomUserDetailsService.java
    |       |   JWTSecurityConstants.java
    |       |   SecurityMember.java
    |       |
    |       \---filters
    |               JwtAuthenticationSignInFilter.java
    |               JwtAuthenticationSignUpFilter.java
    |               JwtAuthorizationFilter.java
    |
    +---controller
    |       CouponController.java
    |
    +---domain
    |       Coupon.java
    |       CouponInfo.java
    |       Member.java
    |
    +---exceptions
    |       CouponMemberNotMatchException.java
    |       CouponNotFoundException.java
    |       IllegalArgumentException.java
    |       InvalidTokenException.java
    |       MemberNotFoundException.java
    |       SignUpFailedException.java
    |
    +---payload
    |       UserModel.java
    |
    +---repository
    |       CouponInfoRepository.java
    |       CouponRepository.java
    |       MemberRepository.java
    |
    +---service
    |       CouponService.java
    |       MemberService.java
    |
    \---utils
            ValidationRegex.java
```

## Problem Solving
(class diagram 사용 출처 : https://online.visual-paradigm.com/)
### JWT authentication (signup / signin 접근)
**1. signp**
<img width="500" alt="JwtAuthenticationSignUpFilter" src="https://user-images.githubusercontent.com/26767161/80301698-8d397b00-87e0-11ea-8651-160b793d6d28.PNG">
- spring security의 `AbstractAuthenticationProcessingFilter`를 상속받아 `/signup` 요청이 들어올때 필터가 적용되도록 하였고,
유저의 가입(인증) 성공시 JWT를 발급하도록 하였다. <br>
- `attemptAuthentication` method에서 주입받은 memberservice 빈을 이용해 가입 요청을 처리하고, Authentication 객체를 넘겨준다. <br>
- `successfulAuthentication` method에서는 가입을 성공한 유저기반의 JWT 토큰을 생성하여 응답 헤더에 넘겨준다. <br>
- `unsuccessfulAuthentication` method에서는 인증 실패 Exception 구분에 따라 적당한 status_code와 에러메시지를 리턴한다. <br>

***

**2. signin**
<img width="500" alt="JwtAuthenticationSignInFilter" src="https://user-images.githubusercontent.com/26767161/80301668-59f6ec00-87e0-11ea-9d5e-5191e7ea54cd.PNG">
- spring security의 `UsernamePasswordAuthenticationFilter`를 상속받아 `/signin` 요청이 들어올때 필터가 적용되도록 하였고,
유저의 로그인 인증 성공시 JWT를 발급하도록 하였다. <br> 
- `attemptAuthentication` method에서 authenticationmanger를 통해 유저의 Authentication 객체를 넘겨준다. <br>
- `successfulAuthentication` method에서는 로그인을 성공항 유저기반의 JWT 토큰을 생성하여 응답 헤더에 넘겨준다. <br>
- `unsuccessfulAuthentication` method에서는 인증 실패 Exception 구분에 따라 적당한 status_code와 에러메시지를 리턴한다. <br>

### JWT authorization 
<img width="500" alt="JwtAuthorizationFilter" src="https://user-images.githubusercontent.com/26767161/80301701-9aef0080-87e0-11ea-8981-f454127f74ad.PNG">

- spring security의 `BasicAuthenticationFilter`를 상속받아 `/coupon/**` API 요청이 들어오면 JWT를 먼저 검사하도록 하였다. <br> 
- `doFilterInternal` method에서 요청 헤더를 검사하고 토큰이 올바른지 검증 후 인증 성공 처리를 한다. <br>
- 이 과정에서 인증이 제대로 성공했다면, request에 `memberId` 정보를 담아 Controller 로직을 타도록 한다. <br>
- `BasicAuthenticationFilter`는 AuthenticationException을 `AuthenticationEntryPoint`에서 handling 하므로,
 `CustomAuthenticationEntryPoint`를 작성하여 가능한 에러에 따라 응답코드와 에러메시지를 설정하였다. 
***

### Entity 설계 및 연관관계
- **Coupon - Member (ManyToOne)** 단방향 연관 관계. Coupon entity(Many)에서 Member 필드를 가지도록 설계하였다. 
- 유저는 쿠폰을 가지고 있을 수도 있고, 하나도 가지고 없을 수도 있다.
- **Coupon - CouponInfo (OneToOne)** 단방향 연관 관계. Coupon entity에서 CouponInfo 필드를 가지도록 설계하였다.
- 쿠폰은 반드시 쿠폰 정보를 필수로 가진다.

### API 설계
- 각 API 요구사항에 필요한 DB 쿼리들을 `repository`에서 구현하였다. 
- 각 `service`에서 필요한 repository의 메소드를 호출하도록 설계하였다. 
- service에서는 repository를 호출하기 전에, 필요한 경우 `validation`을 수행하여 통과하지 못하면 400대 에러를 리턴하도록 설계하였다.
- `controller`에서는 내용의 존재여부, 에러 발생 여부에 따라 알맞은 status_code와 Coupon 데이터를 리턴한다.

### Test 설계
- repository, service, controller 별로 의존 관계에 있는 빈들을 `Mock`하여 unit test 코드를 작성하였다.
- unit test 이외에 security filter 로직을 타야하는 테스트 검증도 필요하다고 판단하여, spring의 `@WebMvcTest`를 이용해
endpoint로 직접 요청하는 테스트를 추가하였다.

## Schema
![](https://user-images.githubusercontent.com/26767161/80300710-f964b080-87d9-11ea-978c-9b3738096eb2.PNG)
(사용 출처 : https://dbdiagram.io/)

## How to Build
```
gradlew build
```

## How to run app
```
gradlew bootrun
```

## How to run test
```
gradlew test
```

## API specification
**기본 요청 주소 : `http://localhost:8080/`** <br>
**H2 DB client host : `http://localhost:8080/h2-console`** 

- signup : 
  - endpoint : `/signup`
  - method: `POST`
  - payload: id(string. 3자이상 30자 이하), password(string. 8자 이상의 영어, 숫자, 특수문자 최소 1개씩 포함)
  - response: 200 OK. 응답 헤더에 `Bearer {JWT}` 토큰을 실어서 전달한다. 응답 바디는 비어있음.

- signin : 
  - endpoint : `/signin`
  - method: `POST`
  - payload: id(string. 3자이상 30자 이하), password(string. 8자 이상의 영어, 숫자, 특수문자 최소 1개씩 포함)
  - response: 200 OK. 응답 헤더에 `Bearer {JWT}` 토큰을 실어서 전달한다. 응답 바디는 비어있음.
  
- 랜덤한 N개 쿠폰 생성 : 
  - endpoint : `/coupon/{num}`
  - method: `POST`
  - request-header: 로그인/가입시 전달받은 JWT를 Authorization Bearer {JWT} 형식으로 전달.
  - response: 
    - 201 CREATED
    - num이 1000을 넘어갈경우 400 BAD-REQUEST
  
- 사용자 쿠폰 지급 : 
  - endpoint : `/coupon/user`
  - method: `PUT`
  - request-header: 로그인/가입시 전달받은 JWT를 Authorization Bearer {JWT} 형식으로 전달.
  - response: 
    - 200 OK with empty body. 
    - 사용자가 존재하지 않을경우 404 NOT-FOUND.
    
  
- 사용자 쿠폰 조회 : 
  - endpoint : `/coupon/user`
  - method: `GET`
  - request-header: 로그인/가입시 전달받은 JWT를 Authorization Bearer {JWT} 형식으로 전달.
  - response: 
    - 200 OK (body: coupon data). 
    - 쿠폰이 없을 경우 204 NO-CONTENT.
  
  
- 사용자 쿠폰 사용 : 
  - endpoint : `/coupon/{coupon_code}`
  - method: `PUT`
  - request-header: 로그인/가입시 전달받은 JWT를 Authorization Bearer {JWT} 형식으로 전달.
  - response: 
    - 200 OK (body: coupon data). 
    - 쿠폰번호가 UUID 형식이 아닌경우 400 BAD-REQUEST
    - 쿠폰번호에 해당하는 쿠폰이 없을 경우 404 NOT-FOUND.
    - 쿠폰번호에 해당하는 쿠폰이 해당 사용자의 소유가 아니면 403 FORBIDDEN
    
- 사용자 쿠폰 사용 취소 : 
  - endpoint : `/coupon/{coupon_code}/cancel`
  - method: `PUT`
  - request-header: 로그인/가입시 전달받은 JWT를 Authorization Bearer {JWT} 형식으로 전달.
  - response: 
    - 200 OK (body: coupon data). 
    - 쿠폰번호가 UUID 형식이 아닌경우 400 BAD-REQUEST
    - 쿠폰번호에 해당하는 쿠폰이 없을 경우 404 NOT-FOUND.
    - 쿠폰번호에 해당하는 쿠폰이 해당 사용자의 소유가 아니면 403 FORBIDDEN
    
- 당일 만료되는 쿠폰 조회 : 
  - endpoint : `/coupon/expired`
  - method: `GET`
  - request-header: 로그인/가입시 전달받은 JWT를 Authorization Bearer {JWT} 형식으로 전달.
  - response: 
    - 200 OK (body: coupon data). 
    - 당일 만료되는 쿠폰이 없을경우 204 NO_CONTENT
    
    
  







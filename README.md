# mycoupon-jwt-app
REST API 기반의 JWT 인증 / 인가를 사용하여 유저 관리 및 쿠폰 시스템

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
- spring security의 `AbstractAuthenticationProcessingFilter`를 상속받아 유저의 가입(인증) 성공시 JWT를 발급하도록 하였다. <br>
- `attemptAuthentication` method에서 주입받은 memberservice 빈을 이용해 가입 요청을 처리하고, Authentication 객체를 넘겨준다. <br>
- `successfulAuthentication` method에서는 가입을 성공한 유저기반의 JWT 토큰을 생성하여 응답 헤더에 넘겨준다. <br>
- `unsuccessfulAuthentication` method에서는 인증 실패 Exception 구분에 따라 적당한 status_code와 에러메시지를 리턴한다. <br>

***

**2. signin**
<img width="500" alt="JwtAuthenticationSignInFilter" src="https://user-images.githubusercontent.com/26767161/80301668-59f6ec00-87e0-11ea-9d5e-5191e7ea54cd.PNG">
- spring security의 `UsernamePasswordAuthenticationFilter`를 상속받아 유저의 로그인 인증 성공시 JWT를 발급하도록 하였다. <br> 
- `attemptAuthentication` method에서 authenticationmanger를 통해 유저의 Authentication 객체를 넘겨준다. <br>
- `successfulAuthentication` method에서는 로그인을 성공항 유저기반의 JWT 토큰을 생성하여 응답 헤더에 넘겨준다. <br>
- `unsuccessfulAuthentication` method에서는 인증 실패 Exception 구분에 따라 적당한 status_code와 에러메시지를 리턴한다. <br>

### JWT authorization 
<img width="500" alt="JwtAuthorizationFilter" src="https://user-images.githubusercontent.com/26767161/80301701-9aef0080-87e0-11ea-8981-f454127f74ad.PNG">
- spring security의 `BasicAuthenticationFilter`를 상속받아 `/coupon/**` API 요청이 들어오면 JWT를 먼저 검사하도록 하였다. <br> 
- `doFilterInternal` method에서 요청 헤더를 검사하고 토큰이 올바른지 검증 후 인증 성공 처리를 한다. <br>
- 이 과정에서 인증이 제대로 성공했다면, request에 `memberId` 정보를 담아 Controller 로직을 타도록 한다. <br>

### Entity 설계 및 연관관계
- **Conpon - Member (ManyToOne)** 단방향 연관 관계. Coupon entity(Many)에서 Member 필드를 가지도록 설계하였다.
- 유저는 쿠폰을 가지고 있을 수도 있고, 하나도 가지고 없을 수도 있다.
- **Coupon - CouponInfo (OneToOne)** 단방향 연관 관계. Coupon entity에서 CouponInfo 필드를 가지도록 설계하였다.
- 쿠폰은 반드시 쿠폰 정보를 필수로 가진다.
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

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
1. JWT authentication (signup / signin 접근)
  - signp
<img width="500" alt="JwtAuthenticationSignUpFilter" src="https://user-images.githubusercontent.com/26767161/80301698-8d397b00-87e0-11ea-8651-160b793d6d28.PNG">

  - signin
<img width="500" alt="JwtAuthenticationSignInFilter" src="https://user-images.githubusercontent.com/26767161/80301668-59f6ec00-87e0-11ea-9d5e-5191e7ea54cd.PNG">
2. JWT authorization 
<img width="500" alt="JwtAuthorizationFilter" src="https://user-images.githubusercontent.com/26767161/80301701-9aef0080-87e0-11ea-8981-f454127f74ad.PNG">

3. Entity 설계 및 연관관계

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

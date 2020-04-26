# mycoupon-jwt-app
MyCoupon Rest Spring boot App using JWT authentication / authorization. 

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
1.

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

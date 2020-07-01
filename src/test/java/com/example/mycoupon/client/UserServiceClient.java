package com.example.mycoupon.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Set;

// MSA 아래 eureka에 등록된 다른 user service 요청을 위한 feign client.
// Ribbon이 클라이언트로 로드밸런스 수행
@FeignClient(name="user-service")
public interface UserServiceClient {
    @GetMapping("/api/v1/users/github/sync")
    Map<String, String> getGithubUser(@RequestBody Set<String> users);
}

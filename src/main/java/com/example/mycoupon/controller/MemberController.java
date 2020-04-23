package com.example.mycoupon.controller;

import com.example.mycoupon.payload.UserModel;
import com.example.mycoupon.domain.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserModel userModel) {
        // validation
        // DB save (password 암호화)
        // JWT create
        URI selfLink = URI.create(
                ServletUriComponentsBuilder.fromCurrentRequest().toUriString()
        );
        return ResponseEntity.created(selfLink).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody UserModel userModel) {
        // validation
        // DB check login(Spring security)
        // JWT create
        return ResponseEntity.ok().build();
    }
}

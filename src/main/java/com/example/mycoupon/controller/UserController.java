package com.example.mycoupon.controller;

import com.example.mycoupon.payload.UserModel;
import com.example.mycoupon.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final MemberService memberService;

    @Autowired
    public UserController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public String signUp(@RequestBody UserModel userModel) {
        // validation
        // DB save (password 암호화)
        // JWT create
        // return JWT
        //System.out.println(userModel.getId());
        //memberService.signUp(userModel);
        return "success";
    }

    @PostMapping("/login")
    public String signIn(@RequestBody UserModel userModel) {
        // validation
        // DB check login(Spring security)
        // JWT create
        // return JWT
        // signIn request ->
        return "success";
    }
}

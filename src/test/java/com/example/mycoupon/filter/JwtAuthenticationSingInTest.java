package com.example.mycoupon.filter;

import com.example.mycoupon.payload.UserModel;
import com.example.mycoupon.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationSingInTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Test
    public void signinSuccess() throws Exception {
        String testId = "test";
        String testPw = "test1234!";
        UserModel model = new UserModel();
        model.setId(testId);
        model.setPassword(testPw);

        memberService.signUp(model);

        mvc.perform(MockMvcRequestBuilders
                .post("/signin")
                .content(new ObjectMapper().writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"));
    }

    @Test
    public void signinFailure() throws Exception {
        String testId = "test";
        String testPw = "test1234";
        UserModel model = new UserModel();
        model.setId(testId);
        model.setPassword(testPw);

        mvc.perform(MockMvcRequestBuilders
                .post("/signin")
                .content(new ObjectMapper().writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}

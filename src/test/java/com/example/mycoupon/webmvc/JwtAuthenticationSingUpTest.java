package com.example.mycoupon.webmvc;

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
public class JwtAuthenticationSingUpTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Test
    public void signupSuccess() throws Exception {
        String testId = "test1";
        String testPw = "test1234!";
        UserModel model = new UserModel();
        model.setId(testId);
        model.setPassword(testPw);

        mvc.perform(MockMvcRequestBuilders
                .post("/signup")
                .content(new ObjectMapper().writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Authorization"));
    }

    @Test
    public void signupFailureByValidationCheck() throws Exception {
        String testId = "test2";
        String testPw = "test1234";
        UserModel model = new UserModel();
        model.setId(testId);
        model.setPassword(testPw);

        mvc.perform(MockMvcRequestBuilders
                .post("/signup")
                .content(new ObjectMapper().writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Authorization"));
    }

    @Test
    public void signupFailureByIdAlreadyExists() throws Exception {
        String testId = "test3";
        String testPw = "test1234!";
        UserModel model = new UserModel();
        model.setId(testId);
        model.setPassword(testPw);

        memberService.signUp(model);

        mvc.perform(MockMvcRequestBuilders
                .post("/signup")
                .content(new ObjectMapper().writeValueAsString(model))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Authorization"));
    }
}

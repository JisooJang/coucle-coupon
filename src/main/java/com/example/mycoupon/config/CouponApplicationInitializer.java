package com.example.mycoupon.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class CouponApplicationInitializer implements WebApplicationInitializer {
    // 스프링 MVC에서 비동기 요청 처리를 사용하려면 모든 필터와 서블릿이 비동기로 작동하게끔 활성화해야 한다.
    // 필터 / 서블릿을 등록할 때 setAsyncSupported(true) 메서드를 호출하면 비동기모드가 켜진다.
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        DispatcherServlet servlet = new DispatcherServlet();
        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", servlet);
        registration.setAsyncSupported(true);
    }
}

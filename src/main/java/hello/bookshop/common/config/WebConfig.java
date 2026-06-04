package hello.bookshop.common.config;

import hello.bookshop.common.interceptor.AdminCheckInterceptor;
import hello.bookshop.common.interceptor.GuestOnlyInterceptor;
import hello.bookshop.common.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig  implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new GuestOnlyInterceptor())
                .order(1)
                .addPathPatterns(
                        "/member/login",
                        "/member/signup/**"
                );


        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns(
                        "/member/mypage",
                        "/member/info",
                        "/member/edit"
                );


        registry.addInterceptor(new AdminCheckInterceptor())
                .order(3)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/login",
                        "admin/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                );

    }
}

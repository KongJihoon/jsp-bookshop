package hello.bookshop.home.controller;


import hello.bookshop.home.dto.response.HomeProductResponse;
import hello.bookshop.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {


    private final HomeService homeService;



    @GetMapping("/")
    public String home(Model model) {

        List<HomeProductResponse> latestProducts = homeService.findLatestProducts();

        model.addAttribute("latestProducts", latestProducts);
        return "home";
    }

}

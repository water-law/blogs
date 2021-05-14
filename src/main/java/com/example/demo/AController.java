package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author duwei
 * @date 2021/5/8
 */
@Controller
public class AController {


    @GetMapping("/login")
//    @ResponseBody
    public String login() {

        return "aa";
    }
}

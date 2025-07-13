package br.edu.ifpb.pweb2.caesarcoin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    
    @RequestMapping("/home")
    public ModelAndView home(ModelAndView model) {
        model.addObject("menu", "home");
        model.setViewName("index");
        return model;
    }
}

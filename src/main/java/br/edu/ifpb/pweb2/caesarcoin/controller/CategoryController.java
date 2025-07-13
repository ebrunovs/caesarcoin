package br.edu.ifpb.pweb2.caesarcoin.controller;

import br.edu.ifpb.pweb2.caesarcoin.model.Category;
import br.edu.ifpb.pweb2.caesarcoin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService catService;

    private final String[] nameCategories = {
            "Salário", "Cashback", "Resgate",
            "Investimento", "Outras Entradas", "Saúde e Remédios", "Academia e Personal", "Carros e Uber",
            "Educação e Cursos", "Lazer e Turismo", "Condomínio", "Energia", "Celular", "Internet", "Itens Pessoais",
            "Feira", "Casa", "Impostos", "Outros gastos",
            "Aporte Renda Fixa", "Aporte Renda Variável", "Aporte Reserva Emergencia", "Aporte Previdência"
    };



    @GetMapping("/form")
    public ModelAndView getForm(ModelAndView model) {
        model.setViewName("categories/form");
        model.addObject("category", new Category());
        return model;
    }


    @PostMapping
    public ModelAndView save(Category cat, ModelAndView model, RedirectAttributes attr){
        catService.save(cat);
        attr.addFlashAttribute("message", "Categoria inserida com sucesso!");
        model.setViewName("redirect:categories");
        return model;
    }



    @GetMapping
    public ModelAndView listAll(ModelAndView model){
        model.addObject("categories", catService.findAll());
        model.setViewName("categories/list");
        return model;
    }

    @ModelAttribute("categoryItens")
    public List<String> getCategories(){
        return Arrays.asList(nameCategories);
    }





}

package br.edu.ifpb.pweb2.caesarcoin.controller;

import br.edu.ifpb.pweb2.caesarcoin.exception.BusinessException;
import br.edu.ifpb.pweb2.caesarcoin.exception.InvalidDataException;
import br.edu.ifpb.pweb2.caesarcoin.exception.ResourceNotFoundException;
import br.edu.ifpb.pweb2.caesarcoin.model.Category;
import br.edu.ifpb.pweb2.caesarcoin.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
        try {
            model.addObject("menu", "category");
            model.setViewName("categories/form");
            model.addObject("category", new Category());
        } catch (Exception e) {
            throw new BusinessException("Erro ao carregar formulário de categoria", e);
        }
        return model;
    }

    @PostMapping
    public ModelAndView save(Category cat, ModelAndView model, RedirectAttributes attr){
        try {
            if (cat.getName() == null || cat.getName().trim().isEmpty()) {
                throw new InvalidDataException("Nome da categoria é obrigatório");
            }
            if (cat.getKind() == null) {
                throw new InvalidDataException("Tipo da categoria é obrigatório");
            }
            if (cat.getOrd() == null || cat.getOrd() < 1) {
                throw new InvalidDataException("Ordem deve ser um número positivo");
            }
            
            catService.save(cat);
            attr.addFlashAttribute("message", "Categoria inserida com sucesso!");
            model.setViewName("redirect:categories");
        } catch (Exception e) {
            if (e instanceof InvalidDataException) {
                throw e;
            }
            throw new BusinessException("Erro ao salvar categoria", e);
        }
        return model;
    }

    @GetMapping
    public ModelAndView listAll(ModelAndView model){
        try {
            model.addObject("menu", "category");
            model.addObject("categories", catService.findAll());
            model.setViewName("categories/list");
        } catch (Exception e) {
            throw new BusinessException("Erro ao listar categorias", e);
        }
        return model;
    }
    
    @GetMapping("/{id}")
    public ModelAndView getCategoryById(@PathVariable(value = "id") Integer id, ModelAndView model) {
        try {
            if (id == null || id <= 0) {
                throw new InvalidDataException("ID inválido");
            }
            
            Category category = catService.findById(id);
            if (category == null) {
                throw new ResourceNotFoundException("Categoria não encontrada com ID: " + id);
            }
            
            model.addObject("menu", "category");
            model.setViewName("categories/form");
            model.addObject("category", category);
        } catch (Exception e) {
            if (e instanceof InvalidDataException || e instanceof ResourceNotFoundException) {
                throw e;
            }
            throw new BusinessException("Erro ao buscar categoria", e);
        }
        return model;
    }

    @ModelAttribute("categoryItens")
    public List<String> getCategories(){
        return Arrays.asList(nameCategories);
    }

    @GetMapping("/{id}/delete")
    public ModelAndView deleteById(@PathVariable(value = "id") Integer id,
        ModelAndView mav, RedirectAttributes attr) {
        catService.deleteById(id);
        attr.addFlashAttribute("message", "Categoria removida com sucesso!");
        mav.setViewName("redirect:/categories");
        return mav;
    }

    // Tratamentos de exceção locais
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) {
        resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("status", resp.getStatus());
        return model;
    }

    @ExceptionHandler(InvalidDataException.class)
    public ModelAndView handleInvalidDataException(InvalidDataException ex, HttpServletRequest req,jakarta.servlet.http.HttpServletResponse resp) {       
        resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("status", resp.getStatus());
        return model;
    }



    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) {
        resp.setStatus(jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ModelAndView model = new ModelAndView("/error");
        model.addObject("message", ex.getMessage());
        model.addObject("exception", ex);
        model.addObject("path", req.getRequestURI());
        model.addObject("status", resp.getStatus());
        return model;
    }
}
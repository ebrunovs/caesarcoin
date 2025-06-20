package br.edu.ifpb.pweb2.caesarcoin.service;

import br.edu.ifpb.pweb2.caesarcoin.model.Category;
import br.edu.ifpb.pweb2.caesarcoin.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryService implements Service<Category, Integer> {

    @Autowired
    private CategoryRepository catRepo;

    @Override
    public List<Category> findAll() {
        return catRepo.findAll();
    }

    @Override
    public Category findById(Integer integer) {
        return catRepo.findById(integer).orElse(null);
    }

    @Override
    public Category save(Category category) {
        return catRepo.save(category);
    }


    public Category findByName(String name ){
        return catRepo.findByName(name);
    }
}

package br.edu.ifpb.pweb2.caesarcoin.service;

import br.edu.ifpb.pweb2.caesarcoin.model.Category;
import br.edu.ifpb.pweb2.caesarcoin.model.TransactionType;
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

    public void deleteById(Integer id) {
        catRepo.deleteById(id);
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
    
    public List<Category> findByTransactionType(TransactionType type) {
        return catRepo.findByKindAndIsActive(type, true);
    }
    
    public List<Category> findAllByTransactionType(TransactionType type) {
        return catRepo.findByKind(type);
    }
    
    public List<Category> findActiveByTransactionTypeOrderedByOrd(TransactionType type) {
        return catRepo.findByKindAndIsActiveOrderByOrd(type, true);
    }
}

package br.edu.ifpb.pweb2.caesarcoin.repository;

import br.edu.ifpb.pweb2.caesarcoin.model.Category;
import br.edu.ifpb.pweb2.caesarcoin.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);
    List<Category> findByKindAndIsActive(TransactionType kind, Boolean isActive);
    List<Category> findByKind(TransactionType kind);
    List<Category> findByKindAndIsActiveOrderByOrd(TransactionType kind, Boolean isActive);
    void deleteById(Integer id);
}

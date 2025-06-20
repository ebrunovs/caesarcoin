package br.edu.ifpb.pweb2.caesarcoin.repository;

import br.edu.ifpb.pweb2.caesarcoin.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);
}

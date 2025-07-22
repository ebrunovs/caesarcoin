package br.edu.ifpb.pweb2.caesarcoin.config;

import br.edu.ifpb.pweb2.caesarcoin.model.Category;
import br.edu.ifpb.pweb2.caesarcoin.model.TransactionType;
import br.edu.ifpb.pweb2.caesarcoin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryInitializer implements CommandLineRunner {

    @Autowired
    private CategoryService categoryService;

    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existem categorias para evitar duplicação
        if (categoryService.findAll().isEmpty()) {
            createPredefinedCategories();
        }
    }

    private void createPredefinedCategories() {
        // Categorias de ENTRADA
        createCategory("Salário", TransactionType.ENTRADA, true, 1);
        createCategory("Cashback", TransactionType.ENTRADA, true, 2);
        createCategory("Resgate Investimento", TransactionType.ENTRADA, true, 3);
        createCategory("Outras Entradas", TransactionType.ENTRADA, true, 4);

        // Categorias de SAIDA
        createCategory("Saúde e Remédios", TransactionType.SAIDA, true, 1);
        createCategory("Academia e Personal", TransactionType.SAIDA, true, 2);
        createCategory("Carros e Uber", TransactionType.SAIDA, true, 3);
        createCategory("Educação e Cursos", TransactionType.SAIDA, true, 4);
        createCategory("Lazer e Turismo", TransactionType.SAIDA, true, 5);
        createCategory("Condomínio", TransactionType.SAIDA, true, 6);
        createCategory("Energia", TransactionType.SAIDA, true, 7);
        createCategory("Celular", TransactionType.SAIDA, true, 8);
        createCategory("Internet", TransactionType.SAIDA, true, 9);
        createCategory("Itens Pessoais", TransactionType.SAIDA, true, 10);
        createCategory("Feira", TransactionType.SAIDA, true, 11);
        createCategory("Casa", TransactionType.SAIDA, true, 12);
        createCategory("Impostos", TransactionType.SAIDA, true, 13);
        createCategory("Outros gastos", TransactionType.SAIDA, true, 14);

        // Categorias de INVESTIMENTO
        createCategory("Aporte Renda Fixa", TransactionType.INVESTIMENTO, true, 1);
        createCategory("Aporte Renda Variável", TransactionType.INVESTIMENTO, true, 2);
        createCategory("Aporte Reserva Emergencia", TransactionType.INVESTIMENTO, true, 3);
        createCategory("Aporte Previdência", TransactionType.INVESTIMENTO, true, 4);

        System.out.println("Categorias predefinidas criadas com sucesso!");
    }

    private void createCategory(String name, TransactionType kind, Boolean isActive, Integer order) {
        Category category = new Category();
        category.setName(name);
        category.setKind(kind);
        category.setIsActive(isActive);
        category.setOrd(order);
        categoryService.save(category);
    }
}

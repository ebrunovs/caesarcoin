package br.edu.ifpb.pweb2.caesarcoin.util;

import br.edu.ifpb.pweb2.caesarcoin.model.Category;
import br.edu.ifpb.pweb2.caesarcoin.model.TransactionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CategoryColorHelper {
    
    // Cores para categorias de ENTRADA
    private static final Map<String, String> INCOME_COLORS = new HashMap<>();
    
    // Cores para categorias de SAIDA
    private static final Map<String, String> OUTCOME_COLORS = new HashMap<>();
    
    // Cores para categorias de INVESTIMENTO
    private static final Map<String, String> INVESTMENT_COLORS = new HashMap<>();
    
    // Cores disponíveis para categorias aleatórias
    private static final String[] RANDOM_COLORS = {
        "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FECA57", 
        "#FF9FF3", "#54A0FF", "#5F27CD", "#00D2D3", "#FF9F43",
        "#FC427B", "#FD79A8", "#A29BFE", "#6C5CE7", "#00B894",
        "#00CEC9", "#55A3FF", "#74B9FF", "#FD79A8", "#E17055"
    };
    
    static {
        // Cores para categorias de ENTRADA
        INCOME_COLORS.put("Salário", "#10B981");              // Verde forte
        INCOME_COLORS.put("Cashback", "#059669");             // Verde médio
        INCOME_COLORS.put("Resgate Investimento", "#047857");  // Verde escuro
        INCOME_COLORS.put("Outras Entradas", "#34D399");      // Verde claro
        
        // Cores para categorias de SAIDA
        OUTCOME_COLORS.put("Saúde e Remédios", "#EF4444");      // Vermelho
        OUTCOME_COLORS.put("Academia e Personal", "#F97316");    // Laranja
        OUTCOME_COLORS.put("Carros e Uber", "#F59E0B");         // Amarelo
        OUTCOME_COLORS.put("Educação e Cursos", "#8B5CF6");     // Roxo
        OUTCOME_COLORS.put("Lazer e Turismo", "#EC4899");       // Rosa
        OUTCOME_COLORS.put("Condomínio", "#6B7280");           // Cinza
        OUTCOME_COLORS.put("Energia", "#FCD34D");              // Amarelo claro
        OUTCOME_COLORS.put("Celular", "#60A5FA");              // Azul claro
        OUTCOME_COLORS.put("Internet", "#34D399");             // Verde claro
        OUTCOME_COLORS.put("Itens Pessoais", "#F472B6");       // Rosa claro
        OUTCOME_COLORS.put("Feira", "#84CC16");                // Verde lima
        OUTCOME_COLORS.put("Casa", "#A78BFA");                 // Roxo claro
        OUTCOME_COLORS.put("Impostos", "#DC2626");             // Vermelho escuro
        OUTCOME_COLORS.put("Outros gastos", "#6B7280");        // Cinza
        
        // Cores para categorias de INVESTIMENTO
        INVESTMENT_COLORS.put("Aporte Renda Fixa", "#3B82F6");          // Azul
        INVESTMENT_COLORS.put("Aporte Renda Variável", "#1D4ED8");       // Azul escuro
        INVESTMENT_COLORS.put("Aporte Reserva Emergencia", "#0EA5E9");   // Azul claro
        INVESTMENT_COLORS.put("Aporte Previdência", "#0284C7");          // Azul médio
    }
    
    /**
     * Obtém a cor para uma categoria específica
     */
    public static String getColorForCategory(Category category) {
        String categoryName = category.getName();
        TransactionType type = category.getKind();
        
        // Verificar se é uma categoria predefinida
        String color = null;
        
        switch (type) {
            case ENTRADA:
                color = INCOME_COLORS.get(categoryName);
                break;
            case SAIDA:
                color = OUTCOME_COLORS.get(categoryName);
                break;
            case INVESTIMENTO:
                color = INVESTMENT_COLORS.get(categoryName);
                break;
        }
        
        // Se não for uma categoria predefinida, gerar uma cor aleatória baseada no ID
        if (color == null) {
            color = getRandomColorForCategory(category.getId());
        }
        
        return color;
    }
    
    /**
     * Gera uma cor aleatória consistente baseada no ID da categoria
     */
    private static String getRandomColorForCategory(Integer categoryId) {
        if (categoryId == null) {
            return RANDOM_COLORS[0]; // Cor padrão
        }
        
        // Usar o ID como semente para garantir que a mesma categoria sempre tenha a mesma cor
        Random random = new Random(categoryId.longValue());
        int index = random.nextInt(RANDOM_COLORS.length);
        return RANDOM_COLORS[index];
    }
    
    /**
     * Obtém a cor de fundo (com transparência) para uma categoria
     */
    public static String getBackgroundColorForCategory(Category category) {
        String color = getColorForCategory(category);
        // Converter hex para rgba com 10% de opacidade
        return hexToRgba(color, 0.1);
    }
    
    /**
     * Converte cor hexadecimal para rgba com transparência
     */
    private static String hexToRgba(String hex, double alpha) {
        // Remove o # se presente
        hex = hex.replace("#", "");
        
        // Converte para RGB
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        
        return String.format("rgba(%d, %d, %d, %.1f)", r, g, b, alpha);
    }
}

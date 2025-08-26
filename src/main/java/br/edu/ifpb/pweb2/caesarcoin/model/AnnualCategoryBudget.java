package br.edu.ifpb.pweb2.caesarcoin.model;

/**
 * DTO para representar totais mensais e anual de uma categoria.
 */
public class AnnualCategoryBudget {

    private final Category category;
    // índice 0 = Jan ... 11 = Dez
    private final double[] monthlyTotals = new double[12];
    private double annualTotal = 0d;

    public AnnualCategoryBudget(Category category) {
        this.category = category;
    }

    public void addValue(int month, double value) {
        if (month < 1 || month > 12) return;
        monthlyTotals[month - 1] += value;
        annualTotal += value;
    }

    public Category getCategory() {
        return category;
    }

    public double[] getMonthlyTotals() {
        return monthlyTotals;
    }

    public double getAnnualTotal() {
        return annualTotal;
    }
}

package br.edu.ifpb.pweb2.caesarcoin.model;

import java.time.LocalDate;
import java.util.List;

public class ExtractData {
    private List<Transaction> transactions;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalIncomes;
    private double totalOutcomes;
    private double totalInvestments;
    private double periodBalance;

    public ExtractData() {}

    public ExtractData(List<Transaction> transactions, LocalDate startDate, LocalDate endDate,
                      double totalIncomes, double totalOutcomes, double totalInvestments, double periodBalance) {
        this.transactions = transactions;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncomes = totalIncomes;
        this.totalOutcomes = totalOutcomes;
        this.totalInvestments = totalInvestments;
        this.periodBalance = periodBalance;
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getTotalIncomes() {
        return totalIncomes;
    }

    public void setTotalIncomes(double totalIncomes) {
        this.totalIncomes = totalIncomes;
    }

    public double getTotalOutcomes() {
        return totalOutcomes;
    }

    public void setTotalOutcomes(double totalOutcomes) {
        this.totalOutcomes = totalOutcomes;
    }

    public double getTotalInvestments() {
        return totalInvestments;
    }

    public void setTotalInvestments(double totalInvestments) {
        this.totalInvestments = totalInvestments;
    }

    public double getPeriodBalance() {
        return periodBalance;
    }

    public void setPeriodBalance(double periodBalance) {
        this.periodBalance = periodBalance;
    }
}

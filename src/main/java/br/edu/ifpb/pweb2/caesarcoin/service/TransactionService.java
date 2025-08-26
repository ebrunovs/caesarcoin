package br.edu.ifpb.pweb2.caesarcoin.service;

import br.edu.ifpb.pweb2.caesarcoin.model.*;
import br.edu.ifpb.pweb2.caesarcoin.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TransactionService implements Service<Transaction, Integer> {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<Transaction> findAll(){
        return transactionRepository.findAll();
    }

    @Override
    public Page<Transaction> findAll(Pageable p) {
        return transactionRepository.findAll(p);
    }

    public void deleteById(Integer id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public Transaction findById(Integer id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> findByAccountAndDateBetween(Account account, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByAccountAndDateBetween(account, startDate, endDate);
    }

    public ExtractData generateExtract(Account account, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = findByAccountAndDateBetween(account, startDate, endDate);

        double totalIncomes = transactions.stream()
            .filter(t -> "ENTRADA".equals(t.getType().toString()))
            .mapToDouble(Transaction::getValue)
            .sum();

        double totalOutcomes = transactions.stream()
            .filter(t -> "SAIDA".equals(t.getType().toString()))
            .mapToDouble(Transaction::getValue)
            .sum();

        double totalInvestments = transactions.stream()
            .filter(t -> "INVESTIMENTO".equals(t.getType().toString()))
            .mapToDouble(Transaction::getValue)
            .sum();

        double periodBalance = totalIncomes - totalOutcomes;

        return new ExtractData(transactions, startDate, endDate, totalIncomes, totalOutcomes, totalInvestments, periodBalance);
    }

    public ExtractData generateExtractWithDefaultDates(Account account, String startDateStr, String endDateStr) {
        LocalDate startDate;
        LocalDate endDate;

        if (startDateStr != null && !startDateStr.isEmpty()) {
            startDate = LocalDate.parse(startDateStr);
        } else {
            startDate = LocalDate.now().withDayOfMonth(1);
        }

        if (endDateStr != null && !endDateStr.isEmpty()) {
            endDate = LocalDate.parse(endDateStr);
        } else {
            endDate = LocalDate.now();
        }

        return generateExtract(account, startDate, endDate);
    }

    public List<AnnualCategoryBudget> generateAnnualBudget(Account account, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<Transaction> transactions = findByAccountAndDateBetween(account, start, end);

        Map<Integer, AnnualCategoryBudget> map = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            if (t.getCategory() == null || t.getDate() == null) continue;
            Category cat = t.getCategory();
            AnnualCategoryBudget row = map.computeIfAbsent(cat.getId(), k -> new AnnualCategoryBudget(cat));
            row.addValue(t.getDate().getMonthValue(), t.getValue() != null ? t.getValue() : 0d);
        }

        return map.values().stream()
                .sorted((a, b) -> {
                    int typeOrderA = typeOrder(a.getCategory().getKind());
                    int typeOrderB = typeOrder(b.getCategory().getKind());
                    if (typeOrderA != typeOrderB) return Integer.compare(typeOrderA, typeOrderB);
                    Integer ordA = a.getCategory().getOrd();
                    Integer ordB = b.getCategory().getOrd();
                    if (ordA == null && ordB == null) return a.getCategory().getName().compareToIgnoreCase(b.getCategory().getName());
                    if (ordA == null) return 1;
                    if (ordB == null) return -1;
                    int cmp = ordA.compareTo(ordB);
                    if (cmp != 0) return cmp;
                    return a.getCategory().getName().compareToIgnoreCase(b.getCategory().getName());
                })
                .collect(Collectors.toList());
    }

    private int typeOrder(TransactionType type) {
        if (type == null) return 99;
        return switch (type) {
            case ENTRADA -> 1;
            case SAIDA -> 2;
            case INVESTIMENTO -> 3;
        };
    }
}


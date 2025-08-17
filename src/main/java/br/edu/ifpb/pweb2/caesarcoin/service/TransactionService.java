package br.edu.ifpb.pweb2.caesarcoin.service;

import br.edu.ifpb.pweb2.caesarcoin.model.Account;
import br.edu.ifpb.pweb2.caesarcoin.model.ExtractData;
import br.edu.ifpb.pweb2.caesarcoin.model.Transaction;
import br.edu.ifpb.pweb2.caesarcoin.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TransactionService implements Service<Transaction, Integer> {

    @Autowired
    private TransactionRepository transactionRepository;


    @Override
    public List<Transaction> findAll(){
        return transactionRepository.findAll();
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


}

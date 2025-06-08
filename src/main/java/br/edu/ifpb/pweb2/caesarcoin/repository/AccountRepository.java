package br.edu.ifpb.pweb2.caesarcoin.repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.caesarcoin.model.Account;

@Component
public class AccountRepository {
    private Map<Integer, Account> repository = new HashMap<Integer, Account>();

    public Account findById(Integer id) {
        return repository.get(id);
    }

    public Account save(Account Account) {
        Integer id = null;
        id = (Account.getId() == null) ? this.getMaxId() : Account.getId();
        Account.setId(id);
        repository.put(id, Account);
        return Account;
    }

    public List<Account> findAll() {
        List<Account> Accounts = repository.values().stream().collect(Collectors.toList());
        return Accounts;
    }

    public Integer getMaxId() {
        List<Account> Accounts = findAll();
        if (Accounts == null || Accounts.isEmpty())
            return 1;
        Account AccountMaxId = Accounts
                .stream()
                .max(Comparator.comparing(Account::getId))
                .orElseThrow(NoSuchElementException::new);
        return AccountMaxId.getId() == null ? 1 : AccountMaxId.getId() + 1;
    }

}

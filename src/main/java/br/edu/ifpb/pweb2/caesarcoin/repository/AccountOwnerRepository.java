package br.edu.ifpb.pweb2.caesarcoin.repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;

public class AccountOwnerRepository {
    private Map<Integer, AccountOwner> repository = new HashMap<Integer, AccountOwner>();

    public AccountOwner findById(Integer id){
        return repository.get(id);
    }

    public AccountOwner save(AccountOwner accOwner){
        Integer id = null;
        id = (accOwner.getId() == null) ? this.getMaxId() : accOwner.getId();
        accOwner.setId(id);
        repository.put(id, accOwner);
        return accOwner;
    }

    public List<AccountOwner> findAll(){
        List<AccountOwner> accsOwner = repository.values().stream().collect(Collectors.toList());
        return accsOwner;
    }

    private Integer getMaxId() {
        List<AccountOwner> accs = findAll();
        if(accs == null || accs.isEmpty())
            return 1;
        AccountOwner accMaxId = accs.stream().max(Comparator.comparing(AccountOwner::getId)).orElseThrow(NoSuchElementException::new);
        return accMaxId.getId() == null ? 1 : accMaxId.getId() + 1; 
    }
}

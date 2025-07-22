package br.edu.ifpb.pweb2.caesarcoin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountOwnerRepository extends JpaRepository<AccountOwner, Integer> {

    AccountOwner findByEmail(String email);

}

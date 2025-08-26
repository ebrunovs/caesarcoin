package br.edu.ifpb.pweb2.caesarcoin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.caesarcoin.model.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Authority.AuthorityId> {
    
    @Query("SELECT a FROM Authority a WHERE a.username.username = :username")
    List<Authority> findByUsernameUsername(@Param("username") String username);

}

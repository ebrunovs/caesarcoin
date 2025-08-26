package br.edu.ifpb.pweb2.caesarcoin.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String username;
    private String password;
    private Boolean enabled;
    private String email;
    private String name;
    @OneToMany(mappedBy = "username")
    @ToString.Exclude
    List<Authority> authorities;
}

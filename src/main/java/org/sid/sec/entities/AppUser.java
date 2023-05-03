package org.sid.sec.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    //en ecriture on peut prendre la valeur de la stocke
    //et quant on utilise les getters comme on va l'ignorer avec json ignore
    // au moment de la serialization
    //c'est une moyen de proteger le mot de passe pour ne soit pas serializer au format JSON
    //XML avec xml transit
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY )
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    //lAZY : si je charge un objet user a partir de la base de données il n'a pas
    // chargé les roles de cette utilisateur
    //EAGER: des que on charge l'utilisateur j'ai les roles de cette utilisateur
    //il est preferable de cree une collection avec arrayList
    private Collection<AppRole> appRoles = new ArrayList<>();
}

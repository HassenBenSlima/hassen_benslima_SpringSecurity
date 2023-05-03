package org.sid.sec.service;

import org.sid.sec.entities.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class UserDetailsServiceImpl implements
        UserDetailsService {

    private AccountService accountService;

    public UserDetailsServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * //quant une utilisateur s'affiche utilise moi cette method pour chercher l'utilisateur
     * //a partir de la couche service que je vais creer
     * //quant l'ulisatuer saisir son login et son mot de passe fait moi appelle a cette method
     *
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = accountService.loadUserByUsername(username);
        //pour le refactoring on va faire tous ca dans une class a part
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        appUser.getAppRoles().forEach(
                r -> authorities.add(new SimpleGrantedAuthority(r.getRoleName())));

        return new User(appUser.getUserName(), appUser.getPassword(), authorities);
    }
}

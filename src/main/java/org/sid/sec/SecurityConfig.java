package org.sid.sec;

import org.sid.sec.entities.AppUser;
import org.sid.sec.filter.JwtAuthenticationFilter;
import org.sid.sec.filter.JwtAuthorizationFilter;
import org.sid.sec.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collection;

//pour configuirer spring security on a besoin d'utiliser la class de configuiration
//WebSecurityConfigurerAdapter
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    //on va specifier quelles sont les utilisateurs qui ont la deroits
    //d'acceder'
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    //on va specifier les droit d'accées
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //ici on va desactiver le csrf
        //comme on dit a spring security ce n'est pas l'apple
        // de generer le synchronizer token et de placer dans la session
        //j'ai pas l'utiliser
        //si on utilise l'authentification statefull (utilisation des session)il faut l'essayer la protection csrf parce que il proteger l'application contre les ataque csrf
        //pour l'authentification stateless il faut desactiver  csrf car
        // csrf utilise les sessions et quant on utilise l'authentification stateless on n'utilise pas les sessions
        //la session n'est pas gerer coté serveur , il va etre gerer on utilisant un TOKEN c'est le JWT
        http.csrf().disable();
        //2 : on n'utilise pas les session gerer coté serveur// je le dois gerer on utilisant le JWT Token
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //tu desactive la protection contre les frames , ca c'est uniquement
        //car j'utilise h2
        http.headers().frameOptions().disable();
     /*   //on va specifier l'accer a toutes les fonctionnalité
        //toutes les requettes ne necessite pas une authentification
        http.authorizeRequests().anyRequest().permitAll();*/
        //activer un formuaire d'authentification
        //demander a spring security d'afficher un formulaire d'authentification (fournit par spring security) en cas
        //de besoin
        //http.formLogin();// on va utiliser notre propre front-end

        //autoriser des url sans authentification
        http.authorizeRequests().antMatchers("/h2-console/**", "/refreshToken/**", "/login/**").permitAll();
            /* 1ere solution
            //start il faut le faire avant  http.authorizeRequests().anyRequest().authenticated();
            //pour l'authorisation les roles il ya deux facon de faire soit on ajoute http
            http.authorizeRequests().antMatchers(HttpMethod.POST, "/users/**").hasAnyAuthority("ADMIN");
            http.authorizeRequests().antMatchers(HttpMethod.GET, "/users/**").hasAnyAuthority("USER");
            //END
            */

        //chaque request necessite une system d'authentification
        //il faut passer par un systeme d'authentification
        //il y a deux facon de le faire , il y a une authentification stateful et une authentification
        //stateless
        http.authorizeRequests().anyRequest().authenticated();


        //integrer le filter dans la configuration de spring security:
        http.addFilter(new JwtAuthenticationFilter(authenticationManagerBean()));
        //pour que le filter utiliser dans la configuiration
        //on peut specifier qulle est le filter qui commance le 1ere
        //c'est la notion du middleware(Spring il s'appele des Filter dans d'autre framework il s'appel des middleware)
        //je vais uliliser Before pour vous dire que c'est le 1ere filter qui va recevoir la requete  de type UsernamePasswordAuthenticationFilter
        http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    /**
     * dans WebSecurityConfigurerAdapter il y a cette method qui retourn un objet de type AuthenticationManager
     * et on a ajouter sur cette method l'annotation @Bean
     *
     * @return
     * @throws Exception
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    //Filter pour JWT
//1ere filter
//il y a un filter :JWT authentification filter  qui va etre utiliser au moment de l'authentification
// cad des que l'utilisateur va saisir son login et son mot de passe je vais le prendre et je vais generer
// un token

//2eme JWT AUTHORISATION FILER
//Des que l'utilisateur demande une resource
}

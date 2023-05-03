package org.sid.sec.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sid.sec.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 1ere Filter
 * qui va etre utiliser au moement de l'authentification
 * apres le saisi de login et mot de passe , je vais generer un token
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    /**
     * quant l'utilisateur va tempter de s'authentifier
     *
     * @param request  pour recuperer le login et le mot de passe
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication");
        String username = request.getParameter("userName");
        String password = request.getParameter("password");
        System.out.println(username);
        System.out.println(password);
        //on va les stocker dans un objet de Spring security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * quant l'authentification a reussi
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication");
        //authResult contient le resultat de l'authentification
        //il va vers la base de données il va chercher l'utilisateur qui contient username et ces roles

        User user = (User) authResult.getPrincipal();//permet de recupirer l'utilisateur courant authentifier
        //user contient username et les roles , ces ce qu'il faut pour generer un Token

        //on va generer le JWT
        //pour calculer la signature JWT on va utiliser un algorithme hmac ou ras
        //mySecret1234 clé privé qu'on ne dois pas partager
        Algorithm algo1 = Algorithm.HMAC256(JWTUtil.SECRET);
        String jwtAccessToken = JWT.create()
                .withSubject(user.getUsername()).withExpiresAt(new Date(System.currentTimeMillis() + JWTUtil.EXPIRE_ACCESS_TOKEN))
                .withIssuer(request.getRequestURI().toString())
                //getAuthority c'est le role ////pour chaque GrantedAuthority
                .withClaim("roles", user.getAuthorities().stream().map(ga -> ga.getAuthority()).collect(Collectors.toList()))
                //on va calculer la signature avec algo1
                .sign(algo1);


        String jwtRefreshToken = JWT.create()
                //on va mettre une expiration beaucoup plus long 15 minute par exemple(normalment 30 jours)
                .withSubject(user.getUsername()).withExpiresAt(new Date(System.currentTimeMillis() + JWTUtil.EXPIRE_REFRESH_TOKEN))
                .withIssuer(request.getRequestURI().toString())
                //on n'a pas besoin de role=> dans le refresh token on va mettre des chose qui concerne l'accée
                //on va calculer la signature avec algo1
                .sign(algo1);
        //si le token ne fait pas partir de back list je te genere un nouveau access token
        //on peut utiliser deux header 1 on va le stoker jwtAccessToken et l'autre jwtRefreshToken c'est pas pratique
        //donc on va envoyer jwtRefreshToken dans le core de la reponse
        Map<String, String> idToken = new HashMap<>();
        idToken.put("access-token", jwtAccessToken);
        idToken.put("refresh-token", jwtRefreshToken);
        //pour indique au client le core de la requete contient des données JSON
        //on va utiliser  response.setContentType("application/json");
        response.setContentType("application/json");
        //ON VA ENVOYER L'Objet au format JSON dans le core de la reponse
        //ON VA Utiliser JAXSON ObjectMapper QUI UTILISER PAR SPRING pour serialiser un objet au format JSON
        //on va ecrire une valeur sur une sorti (response.getOutputStream() )et je vais envoyer le "idToken"
        new ObjectMapper().writeValue(response.getOutputStream(), idToken);
//RESUME :quant je m'authentifier je veux avoir deux tokens et on dois utiliser access token pour acceder a l'application


/*
        //je peut envoyer ce jwt dans un header
        //header (nameOfHeader,jwtAccessToken)
        response.setHeader("Authorization",jwtAccessToken);*/
    }
}

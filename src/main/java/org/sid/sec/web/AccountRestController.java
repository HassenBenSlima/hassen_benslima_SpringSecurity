package org.sid.sec.web;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.sid.sec.JWTUtil;
import org.sid.sec.entities.AppRole;
import org.sid.sec.entities.AppUser;
import org.sid.sec.service.AccountService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AccountRestController {

    private AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping(path = "/users")
    @PostAuthorize("hasAuthority('USER')")
    public List<AppUser> appUser() {
        return accountService.listUsers();
    }

    @PostMapping(path = "/users")
    @PostAuthorize("hasAuthority('ADMIN')")
    public AppUser saveUser(@RequestBody AppUser appUser) {
        return accountService.addNewUser(appUser);
    }

    @PostMapping(path = "/roles")
    @PostAuthorize("hasAuthority('ADMIN')")
    public AppRole saveRole(@RequestBody AppRole appRole) {
        return accountService.addNewRole(appRole);
    }

    @PostMapping(path = "/addRoleToUser")
    public void addRoleToUser(@RequestBody RoleUserForm roleUserForm) {
        accountService.addRoleToUser(roleUserForm.getUserName(), roleUserForm.getRoleName());
    }

    //demander  une nouvelle access token
    @GetMapping(path = "/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String auhToken = request.getHeader(JWTUtil.AUTH_HEADER);
        if (auhToken != null && auhToken.startsWith(JWTUtil.PREFIX)) {
            try {
                String refreshToken = auhToken.substring(JWTUtil.PREFIX.length());
                Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET);
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);
                //si c'est valable je vais recupere la session de l'utilisateur
                //s'il n'y a pas de probleme je vais recuperer le user name
                String username = decodedJWT.getSubject();
                //         String[] roles = decodedJWT.getClaim("roles").asArray(String.class);// c'est un tableau de string
                //convertion roles vers GantedAuthority
                //       Collection<GrantedAuthority> authorities = new ArrayList<>();
//            for (String r : roles) {
//                authorities.add(new SimpleGrantedAuthority(r));
//            }

                //je peut verifier ici le black list
                AppUser appUser = accountService.loadUserByUsername(username);

                String jwtAccessToken = JWT.create()
                        .withSubject(appUser.getUserName())
                        .withExpiresAt(new Date(System.currentTimeMillis() + JWTUtil.EXPIRE_REFRESH_TOKEN))
                        .withIssuer(request.getRequestURI().toString())
                        //getAuthority c'est le role ////pour chaque GrantedAuthority
                        .withClaim("roles", appUser.getAppRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toList()))
                        //je vais signer  avec algorithm
                        .sign(algorithm);

                //maintenant je vais envoyer les deux tokens
                Map<String, String> idToken = new HashMap<>();
                idToken.put("access-token", jwtAccessToken);
                idToken.put("refresh-token", refreshToken);
                //pour indique au client le core de la requete contient des données JSON
                //on va utiliser  response.setContentType("application/json");
                response.setContentType("application/json");
                //ON VA ENVOYER L'Objet au format JSON dans le core de la reponse
                //ON VA Utiliser JAXSON ObjectMapper QUI UTILISER PAR SPRING pour serialiser un objet au format JSON
                //on va ecrire une valeur sur une sorti (response.getOutputStream() )et je vais envoyer le "idToken"
                new ObjectMapper().writeValue(response.getOutputStream(), idToken);

            } catch (Exception e) {
                //si le token est expirer il genere une exception expired..Exception
                /*response.setHeader("error-message", e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);*/
                //on peut aussi utiliser throw e
            }
        } else {
            throw new RuntimeException("Refresh token required!!!");
        }
    }

    //si vous voulez consulter l'utilisateur authentifier
    @GetMapping(path = "/profile")
    public AppUser profile(Principal principal) {
        return accountService.loadUserByUsername(principal.getName());
    }
}


@Data
class RoleUserForm {
    private String userName;
    private String roleName;
}

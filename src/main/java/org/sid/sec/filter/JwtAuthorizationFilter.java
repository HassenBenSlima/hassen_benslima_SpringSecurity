package org.sid.sec.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.sid.sec.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 2eme Filter
 * des que l'utilisateur demande une ressource je le verifie et a partir de ce tocken je recupere l'utilisateur et les role
 * on va ajouter dans la class SecurityConfig dans la method   protected void configure(HttpSecurity http) throws Exception  ce ligne :
 * <p>
 * //pour que le filter utiliser dans la configuiration
 * //on peut specifier qulle est le filter qui commance le 1ere
 * //c'est la notion du middleware(Spring il s'appele des Filter dans d'autre framework il s'appel des middleware)
 * //je vais uliliser Before pour vous dire que c'est le 1ere filter qui va recevoir la requete  de type UsernamePasswordAuthenticationFilter
 * <p>
 * <p>
 * http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    /**
     * chaque requete qui arrive je dois utiliser cette objet HttpServletRequest request
     * pour lire le header Authorization
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/refreshToken")) {
            filterChain.doFilter(request, response);
        } else {
            //a chaque fois qu'il y a une requete qui arrive la requete doit presenter le visa
            String authorizationToken = request.getHeader(JWTUtil.AUTH_HEADER);
            //il y a l'authotifiv=cation basic et l'authentification bearer qui contient un token c'est un prefixe qui est utilisé
            if (authorizationToken != null && authorizationToken.startsWith(JWTUtil.PREFIX)) {
                try {
                    //ignorer les 7 permier character de la chaine
                    String jwt = authorizationToken.substring(JWTUtil.PREFIX.length());
                    //singer le jwt//je dois verifier est cequ'il est valable
                    //JE DOIS UTILISER LE MEME Secret
                    Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET);
                    JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                    //si c'est valable je vais recupere la session de l'utilisateur
                    //s'il n'y a pas de probleme je vais recuperer le user name
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);// c'est un tableau de string
                    //convertion roles vers GantedAuthority
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    for (String r : roles) {
                        authorities.add(new SimpleGrantedAuthority(r));
                    }

                    //maintenant je vais authentifier cette utilisateur
                    //sans mot de passe null
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    //dans le context de spring security on va placer l'utilisateur pour voir est ce qu'il a le droit ou non de l'authentifier
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    //je passe pour le filter suivant (next filter)
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    //si le token est expirer il genere une exception expired..Exception
                    response.setHeader("error-message", e.getMessage());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }

            } else {
                //tu passe au suivant mais je connais pas , spring security filter il fait son travail apres, il va
                //verifier est ce que la ressource demander est ce qu'il nessecite une authentification
                filterChain.doFilter(request, response);
            }
        }

    }
}

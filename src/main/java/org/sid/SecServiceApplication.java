package org.sid;

import org.sid.sec.entities.AppRole;
import org.sid.sec.entities.AppUser;
import org.sid.sec.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
//prePostEnabled = true: cad je peut utiliser l'annotation post authorise
//securedEnabled = true : cad vous pouvez utiliser une autre annotation quis'appelle secured
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecServiceApplication.class, args);
    }

    //pour encoder le mot de passe on a besoin de password incoder
    //dans le context de l'application je veux creer un objet passwordIncoder

    @Bean
        //pour placer dans le context de l'application
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner start(AccountService accountService) {
        return args -> {
            accountService.addNewRole(new AppRole(null, "USER"));
            accountService.addNewRole(new AppRole(null, "ADMIN"));
            accountService.addNewRole(new AppRole(null, "CUSTOMER_MANAGER"));
            accountService.addNewRole(new AppRole(null, "PRODUCT_MANAGER"));
            accountService.addNewRole(new AppRole(null, "BILLS_MANAGER"));

            accountService.addNewUser(new AppUser(null, "user1", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "admin", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "user2", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "user3", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "user4", "1234", new ArrayList<>()));

            accountService.addRoleToUser("user1", "USER");
            accountService.addRoleToUser("admin", "USER");
            accountService.addRoleToUser("user2", "USER");
            accountService.addRoleToUser("user2", "CUSTOMER_MANAGER");
            accountService.addRoleToUser("user3", "USER");
            accountService.addRoleToUser("user3", "PRODUCT_MANAGER");

            accountService.addRoleToUser("user4", "USER");
            accountService.addRoleToUser("user4", "PRODUCT_MANAGER");

        };
    }

//    CommandLineRunner start2(AccountService accountService) {
//        return new CommandLineRunner() {
//            @Override
//            public void run(String... args) throws Exception {
//                accountService.addNewRole(new AppRole(null, "USER"));
//                accountService.addNewRole(new AppRole(null, "USER"));
//            }
//        };
//    }

}

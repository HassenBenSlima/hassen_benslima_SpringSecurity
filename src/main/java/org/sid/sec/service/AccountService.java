package org.sid.sec.service;

import org.sid.sec.entities.AppRole;
import org.sid.sec.entities.AppUser;

import java.util.List;

public interface AccountService {

    AppUser addNewUser(AppUser appUser);

    AppRole addNewRole(AppRole appRole);

    void addRoleToUser(String userName, String roleName);

    AppUser loadUserByUsername(String username);

    List<AppUser> listUsers();
}

package com.whatsapp.barbeariaWill.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.whatsapp.barbeariaWill.domain.model.User;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        switch (username) {
            case "willian":
                return new User(
                        "willian",
                        "$2a$10$9DvY4Uffiu6Dfl9BHmz.j.J1rxTJRzcvRJlv7zW72TBFK86jJB8O.",
                        "ADMIN");
            case "abner":
                return new User(
                        "abner",
                        "$2a$10$9DvY4Uffiu6Dfl9BHmz.j.J1rxTJRzcvRJlv7zW72TBFK86jJB8O.",
                        "FUNCIONARIO");
            default:
                throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
    }
} 
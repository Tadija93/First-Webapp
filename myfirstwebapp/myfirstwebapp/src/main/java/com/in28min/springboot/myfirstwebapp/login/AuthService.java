package com.in28min.springboot.myfirstwebapp.login;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public boolean authenticate(String username, String password) {

        boolean isValidUser = username.equalsIgnoreCase("aleks");
        boolean isValidPass = password.equalsIgnoreCase("test");
        return isValidUser && isValidPass;
    }
}

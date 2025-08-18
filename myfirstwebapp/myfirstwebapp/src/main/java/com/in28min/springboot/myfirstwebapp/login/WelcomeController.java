package com.in28min.springboot.myfirstwebapp.login;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("name")
public class WelcomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String goToWelcomePage(ModelMap model) {
        model.put("name", getLoggedinUserName());
        return "sayHello";
    }

    private String getLoggedinUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    //@RequestMapping(value = "login", method = RequestMethod.POST)
    //public String goToHelloPage(@RequestParam String name, @RequestParam String password, ModelMap model) {

    //  if (authService.authenticate(name, password)) {
    //    model.put("name", name);
    //  model.put("password", password);
    //return "sayHello";
    //}
    //model.put("errorMsg", "Invalid credentials");
    //return "login";
    //}
}

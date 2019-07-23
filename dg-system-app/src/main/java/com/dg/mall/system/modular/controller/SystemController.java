package com.dg.mall.system.modular.controller;


import com.dg.mall.model.api.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SystemController {


    @Autowired
    private AuthService authService;

    @PostMapping("login")
    public String login(@RequestParam("account") String account, @RequestParam("pasw") String pasw){
        authService.login(account, pasw);
        return "success";
    }
}

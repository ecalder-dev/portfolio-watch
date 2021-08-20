package com.portfoliowatch.controller;

import com.portfoliowatch.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/email")
@RestController
public class EmailController {

    @Autowired
    EmailService emailService;

    @GetMapping("")
    public ResponseEntity<String> email() {
        ResponseEntity<String> responseEntity;
        try {
            this.emailService.sendReport("the.morning.owl.speaks@gmail.com");
            responseEntity = new ResponseEntity<>("Email sent.", HttpStatus.OK);
        } catch (Exception e) {
            responseEntity = new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}

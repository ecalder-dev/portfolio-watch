package com.portfoliowatch.controller;

import com.portfoliowatch.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/email")
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

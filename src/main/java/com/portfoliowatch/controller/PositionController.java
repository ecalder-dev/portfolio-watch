package com.portfoliowatch.controller;

import com.portfoliowatch.model.Position;
import com.portfoliowatch.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class PositionController {

    @Autowired
    PositionService positionService;

    @GetMapping("/positions")
    public ResponseEntity<List<Position>> readAllPositions() {
        List<Position> data;
        HttpStatus httpStatus;
        try {
            data = positionService.readAllPositions();
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @PostMapping("/position")
    public ResponseEntity<Position> createPosition(@RequestBody Position position) {
        Position data;
        HttpStatus httpStatus;
        try {
            data = positionService.createPosition(position);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @PutMapping("/position")
    public ResponseEntity<Position> updatePosition(@RequestBody Position postion) {
        Position data;
         HttpStatus httpStatus;
        try {
            data = positionService.updatePosition(postion);
            if (data != null) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            data = null;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }

    @DeleteMapping("/position")
    public ResponseEntity<Boolean> deletePosition(@RequestBody Position position) {
        boolean data;
        HttpStatus httpStatus;
        try {
            data = positionService.deletePosition(position);
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            data = false;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(data, httpStatus);
    }
}

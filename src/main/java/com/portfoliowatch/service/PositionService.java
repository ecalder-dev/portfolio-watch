package com.portfoliowatch.service;

import com.portfoliowatch.model.Position;
import com.portfoliowatch.model.Transaction;
import com.portfoliowatch.repository.PositionRepository;
import com.portfoliowatch.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PositionService {

    @Autowired
    PositionRepository positionRepository;

    public List<Position> readAllPositions() {
        return positionRepository.findAll();
    }

    public Position createPosition(Position position) {
        position.setPositionId(null);
        position.setDateUpdated(new Date());
        return positionRepository.save(position);
    }

    public boolean deletePosition(Position position) {
        positionRepository.delete(position);
        return true;
    }

    public Position updatePosition(Position position) {
        position.setDateUpdated(new Date());
        return positionRepository.save(position);
    }
}

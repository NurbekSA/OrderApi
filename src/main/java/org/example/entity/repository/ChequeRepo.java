package org.example.entity.repository;

import org.example.entity.model.ChequeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChequeRepo extends JpaRepository<ChequeModel, String> {
    public Optional<ChequeModel> findByUserId(String id);
}

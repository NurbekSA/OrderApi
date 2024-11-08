package org.example.persistence.repository;

import org.example.persistence.model.entity.ChequeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChequeRepo extends JpaRepository<ChequeModel, String> {
    public Optional<ChequeModel> findByOrderId(String id);
}

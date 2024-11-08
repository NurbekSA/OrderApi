package org.example.persistence.service;

import lombok.RequiredArgsConstructor;
import org.example.persistence.model.dto.ChequeDTO;
import org.example.persistence.model.entity.ChequeModel;
import org.example.persistence.model.exception.ResourceNotFoundException;
import org.example.persistence.repository.ChequeRepo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChequeService {

    private final ChequeRepo chequeRepo;

    // Преобразование из ChequeModel в ChequeDTO
    private ChequeDTO convertToDto(ChequeModel chequeModel) {
        return new ChequeDTO(chequeModel.getId(), chequeModel.getOrderId(), chequeModel.getBody());
    }

    // Преобразование из ChequeDTO в ChequeModel
    private ChequeModel convertToEntity(ChequeDTO chequeDTO) {
        return new ChequeModel(chequeDTO.getId(), chequeDTO.getOrderId(), chequeDTO.getBody());
    }

    public ChequeDTO getByOrderId(String id) {
        ChequeModel chequeModel = chequeRepo.findByOrderId(id).orElseThrow(() -> new ResourceNotFoundException("Cheque not found"));
        return convertToDto(chequeModel);
    }

    public ChequeDTO create(ChequeDTO chequeDTO) {
        ChequeModel chequeModel = convertToEntity(chequeDTO);
        ChequeModel savedCheque = chequeRepo.save(chequeModel);
        if (savedCheque == null) {
            throw new ResourceNotFoundException("Failed to create cheque");
        }
        return convertToDto(savedCheque);
    }
}

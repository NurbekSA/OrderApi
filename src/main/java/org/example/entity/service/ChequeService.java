package org.example.entity.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.model.ChequeModel;
import org.example.entity.model.exception.ResourceNotFoundException;
import org.example.entity.repository.ChequeRepo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChequeService {
    ChequeRepo chequeRepo;

    public ChequeModel getByOrderId(String id){
        return chequeRepo.findByUserId(id).orElseThrow(() -> new ResourceNotFoundException("Cheque not found"));
    }
    public ChequeModel create(ChequeModel cheque){
        ChequeModel generateCheque = chequeRepo.save(cheque);
        if(generateCheque == null) new ResourceNotFoundException("Filed to creat cheque");
        return generateCheque;
    }
}

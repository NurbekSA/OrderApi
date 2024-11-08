package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.persistence.model.dto.ChequeDTO;
import org.example.persistence.model.entity.ChequeModel;
import org.example.persistence.service.ChequeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cheque")
@RequiredArgsConstructor
public class ChequeController {
    ChequeService chequeService;
    @GetMapping("order/{id}")
    public ChequeDTO getByOrderId(@PathVariable String id){
        return chequeService.getByOrderId(id);
    }

    @PutMapping
    public ChequeDTO create(@RequestBody ChequeDTO chequeDTO){
        return chequeService.create(chequeDTO);
    }
}

package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.model.ChequeModel;
import org.example.entity.service.ChequeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cheque")
@RequiredArgsConstructor
public class ChequeController {
    ChequeService chequeService;
    @GetMapping("order/{id}")
    public ChequeModel getByOrderId(@PathVariable String id){
        return chequeService.getByOrderId(id);
    }

    @PutMapping
    public ChequeModel create(@RequestBody ChequeModel chequeModel){
        return chequeService.create(chequeModel);
    }
}

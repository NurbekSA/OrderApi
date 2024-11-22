package amanzat.com.controller;

import amanzat.com.persistence.service.ChequeService;
import lombok.RequiredArgsConstructor;
import amanzat.com.persistence.model.dto.ChequeDTO;
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

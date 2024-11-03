package org.example.entity.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class OrderModel {
    @Id
    private String id;
    private String userId;
    private String boxId;
    private Boolean isPaid; // (оплачено/не оплачено)
    private Boolean isAlive; // Логическое удаление
    private Long bookingDateTime;
    private int rentalPeriod;
    private Double summ;
    private String itemCategory;

}

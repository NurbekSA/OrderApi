package amanzat.com.persistence.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {

    @Id
    @Column(nullable = false, unique = true) // Идентификатор должен быть уникальным
    private String id;

    @Column(nullable = false)
    @NotBlank(message = "Идентификатор пользователя не должен быть пустым")
    private String userId;

    @Column(nullable = false)
    @NotBlank(message = "Идентификатор ячейки не должен быть пустым")
    private String boxId;

    @Column(nullable = false)
    @NotBlank(message = "Статус заказа обязателен")
    private String status;

    @Column(nullable = false)
    @NotNull(message = "Дата бронирования не может быть null")
    private Long bookingDateTime; // Используется Instant вместо Long

    @Column(nullable = false)
    @NotNull(message = "Срок аренды обязателен")
    private int rentalPeriod;

    @Column(nullable = false)
    @Min(value = 0, message = "Сумма заказа должна быть неотрицательной")
    private Double orderAmount;

}

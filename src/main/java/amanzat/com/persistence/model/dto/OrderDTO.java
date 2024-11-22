package amanzat.com.persistence.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import amanzat.com.exception.Transfer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    @Null(groups = {Transfer.Create.class})
    @NotNull(groups = {Transfer.Extend.class})
    private String id;

    @NotNull(groups = {Transfer.Create.class})
    @Null(groups = {Transfer.Extend.class})
    private String userId;

    @NotNull(groups = {Transfer.Create.class})
    @Null(groups = {Transfer.Extend.class})
    private String boxId;

    @Null(groups = {Transfer.Create.class, Transfer.Extend.class})
    private Status status;

    @Null(groups = {Transfer.Create.class, Transfer.Extend.class})
    private Long bookingDateTime;

    @NotNull(groups = {Transfer.Create.class, Transfer.Extend.class})
    @Max(value = 48, message = "Rental period cannot exceed 48 hours")
    private int rentalPeriod;

    @Null(groups = {Transfer.Create.class, Transfer.Extend.class})
    private Double orderAmount;

}

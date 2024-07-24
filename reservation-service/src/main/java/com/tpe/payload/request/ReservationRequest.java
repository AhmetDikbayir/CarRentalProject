package com.tpe.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpe.domain.Car;
import com.tpe.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.transform.CacheableResultTransformer;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationRequest {

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startReservationDateTime;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endReservationDateTime;

    //todo user httpreuest den mi alınacak?

//    @NotNull
//    private Long userId;

    @NotNull
    private User user;

//    @NotNull
//    private Long carId;

    @NotNull
    private Car car;
}

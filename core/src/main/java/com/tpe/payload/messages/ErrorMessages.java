package com.tpe.payload.messages;


public class ErrorMessages {


    private ErrorMessages() {
    }

    public static final String CAR_EXISTS_BY_THIS_NUMBERPLATE = "Error: Car is already exists with this number plate %s";
    public static final String CAR_DOES_NOT_EXISTS_BY_ID= "Error: Car is not exists by id: %s";

    public static final String LOG_NOT_CREATED = "Error: Log is not created.";

    public static final String RESERVATION_NOT_AVAILABLE = "Car with ID %d is not available for the specified time range";


}

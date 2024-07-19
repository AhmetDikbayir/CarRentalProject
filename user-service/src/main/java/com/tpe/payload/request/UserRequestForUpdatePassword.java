package com.tpe.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestForUpdatePassword {


    @NotNull
    private String password;

    private Boolean builtIn = false;
}

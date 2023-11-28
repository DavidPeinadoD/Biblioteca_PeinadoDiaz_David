package io.bootify.my_app.model;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LibroReparar {

    @Valid
    private LibroDTO libro;

}

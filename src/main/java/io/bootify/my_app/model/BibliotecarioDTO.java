package io.bootify.my_app.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BibliotecarioDTO {

    private Long id;

    @Size(max = 255)
    private String nombre;

    @Size(max = 255)
    private String apellidos;

    @Size(max = 255)
    private String parteManeja;

    private Long bibliotecario;

    private Long usuarioBibliotecario;

}

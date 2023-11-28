package io.bootify.my_app.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LibroDTO {

    private Long id;

    @Size(max = 255)
    private String nombre;

    @Size(max = 255)
    private String autor;

    @Size(max = 255)
    private String genero;

    private Long numTotal;

    private Long numDisponible;

    private Boolean disponibilidad;

    private EstadoLibro estado;

    private Long libro;

    private Long libroReparador;

}

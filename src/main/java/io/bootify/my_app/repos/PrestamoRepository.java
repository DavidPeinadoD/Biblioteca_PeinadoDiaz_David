package io.bootify.my_app.repos;

import io.bootify.my_app.domain.Bibliotecario;
import io.bootify.my_app.domain.Lector;
import io.bootify.my_app.domain.Libro;
import io.bootify.my_app.domain.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    Prestamo findFirstByPrestamo(Libro libro);

    Prestamo findFirstByPrestamoLibro(Lector lector);

    Prestamo findFirstByPrestamoBibliotecario(Bibliotecario bibliotecario);

}

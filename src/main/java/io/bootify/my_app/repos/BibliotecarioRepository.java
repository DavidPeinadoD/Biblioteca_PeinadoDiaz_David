package io.bootify.my_app.repos;

import io.bootify.my_app.domain.Bibliotecario;
import io.bootify.my_app.domain.Lector;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BibliotecarioRepository extends JpaRepository<Bibliotecario, Long> {

    Bibliotecario findFirstByBibliotecario(Lector lector);

}

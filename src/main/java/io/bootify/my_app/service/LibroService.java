package io.bootify.my_app.service;

import io.bootify.my_app.domain.Lector;
import io.bootify.my_app.domain.Libro;
import io.bootify.my_app.domain.Prestamo;
import io.bootify.my_app.domain.Reparador;
import io.bootify.my_app.model.LibroDTO;
import io.bootify.my_app.repos.LectorRepository;
import io.bootify.my_app.repos.LibroRepository;
import io.bootify.my_app.repos.PrestamoRepository;
import io.bootify.my_app.repos.ReparadorRepository;
import io.bootify.my_app.util.NotFoundException;
import io.bootify.my_app.util.WebUtils;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final LectorRepository lectorRepository;
    private final ReparadorRepository reparadorRepository;
    private final PrestamoRepository prestamoRepository;

    public LibroService(final LibroRepository libroRepository,
                        final LectorRepository lectorRepository, final ReparadorRepository reparadorRepository,
                        final PrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.lectorRepository = lectorRepository;
        this.reparadorRepository = reparadorRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public List<LibroDTO> findAll() {
        final List<Libro> libroes = libroRepository.findAll(Sort.by("id"));
        return libroes.stream()
                .map(libro -> mapToDTO(libro, new LibroDTO()))
                .toList();
    }

    public LibroDTO get(final Long id) {
        return libroRepository.findById(id)
                .map(libro -> mapToDTO(libro, new LibroDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final LibroDTO libroDTO) {
        validateNonNullFields(libroDTO);

        final Libro libro = new Libro();
        mapToEntity(libroDTO, libro);
        return libroRepository.save(libro).getId();
    }

    public void update(final Long id, final LibroDTO libroDTO) {
        validateNonNullFields(libroDTO);

        final Libro libro = libroRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(libroDTO, libro);
        libroRepository.save(libro);
    }

    public void delete(final Long id) {
        libroRepository.deleteById(id);
    }

    private LibroDTO mapToDTO(final Libro libro, final LibroDTO libroDTO) {
        libroDTO.setId(libro.getId());
        libroDTO.setNombre(libro.getNombre());
        libroDTO.setAutor(libro.getAutor());
        libroDTO.setGenero(libro.getGenero());
        libroDTO.setNumTotal(libro.getNumTotal());
        libroDTO.setNumDisponible(libro.getNumDisponible());
        libroDTO.setDisponibilidad(libro.getDisponibilidad());
        libroDTO.setEstado(libro.getEstado());
        libroDTO.setLibro(libro.getLibro() == null ? null : libro.getLibro().getId());
        libroDTO.setLibroReparador(libro.getLibroReparador() == null ? null : libro.getLibroReparador().getId());
        return libroDTO;
    }

    private Libro mapToEntity(final LibroDTO libroDTO, final Libro libro) {
        libro.setNombre(libroDTO.getNombre());
        libro.setAutor(libroDTO.getAutor());
        libro.setGenero(libroDTO.getGenero());
        libro.setNumTotal(libroDTO.getNumTotal());
        libro.setNumDisponible(libroDTO.getNumDisponible());
        libro.setDisponibilidad(libroDTO.getDisponibilidad());
        libro.setEstado(libroDTO.getEstado());
        final Lector lector = libroDTO.getLibro() == null ? null : lectorRepository.findById(libroDTO.getLibro())
                .orElseThrow(() -> new NotFoundException("libro not found"));
        libro.setLibro(lector);
        final Reparador libroReparador = libroDTO.getLibroReparador() == null ? null : reparadorRepository.findById(libroDTO.getLibroReparador())
                .orElseThrow(() -> new NotFoundException("libroReparador not found"));
        libro.setLibroReparador(libroReparador);
        return libro;
    }

    public String getReferencedWarning(final Long id) {
        final Libro libro = libroRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Prestamo prestamoPrestamo = prestamoRepository.findFirstByPrestamo(libro);
        if (prestamoPrestamo != null) {
            return WebUtils.getMessage("libro.prestamo.prestamo.referenced", prestamoPrestamo.getId());
        }
        return null;
    }
    private void validateNonNullFields(LibroDTO libroDTO) {
        if (libroDTO.getNombre() == null
                || libroDTO.getAutor() == null
                || libroDTO.getNumTotal() == null
                || libroDTO.getNumDisponible() == null
                || libroDTO.getDisponibilidad() == null
                || libroDTO.getNumTotal()<libroDTO.getNumDisponible()
        ) {
            throw new IllegalArgumentException("Los campos nombre, autor, numTotal, numDisponible y disponibilidad no pueden ser nulos." + "\n" + " El numero disponible debe ser como mucho igual al total.");
        }
    }

}
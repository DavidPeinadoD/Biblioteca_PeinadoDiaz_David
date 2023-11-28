package io.bootify.my_app.service;

import io.bootify.my_app.domain.Bibliotecario;
import io.bootify.my_app.domain.Lector;
import io.bootify.my_app.domain.Libro;
import io.bootify.my_app.domain.Prestamo;
import io.bootify.my_app.model.PrestamoDTO;
import io.bootify.my_app.repos.BibliotecarioRepository;
import io.bootify.my_app.repos.LectorRepository;
import io.bootify.my_app.repos.LibroRepository;
import io.bootify.my_app.repos.PrestamoRepository;
import io.bootify.my_app.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final LectorRepository lectorRepository;
    private final BibliotecarioRepository bibliotecarioRepository;

    public PrestamoService(final PrestamoRepository prestamoRepository,
            final LibroRepository libroRepository, final LectorRepository lectorRepository,
            final BibliotecarioRepository bibliotecarioRepository) {
        this.prestamoRepository = prestamoRepository;
        this.libroRepository = libroRepository;
        this.lectorRepository = lectorRepository;
        this.bibliotecarioRepository = bibliotecarioRepository;
    }

    public List<PrestamoDTO> findAll() {
        final List<Prestamo> prestamoes = prestamoRepository.findAll(Sort.by("id"));
        return prestamoes.stream()
                .map(prestamo -> mapToDTO(prestamo, new PrestamoDTO()))
                .toList();
    }

    public PrestamoDTO get(final Long id) {
        return prestamoRepository.findById(id)
                .map(prestamo -> mapToDTO(prestamo, new PrestamoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PrestamoDTO prestamoDTO) {
        final Prestamo prestamo = new Prestamo();
        mapToEntity(prestamoDTO, prestamo);
        return prestamoRepository.save(prestamo).getId();
    }

    public void update(final Long id, final PrestamoDTO prestamoDTO) {
        final Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(prestamoDTO, prestamo);
        prestamoRepository.save(prestamo);
    }

    public void delete(final Long id) {
        prestamoRepository.deleteById(id);
    }

    private PrestamoDTO mapToDTO(final Prestamo prestamo, final PrestamoDTO prestamoDTO) {
        prestamoDTO.setId(prestamo.getId());
        prestamoDTO.setNombre(prestamo.getNombre());
        prestamoDTO.setFechaPrestamo(prestamo.getFechaPrestamo());
        prestamoDTO.setFechaDevolucion(prestamo.getFechaDevolucion());
        prestamoDTO.setPrestamo(prestamo.getPrestamo() == null ? null : prestamo.getPrestamo().getId());
        prestamoDTO.setPrestamoLibro(prestamo.getPrestamoLibro() == null ? null : prestamo.getPrestamoLibro().getId());
        prestamoDTO.setPrestamoBibliotecario(prestamo.getPrestamoBibliotecario() == null ? null : prestamo.getPrestamoBibliotecario().getId());
        return prestamoDTO;
    }

    private Prestamo mapToEntity(final PrestamoDTO prestamoDTO, final Prestamo prestamo) {
        prestamo.setNombre(prestamoDTO.getNombre());
        prestamo.setFechaPrestamo(prestamoDTO.getFechaPrestamo());
        prestamo.setFechaDevolucion(prestamoDTO.getFechaDevolucion());
        final Libro libro = prestamoDTO.getPrestamo() == null ? null : libroRepository.findById(prestamoDTO.getPrestamo())
                .orElseThrow(() -> new NotFoundException("prestamo not found"));
        prestamo.setPrestamo(libro);
        final Lector prestamoLibro = prestamoDTO.getPrestamoLibro() == null ? null : lectorRepository.findById(prestamoDTO.getPrestamoLibro())
                .orElseThrow(() -> new NotFoundException("prestamoLibro not found"));
        prestamo.setPrestamoLibro(prestamoLibro);
        final Bibliotecario prestamoBibliotecario = prestamoDTO.getPrestamoBibliotecario() == null ? null : bibliotecarioRepository.findById(prestamoDTO.getPrestamoBibliotecario())
                .orElseThrow(() -> new NotFoundException("prestamoBibliotecario not found"));
        prestamo.setPrestamoBibliotecario(prestamoBibliotecario);
        return prestamo;
    }

}

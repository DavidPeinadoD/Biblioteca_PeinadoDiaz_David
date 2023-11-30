package io.bootify.my_app.service;

import io.bootify.my_app.domain.Bibliotecario;
import io.bootify.my_app.domain.Lector;
import io.bootify.my_app.domain.Libro;
import io.bootify.my_app.domain.Prestamo;
import io.bootify.my_app.domain.Usuario;
import io.bootify.my_app.model.LectorDTO;
import io.bootify.my_app.repos.BibliotecarioRepository;
import io.bootify.my_app.repos.LectorRepository;
import io.bootify.my_app.repos.LibroRepository;
import io.bootify.my_app.repos.PrestamoRepository;
import io.bootify.my_app.repos.UsuarioRepository;
import io.bootify.my_app.util.NotFoundException;
import io.bootify.my_app.util.WebUtils;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class LectorService {

    private final LectorRepository lectorRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;
    private final BibliotecarioRepository bibliotecarioRepository;

    public LectorService(final LectorRepository lectorRepository,
            final UsuarioRepository usuarioRepository, final LibroRepository libroRepository,
            final PrestamoRepository prestamoRepository,
            final BibliotecarioRepository bibliotecarioRepository) {
        this.lectorRepository = lectorRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
        this.bibliotecarioRepository = bibliotecarioRepository;
    }

    public List<LectorDTO> findAll() {
        final List<Lector> lectors = lectorRepository.findAll(Sort.by("id"));
        return lectors.stream()
                .map(lector -> mapToDTO(lector, new LectorDTO()))
                .toList();
    }

    public LectorDTO get(final Long id) {
        return lectorRepository.findById(id)
                .map(lector -> mapToDTO(lector, new LectorDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final LectorDTO lectorDTO) {
        validateNonNullFields(lectorDTO);

        final Lector lector = new Lector();
        mapToEntity(lectorDTO, lector);
        return lectorRepository.save(lector).getId();
    }

    public void update(final Long id, final LectorDTO lectorDTO) {
        validateNonNullFields(lectorDTO);
        final Lector lector = lectorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(lectorDTO, lector);
        lectorRepository.save(lector);
    }

    public void delete(final Long id) {
        lectorRepository.deleteById(id);
    }

    private LectorDTO mapToDTO(final Lector lector, final LectorDTO lectorDTO) {
        lectorDTO.setId(lector.getId());
        lectorDTO.setNombre(lector.getNombre());
        lectorDTO.setApellidos(lector.getApellidos());
        lectorDTO.setLibro(lector.getLibro());
        lectorDTO.setUsuarioLector(lector.getUsuarioLector() == null ? null : lector.getUsuarioLector().getId());
        return lectorDTO;
    }

    private Lector mapToEntity(final LectorDTO lectorDTO, final Lector lector) {
        lector.setNombre(lectorDTO.getNombre());
        lector.setApellidos(lectorDTO.getApellidos());
        lector.setLibro(lectorDTO.getLibro());
        final Usuario usuarioLector = lectorDTO.getUsuarioLector() == null ? null : usuarioRepository.findById(lectorDTO.getUsuarioLector())
                .orElseThrow(() -> new NotFoundException("usuarioLector not found"));
        lector.setUsuarioLector(usuarioLector);
        return lector;
    }

    public String getReferencedWarning(final Long id) {
        final Lector lector = lectorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Libro libroLibro = libroRepository.findFirstByLibro(lector);
        if (libroLibro != null) {
            return WebUtils.getMessage("lector.libro.libro.referenced", libroLibro.getId());
        }
        final Prestamo prestamoLibroPrestamo = prestamoRepository.findFirstByPrestamoLibro(lector);
        if (prestamoLibroPrestamo != null) {
            return WebUtils.getMessage("lector.prestamo.prestamoLibro.referenced", prestamoLibroPrestamo.getId());
        }
        final Bibliotecario bibliotecarioBibliotecario = bibliotecarioRepository.findFirstByBibliotecario(lector);
        if (bibliotecarioBibliotecario != null) {
            return WebUtils.getMessage("lector.bibliotecario.bibliotecario.referenced", bibliotecarioBibliotecario.getId());
        }
        return null;
    }
    private void validateNonNullFields(final LectorDTO lectorDTO) {
        if (lectorDTO.getNombre() == null || lectorDTO.getApellidos()==null){
            throw new IllegalArgumentException("Los campos nombre y apellidos no pueden estar vacios.");
        }
    }

}

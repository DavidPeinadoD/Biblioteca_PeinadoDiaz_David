package io.bootify.my_app.service;

import io.bootify.my_app.domain.Bibliotecario;
import io.bootify.my_app.domain.Lector;
import io.bootify.my_app.domain.Prestamo;
import io.bootify.my_app.domain.Usuario;
import io.bootify.my_app.model.BibliotecarioDTO;
import io.bootify.my_app.repos.BibliotecarioRepository;
import io.bootify.my_app.repos.LectorRepository;
import io.bootify.my_app.repos.PrestamoRepository;
import io.bootify.my_app.repos.UsuarioRepository;
import io.bootify.my_app.util.NotFoundException;
import io.bootify.my_app.util.WebUtils;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class BibliotecarioService {

    private final BibliotecarioRepository bibliotecarioRepository;
    private final LectorRepository lectorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoRepository prestamoRepository;

    public BibliotecarioService(final BibliotecarioRepository bibliotecarioRepository,
            final LectorRepository lectorRepository, final UsuarioRepository usuarioRepository,
            final PrestamoRepository prestamoRepository) {
        this.bibliotecarioRepository = bibliotecarioRepository;
        this.lectorRepository = lectorRepository;
        this.usuarioRepository = usuarioRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public List<BibliotecarioDTO> findAll() {
        final List<Bibliotecario> bibliotecarios = bibliotecarioRepository.findAll(Sort.by("id"));
        return bibliotecarios.stream()
                .map(bibliotecario -> mapToDTO(bibliotecario, new BibliotecarioDTO()))
                .toList();
    }

    public BibliotecarioDTO get(final Long id) {
        return bibliotecarioRepository.findById(id)
                .map(bibliotecario -> mapToDTO(bibliotecario, new BibliotecarioDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final BibliotecarioDTO bibliotecarioDTO) {
        final Bibliotecario bibliotecario = new Bibliotecario();
        mapToEntity(bibliotecarioDTO, bibliotecario);
        return bibliotecarioRepository.save(bibliotecario).getId();
    }

    public void update(final Long id, final BibliotecarioDTO bibliotecarioDTO) {
        final Bibliotecario bibliotecario = bibliotecarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(bibliotecarioDTO, bibliotecario);
        bibliotecarioRepository.save(bibliotecario);
    }

    public void delete(final Long id) {
        bibliotecarioRepository.deleteById(id);
    }

    private BibliotecarioDTO mapToDTO(final Bibliotecario bibliotecario,
            final BibliotecarioDTO bibliotecarioDTO) {
        bibliotecarioDTO.setId(bibliotecario.getId());
        bibliotecarioDTO.setNombre(bibliotecario.getNombre());
        bibliotecarioDTO.setApellidos(bibliotecario.getApellidos());
        bibliotecarioDTO.setParteManeja(bibliotecario.getParteManeja());
        bibliotecarioDTO.setBibliotecario(bibliotecario.getBibliotecario() == null ? null : bibliotecario.getBibliotecario().getId());
        bibliotecarioDTO.setUsuarioBibliotecario(bibliotecario.getUsuarioBibliotecario() == null ? null : bibliotecario.getUsuarioBibliotecario().getId());
        return bibliotecarioDTO;
    }

    private Bibliotecario mapToEntity(final BibliotecarioDTO bibliotecarioDTO,
            final Bibliotecario bibliotecario) {
        bibliotecario.setNombre(bibliotecarioDTO.getNombre());
        bibliotecario.setApellidos(bibliotecarioDTO.getApellidos());
        bibliotecario.setParteManeja(bibliotecarioDTO.getParteManeja());
        final Lector lector = bibliotecarioDTO.getBibliotecario() == null ? null : lectorRepository.findById(bibliotecarioDTO.getBibliotecario())
                .orElseThrow(() -> new NotFoundException("bibliotecario not found"));
        bibliotecario.setBibliotecario(lector);
        final Usuario usuarioBibliotecario = bibliotecarioDTO.getUsuarioBibliotecario() == null ? null : usuarioRepository.findById(bibliotecarioDTO.getUsuarioBibliotecario())
                .orElseThrow(() -> new NotFoundException("usuarioBibliotecario not found"));
        bibliotecario.setUsuarioBibliotecario(usuarioBibliotecario);
        return bibliotecario;
    }

    public String getReferencedWarning(final Long id) {
        final Bibliotecario bibliotecario = bibliotecarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Prestamo prestamoBibliotecarioPrestamo = prestamoRepository.findFirstByPrestamoBibliotecario(bibliotecario);
        if (prestamoBibliotecarioPrestamo != null) {
            return WebUtils.getMessage("bibliotecario.prestamo.prestamoBibliotecario.referenced", prestamoBibliotecarioPrestamo.getId());
        }
        return null;
    }

}

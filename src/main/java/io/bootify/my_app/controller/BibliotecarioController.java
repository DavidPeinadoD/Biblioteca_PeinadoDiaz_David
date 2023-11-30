package io.bootify.my_app.controller;

import io.bootify.my_app.domain.Lector;
import io.bootify.my_app.domain.Usuario;
import io.bootify.my_app.model.BibliotecarioDTO;
import io.bootify.my_app.repos.LectorRepository;
import io.bootify.my_app.repos.UsuarioRepository;
import io.bootify.my_app.service.BibliotecarioService;
import io.bootify.my_app.util.CustomCollectors;
import io.bootify.my_app.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/bibliotecarios")
public class BibliotecarioController {

    private final BibliotecarioService bibliotecarioService;
    private final LectorRepository lectorRepository;
    private final UsuarioRepository usuarioRepository;

    public BibliotecarioController(final BibliotecarioService bibliotecarioService,
            final LectorRepository lectorRepository, final UsuarioRepository usuarioRepository) {
        this.bibliotecarioService = bibliotecarioService;
        this.lectorRepository = lectorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
         model.addAttribute("usuarioBibliotecarioValues", usuarioRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getId, Usuario::getNombre)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("bibliotecarios", bibliotecarioService.findAll());
        return "bibliotecario/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("bibliotecario") final BibliotecarioDTO bibliotecarioDTO) {
        return "bibliotecario/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("bibliotecario") @Valid final BibliotecarioDTO bibliotecarioDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "bibliotecario/add";
        }try{
            bibliotecarioService.create(bibliotecarioDTO);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("bibliotecario.create.success"));
            return "redirect:/bibliotecarios";
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, e.getMessage());
            return "redirect:/bibliotecarios/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("bibliotecario", bibliotecarioService.get(id));
        return "bibliotecario/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("bibliotecario") @Valid final BibliotecarioDTO bibliotecarioDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "bibliotecario/edit";
        }
        try {
            bibliotecarioService.update(id, bibliotecarioDTO);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lector.update.success"));
            return "redirect:/bibliotecarios";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, e.getMessage());
            return "redirect:/bibliotecarios/edit/{id}";
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final String referencedWarning = bibliotecarioService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, referencedWarning);
        } else {
            bibliotecarioService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("bibliotecario.delete.success"));
        }
        return "redirect:/bibliotecarios";
    }

}

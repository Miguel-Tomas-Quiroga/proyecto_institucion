package ar.edu.unju.fi.controlador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ar.edu.unju.fi.modelo.Alumno;
import ar.edu.unju.fi.repositorio.AlumnoRepositorio;

@Controller
public class AlumnoControlador {

    private static String UPLOADED_FOLDER = "uploads/";

    @Autowired
    private AlumnoRepositorio alumnoRepositorio;

    @GetMapping({"/",""})
    public String verPaginaDeInicio(Model modelo) {
        List<Alumno> alumnos = alumnoRepositorio.findAll();
        modelo.addAttribute("alumnos", alumnos);
        return "index";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioDeRegistrarAlumno(Model modelo) {
        modelo.addAttribute("alumno", new Alumno());
        return "nuevo";
    }

    @PostMapping("/nuevo")
    public String guardarAlumno(@Validated Alumno alumno, BindingResult bindingResult,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirect, Model modelo) {
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("alumno", alumno);
            return "nuevo";
        }

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
                Files.write(path, bytes);
                alumno.setImagenUrl(path.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        alumnoRepositorio.save(alumno);
        redirect.addFlashAttribute("msgExito", "El alumno ha sido agregado con éxito");
        return "redirect:/";
    }

    @GetMapping("/{dni}/editar")
    public String mostrarFormularioDeEditarAlumno(@PathVariable Integer dni, Model modelo) {
        Alumno alumno = alumnoRepositorio.getById(dni);
        modelo.addAttribute("alumno", alumno);
        return "nuevo";
    }

    @PostMapping("/{dni}/editar")
    public String actualizarAlumno(@PathVariable Integer dni, @Validated Alumno alumno, BindingResult bindingResult,
                                   @RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirect, Model modelo) {
        Alumno alumnoDB = alumnoRepositorio.getById(dni);
        if (bindingResult.hasErrors()) {
            modelo.addAttribute("alumno", alumno);
            return "nuevo";
        }

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
                Files.write(path, bytes);
                alumnoDB.setImagenUrl(path.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        alumnoDB.setNombre(alumno.getNombre());
        alumnoDB.setApellido(alumno.getApellido());
        alumnoDB.setDireccion(alumno.getDireccion());
        alumnoDB.setCelular(alumno.getCelular());
        alumnoDB.setEmail(alumno.getEmail());
        alumnoDB.setFechaNacimiento(alumno.getFechaNacimiento());

        alumnoRepositorio.save(alumnoDB);
        redirect.addFlashAttribute("msgExito", "El alumno ha sido actualizado correctamente");
        return "redirect:/";
    }

    @PostMapping("/{dni}/eliminar")
    public String eliminarAlumno(@PathVariable Integer dni, RedirectAttributes redirect) {
        Alumno alumno = alumnoRepositorio.getById(dni);
        alumnoRepositorio.delete(alumno);
        redirect.addFlashAttribute("msgExito", "El alumno ha sido eliminado correctamente");
        return "redirect:/";
    }
}

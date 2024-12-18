package proyecto.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import proyecto.backend.dto.UserDetailDto;
import proyecto.backend.dto.UserDto;
import proyecto.backend.entity.User;
import proyecto.backend.repository.UserRepository;
import proyecto.backend.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manage")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Página de inicio de sesión
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Página de acceso restringido
    @GetMapping("/restricted")
    public String restricted() {
        return "restricted";
    }

    // Cargar formulario para agregar usuario solo si el usuario tiene rol ADMIN
    @GetMapping("/add")
    public String addUser(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> loggedUser = userRepository.findByUsername(username);

        if (loggedUser.isPresent()) {
            String role = loggedUser.get().getRole();
            // Imprimir el rol del usuario en la consola para depuración
            System.out.println("Rol del usuario autenticado: " + role); // Verificar el rol en la consola
            if ("ADMIN".equals(role)) {
                model.addAttribute("userDetailDto", new UserDetailDto(null, "", "", "", "", "", "", null, null));
                return "manage-add";  // Vista para agregar usuario
            } else {
                return "redirect:/manage/start";  // Redirigir a la página principal si no es ADMIN
            }
        } else {
            return "redirect:/manage/restricted";  // Si el usuario no existe
        }
    }

    // Procesar el formulario para agregar usuario
    @PostMapping("/add")
    public String addUser(@ModelAttribute UserDetailDto userDetailDto, Model model) {
        try {
            // Llamamos al método addUser del servicio UserService
            boolean isAdded = userService.addUser(userDetailDto);

            if (isAdded) {
                model.addAttribute("success", "Usuario registrado correctamente.");
            } else {
                model.addAttribute("error", "Ocurrió un error al agregar el usuario.");
            }
        } catch (Exception e) {
            // Capturamos la excepción y mostramos el error
            e.printStackTrace();
            model.addAttribute("error", "Ocurrió un error al agregar el usuario.");
        }

        return "redirect:/manage/start";  // Redirigir al listado de usuarios
    }


    // Mostrar lista de usuarios solo para ADMIN y OPERATOR
    @GetMapping("/start")
    public String start(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> loggedUser = userRepository.findByUsername(username);

        if (loggedUser.isPresent()) {
            String role = loggedUser.get().getRole();
            // Imprimir el rol del usuario en la consola para depuración
            System.out.println("Rol del usuario autenticado: " + role); // Verificar el rol en la consola
            if ("ADMIN".equals(role) || "OPERATOR".equals(role)) {
                try {
                    List<UserDto> users = userService.getAllUsers();
                    model.addAttribute("users", users);
                    model.addAttribute("error", null);
                } catch (Exception e) {
                    e.printStackTrace();
                    model.addAttribute("error", "Ocurrió un error al obtener los datos.");
                }
                return "manage";  // Vista para mostrar la lista de usuarios
            } else {
                return "redirect:/manage/restricted";  // Redirigir si el usuario no tiene acceso
            }
        } else {
            return "redirect:/manage/restricted";  // Redirigir si no se encuentra el usuario
        }
    }

    // Mostrar detalle de un usuario
    @GetMapping("/detail/{id}")
    public String getUserById(@PathVariable("id") int id, Model model) {
        try {
            Optional<UserDetailDto> user = userService.getUserById(id);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
                model.addAttribute("error", null);
            } else {
                model.addAttribute("error", "No se encontró el usuario con ID: " + id);
                return "user-not-found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ocurrió un error al obtener los detalles del usuario.");
            return "error";
        }
        return "user-detail";
    }

    // Eliminar un usuario (solo ADMIN)
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, Model model) {
        // Obtener el nombre de usuario autenticado
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Buscar el usuario autenticado en la base de datos usando el nombre de usuario
        Optional<User> loggedUser = userRepository.findByUsername(username);

        if (loggedUser.isPresent()) {
            String role = loggedUser.get().getRole();
            System.out.println("Rol del usuario autenticado: " + role);

            // Verificar si el usuario tiene el rol 'ADMIN'
            if ("ADMIN".equals(role)) {
                try {
                    boolean result = userService.deleteUserById(id);
                    if (result) {
                        model.addAttribute("success", "Usuario eliminado exitosamente.");
                    } else {
                        model.addAttribute("error", "No se encontró el usuario con ID: " + id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    model.addAttribute("error", "Ocurrió un error al eliminar el usuario.");
                }
            } else {
                model.addAttribute("error", "No tienes permiso para eliminar usuarios.");
                return "redirect:/manage/restricted";
            }
        } else {
            model.addAttribute("error", "Usuario no encontrado.");
        }

        // Redirigir a la página de inicio
        return "redirect:/manage/start";
    }



    // Cargar formulario para actualizar usuario (solo si el usuario tiene rol ADMIN)
    @GetMapping("/update/{id}")
    public String updateUserForm(@PathVariable("id") int id, Model model) {
        try {
            // Obtener el nombre de usuario autenticado
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> loggedUser = userRepository.findByUsername(username);

            if (loggedUser.isPresent()) {
                String role = loggedUser.get().getRole();
                // Imprimir el rol del usuario en la consola para depuración
                System.out.println("Rol del usuario autenticado: " + role);

                if ("ADMIN".equals(role)) {
                    // Llamamos al método del servicio para obtener los detalles del usuario
                    Optional<UserDetailDto> user = userService.getUserById(id);
                    if (user.isPresent()) {
                        model.addAttribute("userDetailDto", user.get());
                        return "user-update";  // Vista para actualizar el usuario
                    } else {
                        model.addAttribute("error", "Usuario no encontrado.");
                        return "redirect:/manage/start";
                    }
                } else {
                    return "redirect:/manage/restricted";
                }
            } else {
                return "redirect:/manage/restricted";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Ocurrió un error al cargar los detalles del usuario.");
            return "error";
        }
    }

    // Procesar formulario para actualizar usuario
    @PostMapping("/update")
    public String updateUser(@ModelAttribute UserDetailDto userDetailDto, RedirectAttributes redirectAttributes) {
        try {
            boolean isUpdated = userService.updateUser(userDetailDto);
            if (isUpdated) {
                redirectAttributes.addFlashAttribute("success", "Usuario actualizado exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Ocurrió un error al actualizar el usuario.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al actualizar el usuario.");
        }
        return "redirect:/manage/start";
    }

}
package proyecto.backend.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.backend.dto.UserDetailDto;
import proyecto.backend.dto.UserDto;
import proyecto.backend.entity.User;
import proyecto.backend.repository.UserRepository;
import proyecto.backend.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() throws Exception {
        List<UserDto> users = new ArrayList<>();
        Iterable<User> iterable = userRepository.findAll();
        iterable.forEach(user -> users.add(new UserDto(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail())));
        return users;
    }

    @Override
    public Optional<UserDetailDto> getUserById(int id) throws Exception {
        Optional<User> optional = userRepository.findById(id);
        return optional.map(user -> new UserDetailDto(user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getCreated(),
                user.getUpdated()));
    }

    @Override
    public boolean updateUser(UserDetailDto userDto) throws Exception {
        Optional<User> optional = userRepository.findById(userDto.id());
        return optional.map(user -> {
            user.setFirstName(userDto.firstName());
            user.setLastName(userDto.lastName());
            user.setEmail(userDto.email());
            user.setRole(userDto.role());
            user.setUpdated(new Date());
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional
    public boolean deleteUserById(int id) throws Exception {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            User user = optional.get();
            userRepository.delete(user);
            return true;
        } else {
            // Lanzar una excepción si el usuario no se encuentra, para manejarlo adecuadamente en el controlador
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }


    @Override
    public boolean addUser(UserDetailDto userDetailDto) throws Exception {
        // Verificar si el nombre de usuario ya existe
        Optional<User> existingUser = userRepository.findByUsername(userDetailDto.username());
        if (existingUser.isPresent()) {
            // Si ya existe, no se agrega
            return false;
        }

        // Crear un nuevo objeto User para agregarlo a la base de datos
        User newUser = new User();
        newUser.setUsername(userDetailDto.username());

        // Cifrar la contraseña antes de guardarla
        String encryptedPassword = new BCryptPasswordEncoder().encode(userDetailDto.password());
        newUser.setPassword(encryptedPassword);

        // Establecer los demás campos
        newUser.setEmail(userDetailDto.email());
        newUser.setFirstName(userDetailDto.firstName());
        newUser.setLastName(userDetailDto.lastName());
        newUser.setRole(userDetailDto.role());
        newUser.setCreated(new java.util.Date());  // Fecha de creación

        // Guardar el usuario (el ID se genera automáticamente)
        userRepository.save(newUser);

        // Retornar true si la operación fue exitosa
        return true;
    }
}

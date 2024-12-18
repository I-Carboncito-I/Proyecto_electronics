package proyecto.backend.service;

import proyecto.backend.dto.UserDetailDto;
import proyecto.backend.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDto> getAllUsers() throws Exception;

    Optional<UserDetailDto> getUserById(int id) throws Exception;

    boolean updateUser(UserDetailDto userDto) throws Exception;

    boolean deleteUserById(int id) throws Exception;

    boolean addUser(UserDetailDto userDetailDto) throws Exception;

}

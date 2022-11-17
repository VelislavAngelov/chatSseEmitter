package com.example.chatSSE.service;

import com.example.chatSSE.dto.UserDTO;
import com.example.chatSSE.model.User;
import com.example.chatSSE.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * A Class that helps us validate and manipulate the data returned by the repositories
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final EmitterService emitterService;

    public UserService(UserRepository userRepository, EmitterService emitterService) {
        this.userRepository = userRepository;
        this.emitterService = emitterService;
    }

    /**
     * We validate the data and then we return the UserDTO
     */
    public String registration(User user) {
        UserDTO dto = new UserDTO(user.getUserName());

        if (!findByUsername(user.getUserName())) {
            userRepository.registration(user);

            return dto.getUserName() + " has registered completely";
        }
        return "Username already exists!";
    }

    /**
     * When the given user logouts, then we unsubscribe them from their emitter
     */
    public String setUserStatus(UserDetails userDetails) {
        if (findByUsername(userDetails.getUsername())) {

            userRepository.setUserStatus(userDetails.getUsername());
            if(emitterService.getSseSessions().containsKey(userDetails.getUsername())) {
                emitterService
                        .getSseSessions()
                        .get(userDetails.getUsername())
                        .complete();
            }
            return userDetails.getUsername() + " has logged out successfully";
        }
        return "Invalid username!";
    }

    /**
     * If the database contains the given username then we return true
     */
    public boolean findByUsername(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            User user = userRepository.findByUsername(username).get();

            if (user.getUserName().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Here we return a list of users
     */
    public List<UserDTO> readAllUsers() {
        List<User> users = userRepository.readAllUsers();
        List<UserDTO> dtoList = new ArrayList<>();

        for (User user : users) {

            dtoList.add(new UserDTO(user.getUserName()));
        }
        return dtoList;
    }

    /**
     * Here we return information whether the user has been deleted or not
     */
    public String deleteById(Integer id) {
        int rows = userRepository.deleteById(id);
        if (rows != 0) {
            return "User deleted successfully";
        }
        return "User not found!";
    }
}
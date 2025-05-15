package com.task.service.creadential;

import com.task.dto.credentialDto.UserRequestDTO;
import com.task.entities.credential.Users;

public interface AuthService {
    public Users register(UserRequestDTO usersDto);

    public String login(Users users);


}

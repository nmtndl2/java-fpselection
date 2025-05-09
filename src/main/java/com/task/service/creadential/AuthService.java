package com.task.service.creadential;

import com.task.dto.credentialDto.UserRequestDTO;
import com.task.entities.credential.Users;

public interface AuthService {
    public Users Register(UserRequestDTO usersDto);

    public String Login(Users users);


}

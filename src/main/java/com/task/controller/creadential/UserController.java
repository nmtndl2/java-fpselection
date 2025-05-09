package com.task.controller.creadential;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/get")
    public String userGet() {
        return "API of User Get";
    }

    @PostMapping("/post")
    public String userPost() {
        return "API of User Post";
    }

    @PutMapping ("/put")
    public String userPut() {
        return "API of User Put";
    }

    @DeleteMapping("/delete")
    public String userDelete() {
        return "API of User Delete";
    }
}

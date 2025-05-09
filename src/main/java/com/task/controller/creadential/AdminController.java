package com.task.controller.creadential;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/get")
    public String userGet() {
        return "API of Admin Get";
    }

    @PostMapping("/post")
    public String userPost() {
        return "API of Admin Post";
    }

    @PutMapping ("/put")
    public String userPut() {
        return "API of Admin Put";
    }

    @DeleteMapping("/delete")
    public String userDelete() {
        return "API of Admin Delete";
    }

}

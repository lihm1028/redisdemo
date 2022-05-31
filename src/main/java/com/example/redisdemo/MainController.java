package com.example.redisdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/demo")
public class MainController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private BookRepository bookRepository;

    @PostMapping("/add")
    public @ResponseBody
    User addUser(@RequestParam String name, @RequestParam String email) {
        User n = new User();
        n.setName(name);
        n.setEmail(email);
        User save = userRepository.save(n);
        return save;
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }


    @PostMapping("/book/add")
    public @ResponseBody
    Book addBook(@RequestParam String name, @RequestParam String email) {
        Book n = new Book();
        n.setName(name);
        n.setEmail(email);
        Book save = bookRepository.save(n);
        return save;
    }
}

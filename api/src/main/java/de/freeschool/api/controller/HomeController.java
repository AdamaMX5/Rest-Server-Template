package de.freeschool.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    /**
     * Always redirect any sub-URL to index.html as the Svelte frontend handles them in JavaScript
     */
    @GetMapping(value = {"/", "/{path:[^\\.]*}"})
    public String redirect() {
        return "forward:/index.html";
    }

    @GetMapping(value = "/*/{path:[^\\.]+}")
    public String redirect2() {
        return "forward:/";
    }

}

package com.evacuation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebViewController {

    @GetMapping(value = {
            "/",
            "/dashboard",
            "/floorplan",
            "/scenarios",
            "/simulation",
            "/routes",
            "/risk",
            "/comparison",
            "/reports",
            "/login"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}

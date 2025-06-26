package com.agents.builder.main.controller;

import com.agents.builder.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/valid")
public class ValidController {


    @GetMapping("/application/{limit}")
    public R<?> application(){
        return R.ok(true);
    }


    @GetMapping("/dataset/{limit}")
    public R<?> dataset(){
        return R.ok(true);
    }


    @GetMapping("/user/{limit}")
    public R<?> user(){
        return R.ok(true);
    }
}

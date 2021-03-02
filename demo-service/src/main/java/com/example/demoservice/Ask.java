package com.example.demoservice;

import org.springframework.web.bind.annotation.*;

@RestController
public class Ask {

    @RequestMapping(value="ask", method = RequestMethod.GET)
    public @ResponseBody String getQuestion(@RequestParam("question") String question){
        return "You asked \"" + question + "\", but our programmers still working on it";
    }


}

package be.isservers.be.audiosyncserver;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
public class AudiosyncserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(AudiosyncserverApplication.class, args);
    }

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

    @RequestMapping(value="/music/{id}", method=RequestMethod.GET)
    public String listeProduits(@PathVariable int id) {
        return "Vous avez demandé la musique avec l'id  " + id;
    }

}

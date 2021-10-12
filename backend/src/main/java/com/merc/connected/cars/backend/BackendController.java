package com.merc.connected.cars.backend;

import com.merc.connected.cars.common.model.EncPersonData;
import com.merc.connected.cars.backend.svc.BackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BackendController {

    @Autowired
    private BackendService personService;

    @GetMapping(value = "/read")
    public EncPersonData[] read() {
        return personService.getAllEncPersons();
    }

}

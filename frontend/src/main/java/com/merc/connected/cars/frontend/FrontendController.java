package com.merc.connected.cars.frontend;

import com.merc.connected.cars.frontend.svc.FrontendService;
import com.merc.connected.cars.model.PersonModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RestController
public class FrontendController {

    @Autowired
    private FrontendService personService;

    @PostMapping("/store")
    public ResponseEntity<PersonModel> store(@Valid @RequestBody PersonModel personModel,
                                             @RequestParam("fileType") String fileType)
            throws ParseException {
        personService.store(personModel, fileType);
        return ResponseEntity.ok(personModel);
    }

    @PostMapping("/update")
    public ResponseEntity<PersonModel> update(@Valid @RequestBody PersonModel personModel,
                                              @RequestParam("fileType") String fileType)
            throws ParseException {
        personService.update(personModel, fileType);
        return ResponseEntity.ok(personModel);
    }

    @GetMapping("/read")
    public ResponseEntity<List<PersonModel>> read() {
        try {
            List<PersonModel> persons = personService.read();

            if (persons.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(persons);
            }
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

}

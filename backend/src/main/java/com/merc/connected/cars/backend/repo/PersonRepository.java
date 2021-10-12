package com.merc.connected.cars.backend.repo;

import com.merc.connected.cars.backend.model.PersonData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRepository extends CrudRepository<PersonData, String> {

    List<PersonData> findByName(String name);
}

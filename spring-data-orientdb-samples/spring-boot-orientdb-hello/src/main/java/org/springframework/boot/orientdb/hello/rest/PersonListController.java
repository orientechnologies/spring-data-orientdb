package org.springframework.boot.orientdb.hello.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orientdb.hello.data.Person;
import org.springframework.boot.orientdb.hello.repository.PersonRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by frank on 24/04/2017.
 */
@RestController
public class PersonListController {


    private static Logger log = LoggerFactory.getLogger(PersonListController.class);

    @Autowired
    private PersonRepository repository;

    @RequestMapping("/person/list")
    public List<Person> personList() throws JsonProcessingException {
        log.debug("Got request for person list");
        List<Person> persons = repository.findAll();

        for (Person person : persons) {
            log.debug("First Name: " + person.getFirstName());
            log.debug("Last Name: " + person.getLastName());
        }

        //ObjectMapper mapper = new ObjectMapper();
        //String jsonString = mapper.writeValueAsString(botRecords);

        //return jsonString;
        return persons;
    }

}

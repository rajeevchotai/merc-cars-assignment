package com.merc.connected.cars.backend.svc;

import com.merc.connected.cars.backend.config.ManageCsv;
import com.merc.connected.cars.backend.config.ManageXml;
import com.merc.connected.cars.common.model.EncPersonData;
import com.merc.connected.cars.backend.model.PersonData;
import com.merc.connected.cars.backend.repo.PersonRepository;
import com.merc.connected.cars.common.protobuf.AESEncryptionManager;
import com.merc.connected.cars.common.protobuf.DriverProtos.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BackendService {

    private static final String PERSON_QUEUE="person";

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AESEncryptionManager encryptionManager;

    @Value("${file.upload.csv}")
    private String csvFile;

    @Value("${file.upload.xml}")
    private String xmlFile;

    @Value("${spring.protobuf.encryption.key.password}")
    private String encryptionKeyPass;

    @JmsListener(destination = "${mq.queue-name}")
    public void receiveMessage(@Payload Person person,
                               @Header(name = "fileType") String fileType,
                               @Header(name = "command") String command) {
        PersonData personData = new PersonData();

        switch(command) {
            case "update":
                personData.setId(person.getId());
            case "store":
                personData.setName(person.getName());
                personData.setDob(person.getDob());
                personData.setAge(person.getAge());
                personData.setSalary(person.getSalary());
        }
        store(personData, fileType);
    }

    public EncPersonData[] getAllEncPersons()
    {
        List<EncPersonData> personData = new ArrayList<>();
        personRepository.findAll().forEach(
                p -> {
                    encryptPersonData(personData, p);
                });
        EncPersonData[] encPersonData = new EncPersonData[personData.size()];
        return personData.toArray(encPersonData);
    }

    private void encryptPersonData(List<EncPersonData> personData, PersonData p) {
        try {
            EncPersonData encrypted = new EncPersonData();
            encrypted.setId(encryptionManager.encryptInteger(encryptionKeyPass, p.getId()));
            encrypted.setName(encryptionManager.encryptString(encryptionKeyPass, p.getName()));
            encrypted.setDob(encryptionManager.encryptString(encryptionKeyPass, p.getDob()));
            encrypted.setAge(encryptionManager.encryptInteger(encryptionKeyPass, p.getAge()));
            encrypted.setSalary(encryptionManager.encryptDouble(encryptionKeyPass, p.getSalary()));
            personData.add(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void store(PersonData personData, String fileType){
        personRepository.save(personData);

        List<PersonData> personDataList = new ArrayList<>();
        personRepository.findAll().forEach(personDataList::add);

        switch (fileType) {
            case "CSV":
                ManageCsv.writeCsvFromBean(csvBeanPath(), personDataList);
                break;
            case "XML":
                ManageXml.writeXmlFromBean(xmlBeanPath(), personDataList);
        }
    }

    public Path csvBeanPath()  {
        return Paths.get(csvFile);
    }

    public Path xmlBeanPath() {
        return Paths.get(xmlFile);
    }
}


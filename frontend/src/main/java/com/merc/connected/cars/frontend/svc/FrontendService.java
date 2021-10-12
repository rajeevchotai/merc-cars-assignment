package com.merc.connected.cars.frontend.svc;

import com.merc.connected.cars.common.model.EncPersonData;
import com.merc.connected.cars.common.protobuf.AESEncryptionManager;
import com.merc.connected.cars.common.protobuf.DriverProtos.Person;
import com.merc.connected.cars.model.PersonModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FrontendService {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AESEncryptionManager encryptionManager;


    @Value("${backend.service.read.url}")
    private String backendUrl;

    @Value("${spring.protobuf.encryption.key.password}")
    private String encryptionKeyPass;

    private static final String PERSON_QUEUE="person";




    public List<PersonModel> read() throws Exception {
        ResponseEntity<EncPersonData[]> response = restTemplate.getForEntity(
                backendUrl, EncPersonData [].class);
        EncPersonData[] encPersonData = response.getBody();
        List<PersonModel> personModels = new ArrayList<>();

            for(EncPersonData endData: encPersonData){
                PersonModel personModel = new PersonModel();
                personModel.setId(encryptionManager.decryptInteger(encryptionKeyPass,endData.getId()));
                personModel.setName(encryptionManager.decryptString(encryptionKeyPass,endData.getName()));
                personModel.setDob(stringToDate(encryptionManager.decryptString(encryptionKeyPass,endData.getDob())));
                personModel.setAge(encryptionManager.decryptInteger(encryptionKeyPass,endData.getAge()));
                personModel.setSalary(encryptionManager.decryptDouble(encryptionKeyPass,endData.getSalary()));
                personModels.add(personModel);
            }

        return personModels;
    }


    public void store(PersonModel personModel, String fileType) {

        Person person = Person.newBuilder()
                .setName(personModel.getName())
                .setAge(personModel.getAge())
                .setSalary(personModel.getSalary())
                .setDob(dateToString(personModel.getDob()))
                .build();

        jmsTemplate.convertAndSend(PERSON_QUEUE, person , m -> {
            m.setStringProperty("fileType",fileType);
            m.setStringProperty("command","store");

            return m;
        });
    }

    private String dateToString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }

    private LocalDate stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    public void update(PersonModel personModel, String fileType) {

        Person person = Person.newBuilder()
                .setId(personModel.getId())
                .setName(personModel.getName())
                .setAge(personModel.getAge())
                .setSalary(personModel.getSalary())
                .setDob(dateToString(personModel.getDob()))
                .build();

        jmsTemplate.convertAndSend(PERSON_QUEUE, person , m -> {
            m.setStringProperty("fileType",fileType);
            m.setStringProperty("command","update");
            return m;
        });
    }

}

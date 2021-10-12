package com.merc.connected.cars.frontend.svc;

import com.merc.connected.cars.common.model.EncPersonData;
import com.merc.connected.cars.common.protobuf.AESEncryptionManager;
import com.merc.connected.cars.common.protobuf.DriverProtos;
import com.merc.connected.cars.model.PersonModel;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import javax.jms.Message;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {FrontendService.class, AESEncryptionManager.class})
@SpringBootTest
public class FrontendServiceTests {

    @MockBean
    private JmsTemplate jmsTemplate;

    @MockBean
    private RestTemplate restTemplate;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private Message message;

    @InjectMocks
    @Autowired
    private FrontendService frontendService;

    @Value("${backend.service.read.url}")
    private String backendUrl;

    @Value("${spring.protobuf.encryption.key.password}")
    private String encryptionKeyPass;

    @Test
    public void testReadEmptyList() throws Exception {
        EncPersonData[] encPersonArray = new EncPersonData[0];

        when(restTemplate.getForEntity(
                backendUrl, EncPersonData[].class)).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(encPersonArray);

        List<PersonModel> personModels = frontendService.read();
        Assert.assertTrue(personModels.isEmpty());

        verify(restTemplate, times(1)).getForEntity(backendUrl, EncPersonData[].class);
        verify(responseEntity, times(1)).getBody();
    }

    @Test
    public void testReadList() throws Exception {
        EncPersonData[] encPersonArray = new EncPersonData[1];
        EncPersonData encPersonData = new EncPersonData();
        encPersonData.setId(AESEncryptionManager.encryptInteger(encryptionKeyPass, 1));
        encPersonData.setName(AESEncryptionManager.encryptString(encryptionKeyPass, "hello"));
        encPersonData.setAge(AESEncryptionManager.encryptInteger(encryptionKeyPass, 20));
        encPersonData.setSalary(AESEncryptionManager.encryptDouble(encryptionKeyPass, 1.1));
        encPersonData.setDob(AESEncryptionManager.encryptString(encryptionKeyPass, "2021-10-10"));
        encPersonArray[0] = encPersonData;

        List<PersonModel> personModels = new ArrayList<>();
        PersonModel personModel = new PersonModel();
        personModel.setId(AESEncryptionManager.decryptInteger(encryptionKeyPass, encPersonData.getId()));
        personModel.setName(AESEncryptionManager.decryptString(encryptionKeyPass, encPersonData.getName()));
        personModel.setAge(AESEncryptionManager.decryptInteger(encryptionKeyPass, encPersonData.getAge()));
        personModel.setSalary(AESEncryptionManager.decryptDouble(encryptionKeyPass, encPersonData.getSalary()));
        personModel.setDob(stringToDate(AESEncryptionManager.decryptString(encryptionKeyPass, encPersonData.getDob())));
        personModels.add(personModel);

        when(restTemplate.getForEntity(
                backendUrl, EncPersonData[].class)).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(encPersonArray);

        List<PersonModel> personModelReturn = frontendService.read();
        Assert.assertEquals(personModelReturn, personModels);

        verify(restTemplate, times(1)).getForEntity(backendUrl, EncPersonData[].class);
        verify(responseEntity, times(1)).getBody();
    }

    @Test
    public void  testStore() {
        PersonModel personModel = new PersonModel();
        personModel.setId(1);
        personModel.setName("hello");
        personModel.setAge(20);
        personModel.setSalary(1.1);
        personModel.setDob(stringToDate("2021-10-10"));

        DriverProtos.Person person = DriverProtos.Person.newBuilder()
                .setName("hello")
                .setAge(20)
                .setSalary(1.1)
                .setDob("2021-10-10").build();


        frontendService.store(personModel,"CSV");
        verify(jmsTemplate).convertAndSend(eq("person"), eq(person), any());
    }

    @Test
    public void  testUpdate() throws Exception{
        PersonModel personModel = new PersonModel();
        personModel.setId(1);
        personModel.setName("hello");
        personModel.setAge(20);
        personModel.setSalary(1.1);
        personModel.setDob(stringToDate("2021-10-10"));

        DriverProtos.Person person = DriverProtos.Person.newBuilder()
                .setId(1)
                .setName("hello")
                .setAge(20)
                .setSalary(1.1)
                .setDob("2021-10-10").build();


        frontendService.update(personModel,"CSV");
        verify(jmsTemplate).convertAndSend(eq("person"), eq(person), any());

    }

    private LocalDate stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }
}

package com.merc.connected.cars.backend.config;

import com.merc.connected.cars.backend.model.PersonData;
import com.merc.connected.cars.backend.model.PersonDatas;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ManageXml {

    public static void writeXmlFromBean(Path path, List<PersonData> personDataList) {
        try {
            File file = path.toFile();
            JAXBContext jaxbContext = JAXBContext.newInstance(PersonDatas.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            PersonDatas personDatas = new PersonDatas();
            personDatas.setPersonDataList(new ArrayList<>());
            personDataList.forEach(personDatas.getPersonDataList()::add);

            jaxbMarshaller.marshal(personDatas, file);


        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
    }

    public static PersonDatas readFromXml(Path path) {
        PersonDatas personData = new PersonDatas();
        try {
            File file = path.toFile();
            JAXBContext jaxbContext = JAXBContext.newInstance(PersonDatas.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            personData = (PersonDatas) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException ex) {
            ex.printStackTrace();
        }
        return personData;
    }
}

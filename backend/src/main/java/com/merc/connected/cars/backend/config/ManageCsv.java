package com.merc.connected.cars.backend.config;

import com.merc.connected.cars.backend.model.PersonData;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ManageCsv {

    public static List<PersonData> readFromCsv(Path path, Class clazz) {
        ColumnPositionMappingStrategy ms = new ColumnPositionMappingStrategy();
        return readFromCsv(path, clazz, ms);
    }

    public static List<PersonData> readFromCsv(Path path, Class clazz, MappingStrategy ms) {
        List<PersonData> personData = new ArrayList<>();
            ms.setType(clazz);

        try {
            Reader reader = Files.newBufferedReader(path);

            CsvToBean cb = new CsvToBeanBuilder(reader).withType(clazz)
                    .withMappingStrategy(ms)
                    .build();

            personData = cb.parse();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return personData;
    }

    public static void writeCsvFromBean(Path path, List<PersonData> personDataList) {
        try {
            Writer writer = new FileWriter(path.toString());

            StatefulBeanToCsv sbc = new StatefulBeanToCsvBuilder(writer).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .build();

            sbc.write(personDataList);
            writer.close();

        } catch (IOException |
                CsvRequiredFieldEmptyException |
                CsvDataTypeMismatchException ex) {
            ex.printStackTrace();
        }
    }
}

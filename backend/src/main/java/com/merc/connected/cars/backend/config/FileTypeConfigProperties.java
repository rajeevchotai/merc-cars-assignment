package com.merc.connected.cars.backend.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="file")
@Data
@NoArgsConstructor
public class FileTypeConfigProperties {

    private String csv;
    private String xml;
}

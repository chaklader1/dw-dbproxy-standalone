package com.drivewealth.dbproxy.mapper;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceMapper {

    public RequestMapper getServices(final String action, final String tableName) throws IOException, URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ObjectReader tableMapperObjectReader = mapper.reader(RequestMapper.class);

        String fileName = tableName.toLowerCase() + "_" + action.toLowerCase() + ".json";
        System.out.println("getServices FileName" + fileName);

        final String resFile = "mappingfiles/" + fileName;
        File file = ResourceUtils.getFile(resFile);

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resFile);
        final String s = IOUtils.toString(is);

//        String content = new String(Files.readAllBytes(file.toPath()));

        return tableMapperObjectReader.readValue(s);
    }
}

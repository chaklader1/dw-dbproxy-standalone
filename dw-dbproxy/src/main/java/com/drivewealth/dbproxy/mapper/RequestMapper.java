package com.drivewealth.dbproxy.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestMapper {
    @Getter
    @Setter
    @JsonProperty("DynamoDBTableName")
    public String dynamoDBTableName;
    @Getter
    @Setter
    @JsonProperty("DynamoDBAction")
    public String dynamoDBAction;

    @Getter
    @Setter
    @JsonProperty("GRPCMapper")
    public List<GRPCMapper> gRPCMapper;


    @Getter
    @Setter
    @JsonProperty("DDBGRPCMapper")
    public List<DDBGRPCMapper> dDBGRPCMapper;
}

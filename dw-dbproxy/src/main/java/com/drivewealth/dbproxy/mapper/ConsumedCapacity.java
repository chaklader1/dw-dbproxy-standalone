package com.drivewealth.dbproxy.mapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@lombok.ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumedCapacity {
    @Getter
    @Setter
    @JsonProperty("TableName")
    public String tableName;
    @Getter
    @Setter
    @JsonProperty("CapacityUnits")
    public double capacityUnits;
    @Getter
    @Setter
    @JsonProperty("ReadCapacityUnits")
    public int readCapacityUnits;
    @Getter
    @Setter
    @JsonProperty("WriteCapacityUnits")
    public int writeCapacityUnits;

    @Getter
    @Setter
    @JsonProperty("Table")
    public String table;

    @Getter
    @Setter
    @JsonProperty("LocalSecondaryIndexes")
    public String localSecondaryIndexes;


    @Getter
    @Setter
    @JsonProperty("GlobalSecondaryIndexes")
    public String globalSecondaryIndexes;

}

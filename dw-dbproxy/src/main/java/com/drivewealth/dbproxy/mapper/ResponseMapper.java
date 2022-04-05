package com.drivewealth.dbproxy.mapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@lombok.ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMapper {
    @Getter
    @Setter
    @JsonProperty("sdkResponseMetadata")
    public SdkResponseMetadata sdkResponseMetadata;

    @Getter
    @Setter
    @JsonProperty("sdkHttpMetadata")
    public SdkHttpMetadata sdkHttpMetadata;

    @Getter
    @Setter
    @JsonProperty("consumedCapacity")
    public ConsumedCapacity consumedCapacity;


    @Getter
    @Setter
    @JsonProperty("itemCollectionMetrics")
    public ItemCollectionMetrics itemCollectionMetrics;
}

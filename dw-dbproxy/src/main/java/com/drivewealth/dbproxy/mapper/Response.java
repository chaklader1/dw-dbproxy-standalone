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

// TODO: insert the fields for the item, attributes and itemCollectionMetrics
public class Response {

    @Getter
    @Setter
    @JsonProperty("SdkResponseMetadata")
    public SdkResponseMetadata sdkResponseMetadata;

    @Getter
    @Setter
    @JsonProperty("SdkHttpMetadata")
    public SdkHttpMetadata sdkHttpMetadata;


    @Getter
    @Setter
    @JsonProperty("ConsumedCapacity")
    public ConsumedCapacity consumedCapacity;


//    @Getter
//    @Setter
//    @JsonProperty("ItemCollectionMetrics")
//    public ItemCollectionMetrics itemCollectionMetrics;

    @Getter
    @Setter
    @JsonProperty("item")
    public Map<String, AttributeValue> item;
}

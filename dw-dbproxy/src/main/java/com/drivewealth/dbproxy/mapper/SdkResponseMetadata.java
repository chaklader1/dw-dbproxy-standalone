package com.drivewealth.dbproxy.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@lombok.ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SdkResponseMetadata {
    @Getter
    @Setter
    @JsonProperty("requestId")
    public String requestId;


    @Getter
    @Setter
    @JsonProperty("RequestId")
    public String requestId1;
}

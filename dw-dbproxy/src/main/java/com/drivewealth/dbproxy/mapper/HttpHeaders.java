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
public class HttpHeaders {
    @Getter
    @Setter
    @JsonProperty("Content-Length")
    public String contentLength;

    @Getter
    @Setter
    @JsonProperty("Content-Type")
    public String contentType;

    @Getter
    @Setter
    @JsonProperty("Date")
    public String date;

    @Getter
    @Setter
    @JsonProperty("Server")
    public String server1;

    @Getter
    @Setter
    @JsonProperty("x-amz-crc32")
    public String xAmzCrc32;

    @Getter
    @Setter
    @JsonProperty("x-amzn-RequestId")
    public String xAmznRequestId;
}

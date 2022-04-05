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
public class AllHttpHeaders {
    @Getter
    @Setter
    @JsonProperty("Server")
    public List<String> lServer;
    @Getter
    @Setter
    @JsonProperty("x-amzn-RequestId")
    public List<String> xAmznRequestId;
    @Getter
    @Setter
    @JsonProperty("x-amz-crc32")
    public List<String> xAmzCrc32;
    @Getter
    @Setter
    @JsonProperty("Content-Length")
    public List<String> contentLength;
    @Getter
    @Setter
    @JsonProperty("Date")
    public List<String> date;
    @Getter
    @Setter
    @JsonProperty("Content-Type")
    public List<String> contentType;
}

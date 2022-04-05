package com.drivewealth.dbproxy.mapper;

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
public class GRPCMapper {
    @Getter
    @Setter
    @JsonProperty("ServiceName")
    public String serviceName;

    @Getter
    @Setter
    @JsonProperty("ClassName")
    public String className;

    @Getter
    @Setter
    @JsonProperty("ProtoType")
    public String protoType;

    @Getter
    @Setter
    @JsonProperty("ImplementedClass")
    public String implementedClass;

    @Getter
    @Setter
    @JsonProperty("MethodType")
    public String methodType;
}

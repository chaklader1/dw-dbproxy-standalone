package com.drivewealth.dbproxy.mapper;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.*;

@lombok.ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeValue {

    @JsonProperty("S")
    private String s;
    @JsonProperty("N")
    private String n;
    @JsonProperty("B")
    private ByteBuffer b;
    @JsonProperty("SS")
    private List<String> sS;
    @JsonProperty("NS")
    private List<String> nS;
    @JsonProperty("BS")
    private List<ByteBuffer> bS;
    @JsonProperty("M")
    private Map<String, AttributeValue> m;
    @JsonProperty("L")
    private List<AttributeValue> l;
    @JsonProperty("NULLValue")
    private Boolean nULLValue;
    @JsonProperty("BOOL")
    private Boolean bOOL;

    public AttributeValue() {
    }

    public AttributeValue(String s) {
        this.setS(s);
    }

    public AttributeValue(List<String> sS) {
        this.setSS(sS);
    }

    public String getS() {
        return this.s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public AttributeValue withS(String s) {
        this.setS(s);
        return this;
    }

    public String getN() {
        return this.n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public AttributeValue withN(String n) {
        this.setN(n);
        return this;
    }

    public ByteBuffer getB() {
        return this.b;
    }

    public void setB(ByteBuffer b) {
        this.b = b;
    }

    public AttributeValue withB(ByteBuffer b) {
        this.setB(b);
        return this;
    }

    public List<String> getSS() {
        return this.sS;
    }

    public void setSS(Collection<String> sS) {
        if (sS == null) {
            this.sS = null;
        } else {
            this.sS = new ArrayList(sS);
        }
    }

    public AttributeValue withSS(String... sS) {
        if (this.sS == null) {
            this.setSS(new ArrayList(sS.length));
        }

        String[] var2 = sS;
        int var3 = sS.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String ele = var2[var4];
            this.sS.add(ele);
        }

        return this;
    }

    public AttributeValue withSS(Collection<String> sS) {
        this.setSS(sS);
        return this;
    }

    public List<String> getNS() {
        return this.nS;
    }

    public void setNS(Collection<String> nS) {
        if (nS == null) {
            this.nS = null;
        } else {
            this.nS = new ArrayList(nS);
        }
    }

    public AttributeValue withNS(String... nS) {
        if (this.nS == null) {
            this.setNS(new ArrayList(nS.length));
        }

        String[] var2 = nS;
        int var3 = nS.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String ele = var2[var4];
            this.nS.add(ele);
        }

        return this;
    }

    public AttributeValue withNS(Collection<String> nS) {
        this.setNS(nS);
        return this;
    }

    public List<ByteBuffer> getBS() {
        return this.bS;
    }

    public void setBS(Collection<ByteBuffer> bS) {
        if (bS == null) {
            this.bS = null;
        } else {
            this.bS = new ArrayList(bS);
        }
    }

    public AttributeValue withBS(ByteBuffer... bS) {
        if (this.bS == null) {
            this.setBS(new ArrayList(bS.length));
        }

        ByteBuffer[] var2 = bS;
        int var3 = bS.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            ByteBuffer ele = var2[var4];
            this.bS.add(ele);
        }

        return this;
    }

    public AttributeValue withBS(Collection<ByteBuffer> bS) {
        this.setBS(bS);
        return this;
    }

    public Map<String, AttributeValue> getM() {
        return this.m;
    }

    public void setM(Map<String, AttributeValue> m) {
        this.m = m;
    }

    public AttributeValue withM(Map<String, AttributeValue> m) {
        this.setM(m);
        return this;
    }

    public AttributeValue addMEntry(String key, AttributeValue value) {
        if (null == this.m) {
            this.m = new HashMap();
        }

        if (this.m.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        } else {
            this.m.put(key, value);
            return this;
        }
    }

    public AttributeValue clearMEntries() {
        this.m = null;
        return this;
    }

    public List<AttributeValue> getL() {
        return this.l;
    }

    public void setL(Collection<AttributeValue> l) {
        if (l == null) {
            this.l = null;
        } else {
            this.l = new ArrayList(l);
        }
    }

    public AttributeValue withL(AttributeValue... l) {
        if (this.l == null) {
            this.setL(new ArrayList(l.length));
        }

        AttributeValue[] var2 = l;
        int var3 = l.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            AttributeValue ele = var2[var4];
            this.l.add(ele);
        }

        return this;
    }

    public AttributeValue withL(Collection<AttributeValue> l) {
        this.setL(l);
        return this;
    }

    public Boolean getNULL() {
        return this.nULLValue;
    }

    public void setNULL(Boolean nULLValue) {
        this.nULLValue = nULLValue;
    }

    public AttributeValue withNULL(Boolean nULLValue) {
        this.setNULL(nULLValue);
        return this;
    }

    public Boolean isNULL() {
        return this.nULLValue;
    }

    public Boolean getBOOL() {
        return this.bOOL;
    }

    public void setBOOL(Boolean bOOL) {
        this.bOOL = bOOL;
    }

    public AttributeValue withBOOL(Boolean bOOL) {
        this.setBOOL(bOOL);
        return this;
    }

    public Boolean isBOOL() {
        return this.bOOL;
    }

}
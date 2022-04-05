package com.drivewealth.dbproxy.mapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@lombok.ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemCollectionMetrics {


    @JsonProperty("itemCollectionKey")
    private Map<String, AttributeValue> itemCollectionKey;
    @JsonProperty("sizeEstimateRangeGB")
    private List<Double> sizeEstimateRangeGB;

    public Map<String, AttributeValue> getItemCollectionKey() {
        return this.itemCollectionKey;
    }

    public void setItemCollectionKey(Map<String, AttributeValue> itemCollectionKey) {
        this.itemCollectionKey = itemCollectionKey;
    }

    public ItemCollectionMetrics withItemCollectionKey(Map<String, AttributeValue> itemCollectionKey) {
        this.setItemCollectionKey(itemCollectionKey);
        return this;
    }

    public ItemCollectionMetrics addItemCollectionKeyEntry(String key, AttributeValue value) {
        if (null == this.itemCollectionKey) {
            this.itemCollectionKey = new HashMap();
        }

        if (this.itemCollectionKey.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        } else {
            this.itemCollectionKey.put(key, value);
            return this;
        }
    }

    public ItemCollectionMetrics clearItemCollectionKeyEntries() {
        this.itemCollectionKey = null;
        return this;
    }

    public List<Double> getSizeEstimateRangeGB() {
        return this.sizeEstimateRangeGB;
    }

    public void setSizeEstimateRangeGB(Collection<Double> sizeEstimateRangeGB) {
        if (sizeEstimateRangeGB == null) {
            this.sizeEstimateRangeGB = null;
        } else {
            this.sizeEstimateRangeGB = new ArrayList(sizeEstimateRangeGB);
        }
    }

    public ItemCollectionMetrics withSizeEstimateRangeGB(Double... sizeEstimateRangeGB) {
        if (this.sizeEstimateRangeGB == null) {
            this.setSizeEstimateRangeGB(new ArrayList(sizeEstimateRangeGB.length));
        }

        Double[] var2 = sizeEstimateRangeGB;
        int var3 = sizeEstimateRangeGB.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Double ele = var2[var4];
            this.sizeEstimateRangeGB.add(ele);
        }

        return this;
    }

    public ItemCollectionMetrics withSizeEstimateRangeGB(Collection<Double> sizeEstimateRangeGB) {
        this.setSizeEstimateRangeGB(sizeEstimateRangeGB);
        return this;
    }


}



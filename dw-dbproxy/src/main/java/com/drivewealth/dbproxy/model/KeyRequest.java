package com.drivewealth.dbproxy.model;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyRequest implements Serializable, Cloneable {
    private String tableName;
    private Map<String, AttributeValue> key;
    private Map<String, ExpectedAttributeValue> expected;
    private String conditionalOperator;
    private String returnValues;
    private String returnConsumedCapacity;
    private String returnItemCollectionMetrics;
    private String conditionExpression;
    private Map<String, String> expressionAttributeNames;
    private Map<String, AttributeValue> expressionAttributeValues;

    public KeyRequest() {
    }

    public KeyRequest(String tableName, Map<String, AttributeValue> key) {
        this.setTableName(tableName);
        this.setKey(key);
    }

    public KeyRequest(String tableName, Map<String, AttributeValue> key, String returnValues) {
        this.setTableName(tableName);
        this.setKey(key);
        this.setReturnValues(returnValues);
    }

    public KeyRequest(String tableName, Map<String, AttributeValue> key, ReturnValue returnValues) {
        this.setTableName(tableName);
        this.setKey(key);
        this.setReturnValues(returnValues.toString());
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public KeyRequest withTableName(String tableName) {
        this.setTableName(tableName);
        return this;
    }

    public Map<String, AttributeValue> getKey() {
        return this.key;
    }

    public void setKey(Map<String, AttributeValue> key) {
        this.key = key;
    }

    public KeyRequest withKey(Map<String, AttributeValue> key) {
        this.setKey(key);
        return this;
    }

    public KeyRequest addKeyEntry(String key, AttributeValue value) {
        if (null == this.key) {
            this.key = new HashMap();
        }

        if (this.key.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        } else {
            this.key.put(key, value);
            return this;
        }
    }

    public KeyRequest clearKeyEntries() {
        this.key = null;
        return this;
    }

    public Map<String, ExpectedAttributeValue> getExpected() {
        return this.expected;
    }

    public void setExpected(Map<String, ExpectedAttributeValue> expected) {
        this.expected = expected;
    }

    public KeyRequest withExpected(Map<String, ExpectedAttributeValue> expected) {
        this.setExpected(expected);
        return this;
    }

    public KeyRequest addExpectedEntry(String key, ExpectedAttributeValue value) {
        if (null == this.expected) {
            this.expected = new HashMap();
        }

        if (this.expected.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        } else {
            this.expected.put(key, value);
            return this;
        }
    }

    public KeyRequest clearExpectedEntries() {
        this.expected = null;
        return this;
    }

    public String getConditionalOperator() {
        return this.conditionalOperator;
    }

    public void setConditionalOperator(String conditionalOperator) {
        this.conditionalOperator = conditionalOperator;
    }

    public void setConditionalOperator(ConditionalOperator conditionalOperator) {
        this.withConditionalOperator(conditionalOperator);
    }

    public KeyRequest withConditionalOperator(String conditionalOperator) {
        this.setConditionalOperator(conditionalOperator);
        return this;
    }

    public KeyRequest withConditionalOperator(ConditionalOperator conditionalOperator) {
        this.conditionalOperator = conditionalOperator.toString();
        return this;
    }

    public String getReturnValues() {
        return this.returnValues;
    }

    public void setReturnValues(String returnValues) {
        this.returnValues = returnValues;
    }

    public void setReturnValues(ReturnValue returnValues) {
        this.withReturnValues(returnValues);
    }

    public KeyRequest withReturnValues(String returnValues) {
        this.setReturnValues(returnValues);
        return this;
    }

    public KeyRequest withReturnValues(ReturnValue returnValues) {
        this.returnValues = returnValues.toString();
        return this;
    }

    public String getReturnConsumedCapacity() {
        return this.returnConsumedCapacity;
    }

    public void setReturnConsumedCapacity(String returnConsumedCapacity) {
        this.returnConsumedCapacity = returnConsumedCapacity;
    }

    public void setReturnConsumedCapacity(ReturnConsumedCapacity returnConsumedCapacity) {
        this.withReturnConsumedCapacity(returnConsumedCapacity);
    }

    public KeyRequest withReturnConsumedCapacity(String returnConsumedCapacity) {
        this.setReturnConsumedCapacity(returnConsumedCapacity);
        return this;
    }

    public KeyRequest withReturnConsumedCapacity(ReturnConsumedCapacity returnConsumedCapacity) {
        this.returnConsumedCapacity = returnConsumedCapacity.toString();
        return this;
    }

    public String getReturnItemCollectionMetrics() {
        return this.returnItemCollectionMetrics;
    }

    public void setReturnItemCollectionMetrics(String returnItemCollectionMetrics) {
        this.returnItemCollectionMetrics = returnItemCollectionMetrics;
    }

    public void setReturnItemCollectionMetrics(ReturnItemCollectionMetrics returnItemCollectionMetrics) {
        this.withReturnItemCollectionMetrics(returnItemCollectionMetrics);
    }

    public KeyRequest withReturnItemCollectionMetrics(String returnItemCollectionMetrics) {
        this.setReturnItemCollectionMetrics(returnItemCollectionMetrics);
        return this;
    }

    public KeyRequest withReturnItemCollectionMetrics(ReturnItemCollectionMetrics returnItemCollectionMetrics) {
        this.returnItemCollectionMetrics = returnItemCollectionMetrics.toString();
        return this;
    }

    public String getConditionExpression() {
        return this.conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public KeyRequest withConditionExpression(String conditionExpression) {
        this.setConditionExpression(conditionExpression);
        return this;
    }

    public Map<String, String> getExpressionAttributeNames() {
        return this.expressionAttributeNames;
    }

    public void setExpressionAttributeNames(Map<String, String> expressionAttributeNames) {
        this.expressionAttributeNames = expressionAttributeNames;
    }

    public KeyRequest withExpressionAttributeNames(Map<String, String> expressionAttributeNames) {
        this.setExpressionAttributeNames(expressionAttributeNames);
        return this;
    }

    public KeyRequest addExpressionAttributeNamesEntry(String key, String value) {
        if (null == this.expressionAttributeNames) {
            this.expressionAttributeNames = new HashMap();
        }

        if (this.expressionAttributeNames.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        } else {
            this.expressionAttributeNames.put(key, value);
            return this;
        }
    }

    public KeyRequest clearExpressionAttributeNamesEntries() {
        this.expressionAttributeNames = null;
        return this;
    }

    public Map<String, AttributeValue> getExpressionAttributeValues() {
        return this.expressionAttributeValues;
    }

    public void setExpressionAttributeValues(Map<String, AttributeValue> expressionAttributeValues) {
        this.expressionAttributeValues = expressionAttributeValues;
    }

    public KeyRequest withExpressionAttributeValues(Map<String, AttributeValue> expressionAttributeValues) {
        this.setExpressionAttributeValues(expressionAttributeValues);
        return this;
    }

    public KeyRequest addExpressionAttributeValuesEntry(String key, AttributeValue value) {
        if (null == this.expressionAttributeValues) {
            this.expressionAttributeValues = new HashMap();
        }

        if (this.expressionAttributeValues.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        } else {
            this.expressionAttributeValues.put(key, value);
            return this;
        }
    }

    public KeyRequest clearExpressionAttributeValuesEntries() {
        this.expressionAttributeValues = null;
        return this;
    }

    public void setKey(Entry<String, AttributeValue> hashKey, Entry<String, AttributeValue> rangeKey) throws IllegalArgumentException {
        HashMap<String, AttributeValue> key = new HashMap();
        if (hashKey != null) {
            key.put(hashKey.getKey(), hashKey.getValue());
            if (rangeKey != null) {
                key.put(rangeKey.getKey(), rangeKey.getValue());
            }

            this.setKey(key);
        } else {
            throw new IllegalArgumentException("hashKey must be non-null object.");
        }
    }

    public KeyRequest withKey(Entry<String, AttributeValue> hashKey, Entry<String, AttributeValue> rangeKey) throws IllegalArgumentException {
        this.setKey(hashKey, rangeKey);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getTableName() != null) {
            sb.append("TableName: ").append(this.getTableName()).append(",");
        }

        if (this.getKey() != null) {
            sb.append("Key: ").append(this.getKey()).append(",");
        }

        if (this.getExpected() != null) {
            sb.append("Expected: ").append(this.getExpected()).append(",");
        }

        if (this.getConditionalOperator() != null) {
            sb.append("ConditionalOperator: ").append(this.getConditionalOperator()).append(",");
        }

        if (this.getReturnValues() != null) {
            sb.append("ReturnValues: ").append(this.getReturnValues()).append(",");
        }

        if (this.getReturnConsumedCapacity() != null) {
            sb.append("ReturnConsumedCapacity: ").append(this.getReturnConsumedCapacity()).append(",");
        }

        if (this.getReturnItemCollectionMetrics() != null) {
            sb.append("ReturnItemCollectionMetrics: ").append(this.getReturnItemCollectionMetrics()).append(",");
        }

        if (this.getConditionExpression() != null) {
            sb.append("ConditionExpression: ").append(this.getConditionExpression()).append(",");
        }

        if (this.getExpressionAttributeNames() != null) {
            sb.append("ExpressionAttributeNames: ").append(this.getExpressionAttributeNames()).append(",");
        }

        if (this.getExpressionAttributeValues() != null) {
            sb.append("ExpressionAttributeValues: ").append(this.getExpressionAttributeValues());
        }

        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof KeyRequest)) {
            return false;
        } else {
            KeyRequest other = (KeyRequest) obj;
            if (other.getTableName() == null ^ this.getTableName() == null) {
                return false;
            } else if (other.getTableName() != null && !other.getTableName().equals(this.getTableName())) {
                return false;
            } else if (other.getKey() == null ^ this.getKey() == null) {
                return false;
            } else if (other.getKey() != null && !other.getKey().equals(this.getKey())) {
                return false;
            } else if (other.getExpected() == null ^ this.getExpected() == null) {
                return false;
            } else if (other.getExpected() != null && !other.getExpected().equals(this.getExpected())) {
                return false;
            } else if (other.getConditionalOperator() == null ^ this.getConditionalOperator() == null) {
                return false;
            } else if (other.getConditionalOperator() != null && !other.getConditionalOperator().equals(this.getConditionalOperator())) {
                return false;
            } else if (other.getReturnValues() == null ^ this.getReturnValues() == null) {
                return false;
            } else if (other.getReturnValues() != null && !other.getReturnValues().equals(this.getReturnValues())) {
                return false;
            } else if (other.getReturnConsumedCapacity() == null ^ this.getReturnConsumedCapacity() == null) {
                return false;
            } else if (other.getReturnConsumedCapacity() != null && !other.getReturnConsumedCapacity().equals(this.getReturnConsumedCapacity())) {
                return false;
            } else if (other.getReturnItemCollectionMetrics() == null ^ this.getReturnItemCollectionMetrics() == null) {
                return false;
            } else if (other.getReturnItemCollectionMetrics() != null && !other.getReturnItemCollectionMetrics().equals(this.getReturnItemCollectionMetrics())) {
                return false;
            } else if (other.getConditionExpression() == null ^ this.getConditionExpression() == null) {
                return false;
            } else if (other.getConditionExpression() != null && !other.getConditionExpression().equals(this.getConditionExpression())) {
                return false;
            } else if (other.getExpressionAttributeNames() == null ^ this.getExpressionAttributeNames() == null) {
                return false;
            } else if (other.getExpressionAttributeNames() != null && !other.getExpressionAttributeNames().equals(this.getExpressionAttributeNames())) {
                return false;
            } else if (other.getExpressionAttributeValues() == null ^ this.getExpressionAttributeValues() == null) {
                return false;
            } else {
                return other.getExpressionAttributeValues() == null || other.getExpressionAttributeValues().equals(this.getExpressionAttributeValues());
            }
        }
    }


}

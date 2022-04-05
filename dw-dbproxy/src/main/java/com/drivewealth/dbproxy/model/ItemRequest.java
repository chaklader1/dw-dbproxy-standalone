package com.drivewealth.dbproxy.model;

import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemRequest implements Serializable, Cloneable {

    private String tableName;

    private Map<String, AttributeValue> item;

    private Map<String, ExpectedAttributeValue> expected;

    private String returnValues;

    private String returnConsumedCapacity;
    private String returnItemCollectionMetrics;
    private String conditionalOperator;
    private String conditionExpression;
    private Map<String, String> expressionAttributeNames;
    private Map<String, AttributeValue> expressionAttributeValues;

    public ItemRequest() {
    }

    public ItemRequest(String tableName, Map<String, AttributeValue> item) {
        this.setTableName(tableName);
        this.setItem(item);
    }

    public ItemRequest(String tableName, Map<String, AttributeValue> item, String returnValues) {
        this.setTableName(tableName);
        this.setItem(item);
        this.setReturnValues(returnValues);
    }

    public ItemRequest(String tableName, Map<String, AttributeValue> item, ReturnValue returnValues) {
        this.setTableName(tableName);
        this.setItem(item);
        this.setReturnValues(returnValues.toString());
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ItemRequest withTableName(String tableName) {
        this.setTableName(tableName);
        return this;
    }

    public Map<String, AttributeValue> getItem() {
        return this.item;
    }

    public void setItem(Map<String, AttributeValue> item) {
        this.item = item;
    }

    public ItemRequest withItem(Map<String, AttributeValue> item) {
        this.setItem(item);
        return this;
    }

    public ItemRequest addItemEntry(String key, AttributeValue value) {
        if (null == this.item) {
            this.item = new HashMap();
        }

        if (this.item.containsKey(key)) {
            throw new IllegalArgumentException("Duplicated keys (" + key.toString() + ") are provided.");
        } else {
            this.item.put(key, value);
            return this;
        }
    }

    public ItemRequest clearItemEntries() {
        this.item = null;
        return this;
    }

    public Map<String, ExpectedAttributeValue> getExpected() {
        return this.expected;
    }

    public void setExpected(Map<String, ExpectedAttributeValue> expected) {
        this.expected = expected;
    }

    public ItemRequest withExpected(Map<String, ExpectedAttributeValue> expected) {
        this.setExpected(expected);
        return this;
    }

    public ItemRequest addExpectedEntry(String key, ExpectedAttributeValue value) {
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

    public ItemRequest clearExpectedEntries() {
        this.expected = null;
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

    public ItemRequest withReturnValues(String returnValues) {
        this.setReturnValues(returnValues);
        return this;
    }

    public ItemRequest withReturnValues(ReturnValue returnValues) {
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

    public ItemRequest withReturnConsumedCapacity(String returnConsumedCapacity) {
        this.setReturnConsumedCapacity(returnConsumedCapacity);
        return this;
    }

    public ItemRequest withReturnConsumedCapacity(ReturnConsumedCapacity returnConsumedCapacity) {
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

    public ItemRequest withReturnItemCollectionMetrics(String returnItemCollectionMetrics) {
        this.setReturnItemCollectionMetrics(returnItemCollectionMetrics);
        return this;
    }

    public ItemRequest withReturnItemCollectionMetrics(ReturnItemCollectionMetrics returnItemCollectionMetrics) {
        this.returnItemCollectionMetrics = returnItemCollectionMetrics.toString();
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

    public ItemRequest withConditionalOperator(String conditionalOperator) {
        this.setConditionalOperator(conditionalOperator);
        return this;
    }

    public ItemRequest withConditionalOperator(ConditionalOperator conditionalOperator) {
        this.conditionalOperator = conditionalOperator.toString();
        return this;
    }

    public String getConditionExpression() {
        return this.conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public ItemRequest withConditionExpression(String conditionExpression) {
        this.setConditionExpression(conditionExpression);
        return this;
    }

    public Map<String, String> getExpressionAttributeNames() {
        return this.expressionAttributeNames;
    }

    public void setExpressionAttributeNames(Map<String, String> expressionAttributeNames) {
        this.expressionAttributeNames = expressionAttributeNames;
    }

    public ItemRequest withExpressionAttributeNames(Map<String, String> expressionAttributeNames) {
        this.setExpressionAttributeNames(expressionAttributeNames);
        return this;
    }

    public ItemRequest addExpressionAttributeNamesEntry(String key, String value) {
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

    public ItemRequest clearExpressionAttributeNamesEntries() {
        this.expressionAttributeNames = null;
        return this;
    }

    public Map<String, AttributeValue> getExpressionAttributeValues() {
        return this.expressionAttributeValues;
    }

    public void setExpressionAttributeValues(Map<String, AttributeValue> expressionAttributeValues) {
        this.expressionAttributeValues = expressionAttributeValues;
    }

    public ItemRequest withExpressionAttributeValues(Map<String, AttributeValue> expressionAttributeValues) {
        this.setExpressionAttributeValues(expressionAttributeValues);
        return this;
    }

    public ItemRequest addExpressionAttributeValuesEntry(String key, AttributeValue value) {
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

    public ItemRequest clearExpressionAttributeValuesEntries() {
        this.expressionAttributeValues = null;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getTableName() != null) {
            sb.append("TableName: ").append(this.getTableName()).append(",");
        }

        if (this.getItem() != null) {
            sb.append("Item: ").append(this.getItem()).append(",");
        }

        if (this.getExpected() != null) {
            sb.append("Expected: ").append(this.getExpected()).append(",");
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

        if (this.getConditionalOperator() != null) {
            sb.append("ConditionalOperator: ").append(this.getConditionalOperator()).append(",");
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
        } else if (!(obj instanceof ItemRequest)) {
            return false;
        } else {
            ItemRequest other = (ItemRequest) obj;
            if (other.getTableName() == null ^ this.getTableName() == null) {
                return false;
            } else if (other.getTableName() != null && !other.getTableName().equals(this.getTableName())) {
                return false;
            } else if (other.getItem() == null ^ this.getItem() == null) {
                return false;
            } else if (other.getItem() != null && !other.getItem().equals(this.getItem())) {
                return false;
            } else if (other.getExpected() == null ^ this.getExpected() == null) {
                return false;
            } else if (other.getExpected() != null && !other.getExpected().equals(this.getExpected())) {
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
            } else if (other.getConditionalOperator() == null ^ this.getConditionalOperator() == null) {
                return false;
            } else if (other.getConditionalOperator() != null && !other.getConditionalOperator().equals(this.getConditionalOperator())) {
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



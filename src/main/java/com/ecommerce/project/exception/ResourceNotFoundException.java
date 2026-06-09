package com.ecommerce.project.exception;

import lombok.Data;

@Data
public class ResourceNotFoundException extends RuntimeException {
    private String resourceName;
    private String fieldName;
    private String fieldValue;
    private Long fieldId;

    public ResourceNotFoundException(String resourceName, String fieldValue, Long fieldId) {
        super(String.format("%s not found with %s: %d", resourceName, fieldValue, fieldId));
        this.fieldId = fieldId;
        this.fieldValue = fieldValue;
        this.resourceName = resourceName;
    }

    public ResourceNotFoundException(String resourceName, String fieldValue,String fieldName) {
        super(String.format("%s not found with %s: %s", resourceName, fieldValue, fieldName));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}

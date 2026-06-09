package com.ecommerce.project.payload;

import lombok.Data;

@Data
public class ProductDTO {
    private Long productId;
    private String productName;
    private String description;
    private String image;
    private Integer quantity;
    private Double price;
    private Double specialPrice;
    private Double discount;

}

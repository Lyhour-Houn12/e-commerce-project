package com.ecommerce.project.service.util;


import com.ecommerce.project.entity.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Utility {
    private final ProductResponse productResponse;

    @NonNull
    public static ProductResponse getProductResponse(List<ProductDTO> productDTO, Page<Product> products) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTO);
        productResponse.setPageNumber(products.getNumber() + 1);
        productResponse.setPageSize(products.getSize());
        productResponse.setTotalPages(products.getTotalPages());
        productResponse.setTotalElements((int) products.getTotalElements());
        productResponse.setFirstPage(products.isFirst());
        productResponse.setLastPage(products.isLast());

        return  productResponse;
    }
}

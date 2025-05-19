package org.demo.baoleme.dto.request.product;

import lombok.Data;
import org.demo.baoleme.pojo.Product;

@Data
public class ProductViewRequest {
    private Long id;
    private Long storeId;
    private String category;
    private int status;
    private int page;
    private int pageSize;
}

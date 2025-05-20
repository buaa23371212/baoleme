package org.demo.baoleme.dto.request.order;

import lombok.Data;

@Data
public class OrderViewRequest {
    private Long storeId;
    private Integer status;
}

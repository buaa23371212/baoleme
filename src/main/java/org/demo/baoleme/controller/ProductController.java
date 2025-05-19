package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.product.*;
import org.demo.baoleme.dto.response.product.*;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public CommonResponse createProduct(@RequestBody ProductCreateRequest request) {
        // Step1: 创建 Product 对象并拷贝属性
        Product product = new Product();
        BeanUtils.copyProperties(request, product);

        // Step2: 调用 Service 创建商品
        Product createdProduct = productService.createProduct(product);

        // Step3: 处理创建结果
        if (createdProduct == null) {
            return ResponseBuilder.fail("商品创建失败，请检查输入参数");
        }

        // Step4: 构建响应体
        ProductCreateResponse response = new ProductCreateResponse();
        response.setProductId(createdProduct.getId());
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/{productId}")
    public CommonResponse getProductById(@PathVariable Long productId) {
        // Step1: 查询商品详情
        Product product = productService.getProductById(productId);

        // Step2: 验证查询结果
        if (product == null) {
            return ResponseBuilder.fail("商品不存在");
        }

        // Step3: 构建响应体
        ProductViewResponse response = new ProductViewResponse();
        BeanUtils.copyProperties(product, response);
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/store/{storeId}")
    public CommonResponse getProductsByStore(@RequestBody ProductViewRequest request) {
        Long storeId = request.getStoreId();

        // Step2: 查询店铺商品列表
        List<Product> products = productService.getProductsByStore(storeId);

        // Step3: 构建响应体
        List<ProductViewResponse> responses = products.stream()
                .map(product -> {
                    ProductViewResponse resp = new ProductViewResponse();
                    BeanUtils.copyProperties(product, resp);
                    return resp;
                })
                .collect(Collectors.toList());

        return ResponseBuilder.ok(responses);
    }

    @PutMapping("/update")
    public CommonResponse updateProduct(@RequestBody ProductUpdateRequest request) {
        // Step1: 创建 Product 对象并设置 ID
        Product product = new Product();
        product.setId(request.getId());

        // Step2: 拷贝请求参数
        BeanUtils.copyProperties(request, product);

        // Step3: 调用 Service 更新数据
        boolean success = productService.updateProduct(product);

        // Step4: 处理更新结果
        if (!success) {
            return ResponseBuilder.fail("更新失败，请检查商品 ID 是否存在");
        }

        // Step5: 构建响应体
        ProductUpdateResponse response = new ProductUpdateResponse();
        BeanUtils.copyProperties(product, response);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/{productId}/status")
    public CommonResponse updateProductStatus(@PathVariable Long productId,
                                              @RequestParam int status) {
        // Step1: 执行状态更新
        boolean success = productService.updateProductStatus(productId, status);

        // Step2: 返回操作结果
        return success ?
                ResponseBuilder.ok("商品状态更新成功") :
                ResponseBuilder.fail("状态更新失败");
    }

    @DeleteMapping("/{productId}")
    public CommonResponse deleteProduct(@PathVariable Long productId) {
        // Step1: 创建 ProductDeleteRequest 对象
        ProductDeleteRequest request = new ProductDeleteRequest();
        request.setId(productId);

        // Step2: 执行删除操作
        boolean success = productService.deleteProduct(productId);

        // Step3: 返回操作结果
        return success ?
                ResponseBuilder.ok("商品删除成功") :
                ResponseBuilder.fail("删除失败，商品可能不存在");
    }
}
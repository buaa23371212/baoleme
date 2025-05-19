package org.demo.baoleme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.demo.baoleme.mapper.ProductMapper;
import org.demo.baoleme.pojo.Product;
import org.demo.baoleme.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public Product createProduct(Product product) {
        int result = productMapper.insert(product);
        return result > 0 ? product : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long productId) {
        return productMapper.selectById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByStore(Long storeId) {
        return productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStoreId, storeId)
        );
    }

    @Override
    @Transactional
    public boolean updateProduct(Product product) {
        return productMapper.updateById(product) > 0;
    }

    @Override
    @Transactional
    public boolean updateProductStatus(Long productId, int status) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        return productMapper.updateById(product) > 0;
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long productId) {
        return productMapper.deleteById(productId) > 0;
    }
}
package com.example.carva_x.service.serviceImpl;

import com.example.carva_x.entity.Product;
import com.example.carva_x.exception.NotFoundException;
import com.example.carva_x.model.request.ProductRequest;
import com.example.carva_x.model.response.ProductResponse;
import com.example.carva_x.repository.ProductRepository;
import com.example.carva_x.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Integer productId) {
        return toResponse(requireProduct(productId));
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .amount(productRequest.getAmount())
                .description(productRequest.getDescription())
                .tax(productRequest.getTax())
                .build();

        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Integer productId, ProductRequest productRequest) {
        Product product = requireProduct(productId);
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setAmount(productRequest.getAmount());
        product.setDescription(productRequest.getDescription());
        product.setTax(productRequest.getTax());

        return toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Integer productId) {
        productRepository.delete(requireProduct(productId));
    }

    private Product requireProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(toBigDecimal(product.getPrice()))
                .amount(product.getAmount())
                .description(product.getDescription())
                .tax(toBigDecimal(product.getTax()))
                .build();
    }

    private BigDecimal toBigDecimal(Number value) {
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
    }
}

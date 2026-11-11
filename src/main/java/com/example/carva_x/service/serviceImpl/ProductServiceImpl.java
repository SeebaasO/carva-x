package com.example.carva_x.service.serviceImpl;

import com.example.carva_x.entity.Product;
import com.example.carva_x.exception.ForbiddenException;
import com.example.carva_x.exception.NotFoundException;
import com.example.carva_x.model.JwtValidate;
import com.example.carva_x.model.request.ProductRequest;
import com.example.carva_x.model.response.ProductResponse;
import com.example.carva_x.repository.ProductRepository;
import com.example.carva_x.service.JwtService;
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
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(String authHeader) {
        jwtService.validateAccessToken(authHeader);
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(String authHeader, Integer productId) {
        jwtService.validateAccessToken(authHeader);
        return toResponse(requireProduct(productId));
    }

    @Override
    public ProductResponse createProduct(String authHeader, ProductRequest productRequest) {
        validateAdmin(authHeader);
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
    public ProductResponse updateProduct(String authHeader, Integer productId, ProductRequest productRequest) {
        validateAdmin(authHeader);
        Product product = requireProduct(productId);
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setAmount(productRequest.getAmount());
        product.setDescription(productRequest.getDescription());
        product.setTax(productRequest.getTax());

        return toResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(String authHeader, Integer productId) {
        validateAdmin(authHeader);
        productRepository.delete(requireProduct(productId));
    }

    private void validateAdmin(String authHeader) {
        JwtValidate jwtValidate = jwtService.validateAccessToken(authHeader);
        if (!"admin".equals(jwtValidate.getRole())) {
            throw new ForbiddenException("No tiene permisos para realizar esta accion");
        }
    }

    private Product requireProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .amount(product.getAmount())
                .description(product.getDescription())
                .tax(product.getTax())
                .build();
    }
}

package com.example.carva_x.service.serviceImpl;

import com.example.carva_x.entity.Product;
import com.example.carva_x.entity.User;
import com.example.carva_x.entity.WishlistItem;
import com.example.carva_x.exception.BadRequestException;
import com.example.carva_x.exception.NotFoundException;
import com.example.carva_x.model.JwtValidate;
import com.example.carva_x.model.response.ProductResponse;
import com.example.carva_x.model.response.WishlistProductResponse;
import com.example.carva_x.repository.AuthRepository;
import com.example.carva_x.repository.ProductRepository;
import com.example.carva_x.repository.WishlistRepository;
import com.example.carva_x.service.JwtService;
import com.example.carva_x.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final AuthRepository authRepository;
    private final ProductRepository productRepository;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public List<WishlistProductResponse> getWishlist(String authHeader) {
        JwtValidate jwtValidate = jwtService.validateAccessToken(authHeader);
        Integer userId = jwtValidate.getId();
        requireUser(userId);

        return wishlistRepository.findAllByUser_Id(userId).stream()
                .map(item -> toResponse(item.getProduct()))
                .toList();
    }

    @Override
    public WishlistProductResponse addProduct(String authHeader, Integer productId) {
        JwtValidate jwtValidate = jwtService.validateAccessToken(authHeader);
        Integer userId = jwtValidate.getId();
        User user = requireUser(userId);
        Product product = requireProduct(productId);

        if (wishlistRepository.existsByUser_IdAndProduct_Id(userId, productId)) {
            throw new BadRequestException("El producto ya está en la lista de deseos");
        }

        WishlistItem item = WishlistItem.builder()
                .user(user)
                .product(product)
                .build();

        return toResponse(wishlistRepository.save(item).getProduct());
    }

    @Override
    public void removeProduct(String authHeader, Integer productId) {
        JwtValidate jwtValidate = jwtService.validateAccessToken(authHeader);
        Integer userId = jwtValidate.getId();
        WishlistItem item = findWishlistItem(userId, productId);
        wishlistRepository.delete(item);
    }

    @Override
    public WishlistProductResponse replaceProduct(String authHeader, Integer productId, Integer replacementProductId) {
        JwtValidate jwtValidate = jwtService.validateAccessToken(authHeader);
        Integer userId = jwtValidate.getId();
        WishlistItem item = findWishlistItem(userId, productId);
        Product replacementProduct = requireProduct(replacementProductId);

        if (!productId.equals(replacementProductId)
                && wishlistRepository.existsByUser_IdAndProduct_Id(userId, replacementProductId)) {
            throw new BadRequestException("El producto de reemplazo ya está en la lista de deseos");
        }

        item.setProduct(replacementProduct);
        return toResponse(wishlistRepository.save(item).getProduct());
    }

    private WishlistItem findWishlistItem(Integer userId, Integer productId) {
        requireUser(userId);
        return wishlistRepository.findByUser_IdAndProduct_Id(userId, productId)
                .orElseThrow(() -> new NotFoundException("El producto no está en la lista de deseos"));
    }

    private User requireUser(Integer userId) {
        return authRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
    }

    private Product requireProduct(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    private WishlistProductResponse toResponse(Product product) {
        boolean outOfStock = product.getAmount() <= 0;
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(toDouble(product.getPrice()))
                .description(product.getDescription())
                .tax(toDouble(product.getTax()))
                .build();

        return WishlistProductResponse.builder()
                .product(productResponse)
                .outOfStock(outOfStock)
                .notification(outOfStock ? "Este producto ya no está disponible en stock" : null)
                .build();
    }

    private Double toDouble(Number value) {
        return value == null ? null : value.doubleValue();
    }
}

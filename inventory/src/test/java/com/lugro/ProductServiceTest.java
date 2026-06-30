package com.lugro;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @InjectMocks private ProductService productService;

    @Test
    void shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(new Product()));
        assertThat(productService.getAll()).hasSize(1);
    }

    @Test
    void shouldReturnProductById() {
        Product product = Product.builder().id(1L).name("Test").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        assertThat(productService.getById(1L).getName()).isEqualTo("Test");
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.getById(99L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Product not found");
    }

    @Test
    void shouldCreateProduct() {
        Product input = Product.builder().name("New").price(BigDecimal.TEN).quantity(5).build();
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Product result = productService.create(input);
        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateProduct() {
        Product existing = Product.builder().id(1L).name("Old").quantity(1).build();
        Product updated = Product.builder().name("New").quantity(10).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Product result = productService.update(1L, updated);
        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getQuantity()).isEqualTo(10);
    }

    @Test
    void shouldDeleteProduct() {
        productService.delete(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void shouldSearchByName() {
        when(productRepository.findByNameContainingIgnoreCase("test")).thenReturn(List.of(new Product()));
        assertThat(productService.search("test")).hasSize(1);
    }
}

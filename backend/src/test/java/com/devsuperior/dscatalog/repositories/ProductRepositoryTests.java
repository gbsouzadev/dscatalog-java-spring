package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factories.ProductFactory;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void findByIdShouldReturnNonEmptyOptionalWhenIdExists(){

        Optional<Product> result = repository.findById(existingId);
        Product product = result.get();

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(existingId, product.getId());
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExist(){

        Optional<Product> result = repository.findById(nonExistingId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void saveShouldPersistWithAutoincrementeWhenIdIsValid() {
        Product product = ProductFactory.createProduct();

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdIsValid() {

        long existentId = 1L;

        repository.deleteById(existentId);
        Optional<Product> result = repository.findById(existentId);

        Assertions.assertFalse(result.isPresent());
    }

}

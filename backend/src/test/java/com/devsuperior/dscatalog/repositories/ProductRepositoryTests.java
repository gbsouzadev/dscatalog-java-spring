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

    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        countTotalProducts = 25L;
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

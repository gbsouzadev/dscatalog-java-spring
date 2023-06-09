package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factories.Factory;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;


    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private Product product;
    private Category category;
    private ProductDTO productDTO;
    private PageImpl<Product> page;


    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(product));

        //Update
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(categoryRepository.getReferenceById(category.getId())).thenReturn(category);

        //Insert and Update
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        //FindAllPagable
        Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);

        //FindById
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        //Delete
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DatabaseException.class).when(repository).deleteById(dependentId);
    }

    @Test
    public void insertShouldInsertProductDto() {

        ProductDTO result = service.insert(productDTO);

        Assertions.assertEquals(productDTO.getName(), result.getName());
        Mockito.verify(repository, times(1)).save((Product)ArgumentMatchers.any());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ProductDTO result = service.update(nonExistingId, productDTO);
        });
        Mockito.verify(repository, times(1)).getReferenceById(nonExistingId);
    }

    @Test
    public void updateShouldReturnProductDtoWhenIdExists() {

        ProductDTO result = service.update(existingId, productDTO);

        Assertions.assertEquals(existingId, result.getId());
        Mockito.verify(repository, times(1)).getReferenceById(existingId);
        Mockito.verify(repository, times(1)).save(product);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
        Mockito.verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    public void findByIdShoulReturnProductDtoWhenIdExists() {

        ProductDTO result = service.findById(existingId);

        Assertions.assertEquals(existingId, result.getId());
        Mockito.verify(repository, times(1)).findById(existingId);
    }

    @Test
    public void findAllPagesShouldReturnPage() {

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "id");

        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, times(1)).findAll(pageable);
    }

    @Test
    public void deleteShouldTrhowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

        Mockito.verify(repository, times(1)).existsById(dependentId);
        Mockito.verify(repository, times(1)).deleteById(dependentId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(repository, times(1)).existsById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, times(1)).existsById(existingId);
        Mockito.verify(repository, times(1)).deleteById(existingId);
    }
}

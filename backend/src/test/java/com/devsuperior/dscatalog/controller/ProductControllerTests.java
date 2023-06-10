package com.devsuperior.dscatalog.controller;

import com.devsuperior.dscatalog.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest
public class ProductControllerTests {

    @InjectMocks
    private ProductController controller;

    @Mock
    private ProductService service;

    @BeforeEach
    void setUp() throws Exception{
    }
}

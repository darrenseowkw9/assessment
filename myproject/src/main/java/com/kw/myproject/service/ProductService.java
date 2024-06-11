package com.kw.myproject.service;

import com.kw.myproject.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    Product add(Product product) throws Exception;
    Product update(Product product, Long id);
    void remove(Long id);
    List<Product> list(Integer page, Integer size);
    List<Product> list();
    Product get(Long id);
    void addProductFromVendorList();
}

package com.kw.myproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kw.myproject.repository.ProductRepository;
import com.kw.myproject.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @Override
    public Product add(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    @Override
    public Product update(Product product, Long id) {
        Product productToUpdate = productRepository.getReferenceById(id);
        productToUpdate.setAttributes(product);
        return productRepository.save(productToUpdate);
    }

    @Transactional
    @Override
    public void remove(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    @Override
    public List<Product> list(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.getContent();
    }

    @Transactional
    @Override
    public List<Product> list() {
        return productRepository.findAll();
    }

    @Transactional
    @Override
    public Product get(Long id) {
        return productRepository.findById(id).get();
    }

    @Transactional
    @Override
    public void addProductFromVendorList() {
        List<Product> productList = getVendorProductList();
        productRepository.saveAll(productList);
    }

    private List<Product> getVendorProductList(){
        List<Product> productList = new ArrayList<>();
        final String uri = "https://freetestapi.com/api/v1/products?limit=5";
        RestTemplate restTemplate = new RestTemplate();
        Product[] result = restTemplate.getForObject(uri, Product[].class);
        if(result.length > 0 ){
            productList = Arrays.asList(result);
        }
        return productList;
    }
}

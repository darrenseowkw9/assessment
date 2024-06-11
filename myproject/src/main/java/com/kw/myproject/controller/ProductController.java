package com.kw.myproject.controller;

import com.kw.myproject.entity.Product;
import com.kw.myproject.model.ResponseModel;
import com.kw.myproject.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("product")
public class ProductController {
    Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;

    @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseModel> addProduct(
            @RequestBody Product product
    ) {
        try {
            product = productService.add(product);
            return new ResponseEntity<>(new ResponseModel(product), HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new ResponseModel(HttpStatus.BAD_REQUEST,"Something went wrong."), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseModel> updateProduct(
            @PathVariable String id,
            @RequestBody Product product
    ) {
        try {
            product = productService.update(product, Long.valueOf(id));
            return new ResponseEntity<>(new ResponseModel(product), HttpStatus.OK);
        } catch (NoSuchElementException | EntityNotFoundException e){
            return new ResponseEntity<>(new ResponseModel(HttpStatus.NOT_FOUND,"Invalid ID. Product is not found."), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new ResponseModel(HttpStatus.BAD_REQUEST,"Something went wrong."), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/")
    public ResponseEntity<ResponseModel> listProduct(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        try {
            List<Product> productList = new ArrayList<>();
            if(page != null && size != null){
                productList = productService.list(page, size);
            }else{
                productList = productService.list();
            }
            return new ResponseEntity<>(new ResponseModel(productList), HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new ResponseModel(HttpStatus.BAD_REQUEST,"Something went wrong."), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseModel> getProduct(@PathVariable String id) {
        try {
            Product product = productService.get(Long.valueOf(id));
            return new ResponseEntity<>(new ResponseModel(product), HttpStatus.OK);
        } catch (NoSuchElementException | EntityNotFoundException e){
            return new ResponseEntity<>(new ResponseModel(HttpStatus.NOT_FOUND,"Invalid ID. Product is not found."), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new ResponseModel(HttpStatus.BAD_REQUEST, "Something went wrong."), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/external", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ResponseModel> addProductFromVendor() {
        try {
            productService.addProductFromVendorList();
            return new ResponseEntity<>(new ResponseModel(null), HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(new ResponseModel(HttpStatus.BAD_REQUEST,"Something went wrong."), HttpStatus.BAD_REQUEST);
        }
    }
}

package com.gadgetmonkey.dao;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadgetmonkey.entities.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer> {
    @Override
    List<Product> findAll();
    @Override
    Product getReferenceById(Integer id);
    @Override
    void deleteById(Integer id);
    @Override
    void deleteAllByIdInBatch(Iterable<Integer> ids);
}


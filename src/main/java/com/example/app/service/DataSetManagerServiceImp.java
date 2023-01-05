package com.example.app.service;

import org.apache.jena.query.Dataset;
import org.springframework.stereotype.Service;

@Service
public class DataSetManagerServiceImp implements DataSetManagerService{
    private Dataset dataset;
    @Override
    public Dataset get() {
        return dataset;
    }

    @Override
    public void set(Dataset dataset) {
       this.dataset = dataset;
    }
}

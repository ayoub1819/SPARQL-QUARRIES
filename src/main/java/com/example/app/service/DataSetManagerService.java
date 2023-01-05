package com.example.app.service;

import org.apache.jena.query.Dataset;

public interface DataSetManagerService {
    Dataset get();
    void  set(Dataset dataset);
}

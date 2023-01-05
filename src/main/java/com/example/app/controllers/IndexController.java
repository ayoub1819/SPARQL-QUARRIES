package com.example.app.controllers;

import org.apache.jena.rdf.model.*;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VCARD;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

@RestController
public class IndexController {
    private int count = 2;
    @GetMapping(path = "/indexe")
    public String index(){
        try{
            File f = new File("src/main/resources/upload/rdf"+(count++)+".xml");

            if(f.createNewFile()) {
                try(FileOutputStream fileOutputStream = new FileOutputStream(f);){
                    String personURI    = "http://somewhere/ayoub";
                    String givenName    = "ben John";
                    String familyName   = "ben dahman";
                    String fullName     = givenName + " " + familyName;

                    // create an empty Model
                    Model model = ModelFactory.createDefaultModel();
                    Resource johnSmith = model.createResource(personURI)
                            .addProperty(VCARD.FN, fullName)
                            .addProperty(VCARD.N,
                                    model.createResource()
                                            .addProperty(VCARD.Given, givenName)
                                            .addProperty(VCARD.Family, familyName));
                    model.write(fileOutputStream);

                }


            };
            return f.getAbsolutePath();
        }catch (Exception e){
            return "faild";
        }

    }


}

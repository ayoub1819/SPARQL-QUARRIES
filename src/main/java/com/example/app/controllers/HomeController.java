package com.example.app.controllers;

import com.example.app.service.DataSetManagerService;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VCARD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class HomeController {

    private DataSetManagerService DataSetManager;

    @Autowired
    public HomeController(DataSetManagerService dataSetManager) {
        DataSetManager = dataSetManager;
    }

    @GetMapping(path = "/")
    public String home(){
        return "index.html";
    }

    @PostMapping(path = "/test")
    @ResponseBody
    public String test(@RequestBody Map<String,String> data) throws IOException {
        //System.out.println("i: "+i);
        this.DataSetManager.get().getDefaultModel().write(System.out);
        String queryType = data.get("queryType");
        String s = """
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                
                """;
        String queryString = s + data.get("query");
        //System.out.println(queryType+"   "+queryType.equals("select"));
        switch (queryType){
            case "select":
                //System.out.println("select ");
                //System.out.println("query: "+queryString);
                Query query = QueryFactory.create(queryString);
                QueryExecution queryExecution = QueryExecutionFactory.create(query, this.DataSetManager.get());
                ResultSet resultSet = queryExecution.execSelect();
                List<Map> jsonList = new Vector<Map>();
                while (resultSet.hasNext()) {
                    QuerySolution qsol = resultSet.nextSolution();
                    Iterator<String> varNames = qsol.varNames();
                    Map<String, Object> jsonMap = new HashMap<String, Object>();
                    while (varNames.hasNext()) {
                        String varName = varNames.next();
                        jsonMap.put(varName, qsol.get(varName).toString());
                    }
                    jsonList.add(jsonMap);
                }
                return JsonUtils.toPrettyString(jsonList);

            case "update":
                //System.out.println("update");
                UpdateAction.parseExecute(queryString,this.DataSetManager.get());
                RDFDataMgr.write(System.out, this.DataSetManager.get(), Lang.TRIG);
                return JsonUtils.toPrettyString("success");

            case "ask":
                //System.out.println("3");
                Query askQuery = QueryFactory.create(queryString);
                QueryExecution askQueryExecution = QueryExecutionFactory.create(askQuery, this.DataSetManager.get());
                boolean result = askQueryExecution.execAsk();
                String answer = result ? "yes":"no";
                return   JsonUtils.toPrettyString(answer);


        }
        return "";
    }


    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("files") List<MultipartFile>  files ) throws IOException {

        Model model = ModelFactory.createDefaultModel();
        Map<String,String> response = new HashMap<>();
        //System.out.println("ttttttttttt");
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            System.out.println(fileName);
            try {
                    model.read(file.getInputStream(),null);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                response.put("error",e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(JsonUtils.toPrettyString(response));
            }
        }
        Dataset dataset = DatasetFactory.create(model);
        DataSetManager.set(dataset);



        return ResponseEntity.ok(JsonUtils.toPrettyString("File uploaded successfully"));
    }

}

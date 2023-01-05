package com.example.app;

import com.github.jsonldjava.utils.JsonUtils;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.VCARD;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.InputStream;
import java.util.*;

@SpringBootApplication
public class AppApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {


		// some definitions
		String personURI    = "http://somewhere/JohnSmith";
		String givenName    = "John";
		String familyName   = "Smith";
		String fullName     = givenName + " " + familyName;

		// create an empty Model
		Model model = ModelFactory.createDefaultModel();
		/*
		InputStream in = RDFDataMgr.open( "src/main/resources/upload/rdf2.xml" );
		InputStream in2 = RDFDataMgr.open( "src/main/resources/upload/rdf3.xml" );
		model.read(in,null);
		model.read(in2,null);
		model.write(System.out);
		System.out.println("3333333333333333333333");

		 */
		Resource johnSmith = model.createResource(personURI)
									.addProperty(VCARD.FN, fullName)
									.addProperty(VCARD.N,
									model.createResource()
									.addProperty(VCARD.Given, givenName)
									.addProperty(VCARD.Family, familyName));

		// list the statements in the Model
		StmtIterator iter = model.listStatements();

// print out the predicate, subject and object of each statement
		for (Statement stmt : model.listStatements().toList()) {
			System.out.println(stmt.toString());
		}
		/////////////////////////////////////////////////////////////
		Resource r = model.createResource();
		Property p = model.createProperty("http://somewhere/p");
		// add the property
		r.addProperty(RDFS.label, model.createLiteral("chat", "en"))
				.addProperty(p, model.createLiteral("chat", "fr"))
				.addProperty(RDFS.label, model.createLiteral("<em>chat</em>", true));


		// write out the Model
		model.write(System.out);

		Dataset dataset = DatasetFactory.create(model);
		String quary = """
				PREFIX info216: <http://ex.org/teaching#>
				INSERT DATA {
						info216:cade info216:teaches info216:ECO001 .'
						GRAPH <http://ex.org/personal#Graph> {
									info216:cade info216:age '29' .
									}
				}
				""";
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
		try{
			UpdateAction.parseExecute(quary,dataset);
		}catch (Exception e){
			System.out.println(e.getMessage());
		}

		//dataset.getDefaultModel().write(System.out);
		//RDFDataMgr.write(System.out, dataset.getUnionModel(), Lang.RDFXML);
		Dataset dataset1 = TDBFactory.createDataset("src/main/resources/upload/db1");
		RDFDataMgr.write(System.out, dataset1, Lang.TRIG);
		Query query = QueryFactory.create(""
				+ "SELECT ?s ?p ?o WHERE {"
				+ " ?s ?p ?o ."
				+ "}");
		QueryExecution queryExecution = QueryExecutionFactory.create(query, dataset);
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
		System.out.println(JsonUtils.toPrettyString(jsonList));



	}
}

package cl.uchile.dcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;

public class RDFPredicateFilterHandler implements RDFHandler{
	private RDFHandler sink;
	TreeSet<String> allow = null;
	TreeSet<String> startsWithAllow = null;
	
	public RDFPredicateFilterHandler(RDFHandler sink, TreeSet<String> allow, TreeSet<String> startsWithAllow){
		this.sink = sink;
		this.allow = allow;
		this.startsWithAllow = startsWithAllow;
	}
	
	public static RDFPredicateFilterHandler createRDFPredicateFilterHandler(File f, RDFHandler sink) throws IOException{
		TreeSet<String> allow = new TreeSet<String>();
		TreeSet<String> startsWithAllow = new TreeSet<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = null;
		while((line=br.readLine())!=null){
			line = line.trim();
			if(!line.isEmpty()){
				if(line.startsWith("^")){
					startsWithAllow.add(line.substring(1));
				} else{
					allow.add(line);
				}
			}
		}
		br.close();
		
		return new RDFPredicateFilterHandler(sink, allow, startsWithAllow);
	}
	
	

	@Override
	public void endRDF() throws RDFHandlerException {
		sink.endRDF();
	}

	@Override
	public void handleComment(String arg0) throws RDFHandlerException {
		//sink.handleComment(arg0);		
	}

	@Override
	public void handleNamespace(String arg0, String arg1) throws RDFHandlerException {
		sink.handleNamespace(arg0,arg1);		
	}

	@Override
	public void handleStatement(Statement arg0) throws RDFHandlerException {
		IRI pred = arg0.getPredicate();
		String strPred = pred.toString();
		
		if(allow.contains(strPred)){
			sink.handleStatement(arg0);	
		} else{
			for(String str:startsWithAllow){
				if(strPred.startsWith(str)){
					sink.handleStatement(arg0);
				}
			}
		}
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		sink.startRDF();		
	}

}

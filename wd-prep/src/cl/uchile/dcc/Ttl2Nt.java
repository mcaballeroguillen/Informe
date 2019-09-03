package cl.uchile.dcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;

public class Ttl2Nt {
	public static void main(String[] args) throws IOException{
		if(args.length!=3 && args.length!=4){
			System.err.println("usage: input output baseIri");
			System.err.println("assumes input and output utf-8");
			System.err.println("use '-' for stdin/stdout");
			System.exit(0);
		}
		
		InputStream in = null;
		if(args[0].equals("-")){
			in = System.in;
		} else{
			in = new FileInputStream(args[0]);
			if(args[0].endsWith(".gz")){
				in = new GZIPInputStream(in);
			}
		}
		
		OutputStream out = null;
		if(args[1].equals("-")){
			out = System.out;
		} else{
			out = new FileOutputStream(args[1]);
			if(args[1].endsWith(".gz")){
				out = new GZIPOutputStream(out);
			}
		}
		
		BufferedReader inBr = new BufferedReader(new InputStreamReader(in,"utf-8")); 
				
		RDFParser aParser = Rio.createParser(RDFFormat.TURTLE);
		
		RDFHandler sink = Rio.createWriter(RDFFormat.NTRIPLES, out);
		
		if(args.length==4){
			sink = RDFPredicateFilterHandler.createRDFPredicateFilterHandler(new File(args[3]), sink);
		}

		aParser.setRDFHandler(sink);
		
		try{
			aParser.parse(inBr,args[2]);
		} catch(Exception e){
			inBr.close();
			throw new IOException(e);
		}
		inBr.close();
		out.close();
	}
}

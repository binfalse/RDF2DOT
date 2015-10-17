package de.binfalse.rdf2dot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import de.binfalse.bfutils.AlphabetIterator;

/**
 *
 * @author Martin Scharm
 */
public class RdfDotConverter 
{
    public static void main( String[] args ) throws IOException
    {
      String rankDir = "BT";
      if (args.length > 1)
     	 rankDir = args[1];
    	new RdfDotConverter ().convert (args[0], args[0] + ".dot", rankDir);
    }
    
    public void convert (String inputFileName, String outputFileName, String rankDir) throws IOException
    {
    	File in = new File (inputFileName).getAbsoluteFile ();
    	File out = new File (outputFileName).getAbsoluteFile ();
    	
    	if (!in.exists () || !in.canRead ())
    	{
    		throw new IOException ("file " + in + " does not exist or cannot be read.");
    	}
    	
    	if (out.exists ())
    	{
    		throw new IOException ("file " + out + " exists. won't overwrite.");
    	}
    	
    	inputFileName = in.getAbsolutePath ();
    	
        Model model = ModelFactory.createDefaultModel();
        
       model.read(inputFileName, "TURTLE");
       
       RdfModel rdfModel = new RdfModel (model, inputFileName);
       
       Map<String, RdfSubject> subjects = new HashMap<String, RdfSubject> ();
       
       ResIterator subjectsIt = model.listSubjects ();
       
       while (subjectsIt.hasNext ())
       {
      	 Resource res = subjectsIt.next ();
      	 if (res.isAnon ())
      		 subjects.put (res.getId ().toString (), new RdfSubject (res, rdfModel));
      	 else
      		 subjects.put (res.getURI (), new RdfSubject (res, rdfModel));
       }

       BufferedWriter bw = new BufferedWriter (new FileWriter (outputFileName));
       
       
       bw.write ("digraph {" + System.lineSeparator ());
       bw.write ("\tcharset=\"" + Charset.defaultCharset() + "\";" + System.lineSeparator ());
       bw.write ("\trankdir=\""+rankDir+"\";" + System.lineSeparator ());
       AlphabetIterator ai = AlphabetIterator.getLowerCaseIterator ();
       for (RdfSubject subject : subjects.values ())
       {
      	 subject.parseProperties ();
      	 bw.write (subject.generateDot (ai).replace (inputFileName, "") + System.lineSeparator ());
       }
       bw.write ("}");
       bw.close ();
    }
}

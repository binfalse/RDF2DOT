/**
 * 
 */
package de.binfalse.rdf2dot;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;


/**
 * @author Martin Scharm
 *
 */
public class RdfModel
{
	private Model model;
	private Map<String,String> reverseNsMap;
	private String filename;
	
	public RdfModel (Model model, String filename)
	{
		this.filename = filename;
		this.model = model;
		reverseNsMap = new HashMap<String,String> ();
		Map<String,String> nsMap = model.getNsPrefixMap ();
		for (String ns : nsMap.keySet ())
			reverseNsMap.put (nsMap.get (ns), ns);
	}
	
	public String expandNsAbbr (String abbr)
	{
		return model.getNsPrefixMap ().get (abbr);
	}
	
	public String getNsAbbr (String uri)
	{
		return reverseNsMap.get (uri);
	}

	
	public final String getNamespaceAbbrPlusColon (String nameSpace)
	{
		String abbr = getNsAbbr (nameSpace);
		if (abbr == null)
			return nameSpace;
		return abbr + ":";
	}
	
	public final String getName (Resource res)
	{
		if (res.isAnon ())
			return null;
		
		
		URI uri = URI.create (res.getURI ());
		

 	 if (uri.getScheme ().equals ("file"))
 	 {
 		 if (uri.getPath ().startsWith (filename))
 			 return uri.getFragment ();
 		 else
 		 {
 			 if (uri.getFragment () != null)
 				return uri.getPath () + "#" + uri.getFragment ();
 			 else
 				return uri.getPath ();
 		 }
 	 }
 	 else
 		return res.getURI ();
	}
	
	public static final String getLiteral (Literal literal)
	{
		if (literal.getDatatypeURI ().equals ("http://www.w3.org/2001/XMLSchema#dateTime"))
		{
			// parse date time
			return literal.getValue ().toString ().replace ('T', ' ');
		}
		return literal.getValue ().toString ();
	}
}

/**
 * 
 */
package de.binfalse.rdf2dot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import de.binfalse.bfutils.AlphabetIterator;


/**
 * @author Martin Scharm
 *
 */
public class RdfSubject
{
	
	private Resource subject;
	private Map<String, String> literals;
	private List<String> is;
	private RdfModel model;
	private DotProperties dot;
	private List<String> outgoingEdges;
	private String name;
	
	
	public RdfSubject (Resource subject, RdfModel model)
	{
		this.model = model;
		this.subject = subject;
		literals = new HashMap<String,String> ();
		is = new ArrayList<String> ();
		outgoingEdges = new ArrayList<String> ();
		dot = DotProperties.getDefault ();
	}
	
	public void parseProperties ()
	{
		 if (!subject.isAnon ())
			 name = model.getName (subject);
		 else
			 name = subject.getId ().toString ();
		 
		 if (name.startsWith ("http"))
		 {
			 dot.setStyle ("filled");
			 dot.setFillcolor ("#DCE4FF");
			 dot.setColor ("#3C61DD");
			 name = processUrl (name);
		 }
		 
		StmtIterator stmtIt = subject.listProperties ();
		 while (stmtIt.hasNext ())
		 {
			 Statement stmt = stmtIt.next ();
			 String predicate = model.getNamespaceAbbrPlusColon (stmt.getPredicate ().getNameSpace ()) + stmt.getPredicate ().getLocalName ();
			 if (stmt.getPredicate ().getURI ().equals ("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
			 {
				 String i = model.getNamespaceAbbrPlusColon (stmt.getObject ().asResource ().getNameSpace ()) + stmt.getObject ().asResource ().getLocalName (); 
				 is.add (i);
				 if (i.equals ("prov:Entity"))
					 dot = DotProperties.getProvEntity ();
				 else if (i.equals ("prov:Activity"))
					 dot = DotProperties.getProvActivity ();
				 else if (i.equals ("prov:Association"))
				 {
					 dot = DotProperties.getProvAssociation ();
					 is.remove (is.size () - 1);
				 }
				 else if (i.equals ("prov:SoftwareAgent"))
					 dot = DotProperties.getProvSoftwareAgent ();
				 else if (i.equals ("prov:Agent"))
					 dot = DotProperties.getProvAgent ();
			 }
			 else if (stmt.getObject ().isLiteral ())
			 {
				 //literals.put (RdfModel.getName (stmt.getPredicate ()), stmt.getObject ().toString ());
				 literals.put (predicate, RdfModel.getLiteral (stmt.getObject ().asLiteral ()));
			 }
			 else if (
				 predicate.equals ("foaf:mbox") ||
				 predicate.equals ("dcterms:format") ||
				 predicate.equals ("dcterms:conformsTo")
				 )
			 {
				 literals.put (predicate, stmt.getObject ().toString ());
			 }
			 else
			 {
				 Resource object = stmt.getObject ().asResource ();
				 String obj = "";
				 if (object.isAnon ())
					 obj = object.getId ().toString ();
				 else
				 {
					 obj = model.getName (object);

					 if (!object.listProperties ().hasNext () && obj.startsWith ("http"))
					 {
						 DotProperties props = DotProperties.getDefault ();
						 props.setStyle ("filled");
						 props.setFillcolor ("#DCE4FF");
						 props.setColor ("#3C61DD");
						 props.setLabel (processUrl (obj));
						 outgoingEdges.add ("\t\"" + obj + "\" " + props.toString () + ";");
					 }
				 }
				 outgoingEdges.add ("\t\"" + name + "\" -> \"" + obj + "\" [ label=\"" + predicate + "\" ];");
			 }
		 }
		 if (!subject.isAnon ())
			 dot.setLabel (name);

	}
	
	public String toString ()
	{
		 String ret = name + System.lineSeparator ();
		 for (String i : is)
			 ret += "\tis: " + i + System.lineSeparator ();
		 for (String literal : literals.keySet ())
			 ret += "\t" + literal + " " + literals.get (literal) + System.lineSeparator ();
		 return ret;
	}
	
	public String generateDot (AlphabetIterator ai)
	{
		String ret = "\t\"" + name + "\" ";
		ret += dot.toString () + ";" + System.lineSeparator ();
		
		if (!is.isEmpty () || !literals.isEmpty ())
		{
			String rand = ai.next ();
			ret += "\t\t\"-attr" + rand + "\" [shape=\"note\",fontsize=\"10\",color=\"gray\",fontcolor=\"black\",label=<<TABLE cellpadding=\"0\" border=\"0\">" + System.lineSeparator ();
			for (String i : is)
			{
				ret += "\t\t\t<TR>" + System.lineSeparator ();
				ret += "\t\t\t\t<TD align=\"left\">type</TD>" + System.lineSeparator ();
				ret += "\t\t\t\t<TD align=\"left\">" + i + "</TD>" + System.lineSeparator ();
				ret += "\t\t\t</TR>" + System.lineSeparator ();
			}
			for (String lit : literals.keySet ())
			{
				ret += "\t\t\t<TR>" + System.lineSeparator ();
				ret += "\t\t\t\t<TD align=\"left\">" + lit + "</TD>" + System.lineSeparator ();
				ret += "\t\t\t\t<TD align=\"left\">" + literals.get (lit) + "</TD>" + System.lineSeparator ();
				ret += "\t\t\t</TR>" + System.lineSeparator ();
			}
			ret += "\t\t\t</TABLE>>]" + System.lineSeparator ();
			ret += "\t\t\"-attr" + rand + "\" -> \"" + name + "\" [style=\"dashed\",color=\"gray\",arrowhead=\"none\"];" + System.lineSeparator ();
		}
		
		
		for (String edge : outgoingEdges)
			ret += edge + System.lineSeparator ();
		
		return ret;
	}
	
	private String processUrl (String label)
	{
		if (label.contains ("identifiers.org"))
			return label;
		if (label.length () > 30)
		{
			Matcher m = Pattern.compile("(http[s]?://[^/?]+/)([^/?]*/)+([^/?]+)(\\?.*)?(#.*)?").matcher(label);
			if (m.matches())
				label = m.group (1) + "[..]/" + m.group (3);
		}
		return label;
	}
	
}

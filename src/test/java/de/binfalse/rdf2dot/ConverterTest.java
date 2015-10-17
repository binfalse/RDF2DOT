package de.binfalse.rdf2dot;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

import de.binfalse.bfutils.GeneralTools;
import de.binfalse.rdf2dot.RdfDotConverter;

/**
 * Unit test for the converter.
 * @author Martin Scharm
 */
@RunWith(JUnit4.class)
public class ConverterTest
{
	Pattern dotConnection = Pattern.compile (" -> ");
	Pattern provActivity = Pattern.compile ("\\[ (shape=\"polygon\"\\s*|sides=\"4\"\\s*|color=\"#000000\"\\s*|label=\"[^\"]\"\\s*|style=\"filled\"\\s*|fillcolor=\"#9FB1FC\"\\s*){6}\\];");
	Pattern provAgent = Pattern.compile ("\\[ (shape=\"house\"\\s*|color=\"#000000\"\\s*|label=\"[^\"]\"\\s*|style=\"filled\"\\s*|fillcolor=\"#FDB266\"\\s*){5}\\];");
	Pattern provEntity = Pattern.compile ("\\[ (shape=\"ellipse\"\\s*|color=\"#000000\"\\s*|label=\"[^\"]\"\\s*|style=\"filled\"\\s*|fillcolor=\"#FFFC87\"\\s*){5}\\];");
	Pattern provAssociatedWith = Pattern.compile ("\"[^\"]\" -> \"[^\"]\" \\[ label=\"prov:associatedWith\" \\];");
	Pattern provUsed = Pattern.compile ("\"[^\"]\" -> \"[^\"]\" \\[ label=\"prov:used\" \\];");
	
	
	
	/**
	 * Simple test.
	 */
	@Test
	public void simpleTest ()
	{
		try
		{
			File tmp = File.createTempFile ("Rdf2Dot-test-", "dot");
			tmp.delete ();
			new RdfDotConverter ().convert ("test/simplegraph.ttl", tmp.getAbsolutePath (), "BT");
			String contents = GeneralTools.fileToString (tmp);
			
			assertTrue ("expected to obtain a BT graph", contents.contains ("rankdir=\"BT\""));
			assertEquals ("expected to receive a single connection", 1, findPattern (dotConnection, contents));
			assertEquals ("expected to get a node labeled 'a'", 1, findPattern (Pattern.compile ("\"a\" \\[ [^\\]]*label=\"a\"[^\\]]* \\]"), contents));
			tmp.delete ();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail ("wasn't able to convert simple graph");
		}
	}
	
	
	
	
	/**
	 * Simple test.
	 */
	@Test
	public void simpleTest2 ()
	{
		try
		{
			File tmp = File.createTempFile ("Rdf2Dot-test-", "dot");
			tmp.delete ();
			new RdfDotConverter ().convert ("test/simple-prov.ttl", tmp.getAbsolutePath (), "LR");
			String contents = GeneralTools.fileToString (tmp);
			System.out.println (contents);
			
			assertTrue ("expected to obtain a LR graph", contents.contains ("rankdir=\"LR\""));
			assertEquals ("expected to receive two ordinary connections and three attribute connections", 5, findPattern (dotConnection, contents));
			
			assertEquals ("expected to obtain a prov:associatedWith connection", 1, findPattern (provAssociatedWith, contents));
			assertEquals ("expected to obtain a prov:used connection", 1, findPattern (provUsed, contents));

			assertEquals ("expected exactly 1 prov entity", 1, findPattern (provEntity, contents));
			assertEquals ("expected exactly 1 prov agent", 1, findPattern (provAgent, contents));
			assertEquals ("expected exactly 1 prov activity", 1, findPattern (provActivity, contents));
			
			assertFalse ("didn't expect a file name if speaking about internal stuff", contents.contains (new File ("test/simplegraph-2.ttl").getAbsoluteFile ().getParentFile ().getParentFile ().getAbsolutePath ()));
			assertFalse ("didn't expect a file name if speaking about internal stuff", contents.contains ("simplegraph"));
			assertFalse ("didn't expect a file name if speaking about internal stuff", contents.contains (tmp.getName ()));
			tmp.delete ();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail ("wasn't able to convert simple graph");
		}
	}
	
	
	

	
	private int findPattern (Pattern p, String s)
	{
		int n = 0;
		Matcher m = p.matcher (s);
		while (m.find ())
		{
			//System.out.println (p + " -> " + m.group ());
			n++;
		}
		return n;
	}
	
	
}

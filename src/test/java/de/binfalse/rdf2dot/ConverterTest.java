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
	
	private int findPattern (Pattern p, String s)
	{
		int n = 0;
		Matcher m = p.matcher (s);
		while (m.find ())
			n++;
		return n;
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
			
			assertTrue ("expected to obtain a LR graph", contents.contains ("rankdir=\"LR\""));
			assertEquals ("expected to receive two ordinary connections and three attribute connections", 5, findPattern (dotConnection, contents));
			
			assertTrue ("expected to obtain a prov:associatedWith connection", contents.contains (" [ label=\"prov:associatedWith\" ]"));
			assertTrue ("expected to obtain a prov:used connection", contents.contains (" [ label=\"prov:used\" ]"));
			assertTrue ("expected to obtain a house-agent", contents.contains (" [ shape=\"house\" "));
			assertTrue ("expected to obtain a prov entity shape", contents.contains (" [ shape=\"house\" "));
			assertTrue ("expected to obtain prov entity attributes", contents.contains (" style=\"filled\"  fillcolor=\"#FFFC87\" ]"));
			assertTrue ("expected to obtain prov activity shape", contents.contains ("[ shape=\"house\" "));
			assertTrue ("expected to obtain prov activity attributes", contents.contains (" style=\"filled\"  fillcolor=\"#FDB266\" ]"));
			
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
}

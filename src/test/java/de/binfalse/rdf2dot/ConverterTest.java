package de.binfalse.rdf2dot;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

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
			assertEquals ("expected to receive a single connection", 2, contents.split (" -> ").length);
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
			new RdfDotConverter ().convert ("test/simplegraph-2.ttl", tmp.getAbsolutePath (), "LR");
			String contents = GeneralTools.fileToString (tmp);
			System.out.println (contents);
			assertTrue ("expected to obtain a LR graph", contents.contains ("rankdir=\"LR\""));
			assertEquals ("expected to receive a single connection and 2 attribute connections", 4, contents.split (" -> ").length);
			assertTrue ("expected to obtain a prov:associatedWith connection", contents.contains (" [ label=\"prov:associatedWith\" ]"));
			assertTrue ("expected to obtain a house-agent", contents.contains (" [ shape=\"house\" "));
			assertTrue ("expected to obtain a prov entity", contents.contains (" style=\"filled\"  fillcolor=\"#FFFC87\" ]"));
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

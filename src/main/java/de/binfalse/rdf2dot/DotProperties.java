/**
 * 
 */
package de.binfalse.rdf2dot;


/**
 * @author Martin Scharm
 *
 */
public class DotProperties
{
	private String shape;
	private String color;
	private String label;
	private String style;
	private String fillcolor;
	

	protected DotProperties ()
	{
		super ();
		this.shape = "ellipse";
		this.color = "#000000";
		this.label = "";
		this.style = "";
		this.fillcolor = "";
	}
	
	public static DotProperties getProvActivity ()
	{
		DotProperties props = new DotProperties ();
		props.fillcolor = "#9FB1FC";
		props.shape = "polygon\" sides=\"4";
		props.style = "filled";
		return props;
	}
	
	public static DotProperties getProvEntity ()
	{
		DotProperties props = new DotProperties ();
		props.fillcolor = "#FFFC87";
		props.style = "filled";
		return props;
	}
	
	public static DotProperties getProvAssociation ()
	{
		DotProperties props = new DotProperties ();
		props.shape = "point\" width=\".1";
		props.color = "#303030";
		return props;
	}
	
	public static DotProperties getProvAgent ()
	{
		DotProperties props = new DotProperties ();
		props.fillcolor = "#FDB266";
		props.shape = "house";
		props.style = "filled";
		return props;
	}
	
	public static DotProperties getProvSoftwareAgent ()
	{
		DotProperties props = getProvAgent ();
		props.fillcolor = "#FD6666";
		return props;
	}
	
	public static DotProperties getDefault()
	{
		return new DotProperties ();
	}
	
	
	public String getShape ()
	{
		return shape;
	}

	
	public void setShape (String shape)
	{
		this.shape = shape;
	}

	
	public String getColor ()
	{
		return color;
	}

	
	public void setColor (String color)
	{
		this.color = color;
	}

	
	public String getLabel ()
	{
		return label;
	}

	
	public void setLabel (String label)
	{
		this.label = label;
	}

	
	public String getStyle ()
	{
		return style;
	}

	
	public void setStyle (String style)
	{
		this.style = style;
	}

	
	public String getFillcolor ()
	{
		return fillcolor;
	}

	
	public void setFillcolor (String fillcolor)
	{
		this.fillcolor = fillcolor;
	}

	private String printProperty (String name, String property)
	{
		if (property != null && property.length () > 0)
			return " " + name + "=\"" +property + "\" ";
		return "";
	}
	
	public String toString ()
	{
		return "[" 
			+ printProperty ("shape", shape)
			+ printProperty ("color", color)
			+ printProperty ("label", label)
			+ printProperty ("style", style)
			+ printProperty ("fillcolor", fillcolor) 
			+ "]";
	}
}

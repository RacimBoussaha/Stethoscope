package ca.uqac.lif.cep.methods;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

/*
BeepBeep palette for analyzing traces of method calls
Copyright (C) 2017 Raphaël Khoury, Sylvain Hallé

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.util.Scanner;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.UnaryFunction;
/**
* Function that returns the number of bytes written by a method call.
* This is done by loading a text file that stipulates all the method
* signatures (name and arguments) that perform write operations, and
* which of their arguments corresponds to the data being written in
* each case. The function returns 0 if no data is written by a method
* call.
* 
* @author Sylvain Hallé
*/
public class SaveString extends UnaryFunction<Object,Boolean> 
{
/**
 * The map associating method signatures with the index of the
 * argument corresponding to the data being written
 */
protected  BufferedWriter writer ;
protected String fileToSave;
protected ArrayList <String> m_argumentsToWatch;
Scanner scanner;
/**
 * Creates a new save object to file function instance.
 * @param scanner A scanner open on a source of text lines
 * describing the methods that write data.
 * @param pathObj A writer write an object hashcode to 
 * source of text lines
 * @see #readArgumentsToWatch(Scanner) 
 */
public SaveString(String pathObj)
{
	super(Object.class,Boolean.class);
	
	this.fileToSave=pathObj;
	
	
	

}

SaveString()
{
	this(null);
	
}

@Override
public Boolean getValue(Object x) throws FunctionException 
{
	this.scanner=new Scanner(this.getClass().getResourceAsStream("objects.txt"));
	m_argumentsToWatch = new ArrayList<String>();
	if (scanner != null)
		readArgumentsToWatch(scanner);
	java.util.Iterator<String> it = m_argumentsToWatch.iterator();
	try 
	{
		while (it.hasNext()) 
		{
			String Y=it.next();
			if (Y.trim().equals(x.toString().trim()))
			{
				return false;
			}
	}
		
		Files.write(Paths.get(fileToSave), (x.toString()+System.getProperty("line.separator")).getBytes(), StandardOpenOption.APPEND);
		//System.out.println("saved"+x );
		return true; 	
	
	} catch (Exception e) 
	{
		e.printStackTrace();
		return false;
		// TODO: handle exception
	}
}


/**
 * Reads the list of method signatures from a text source.
 * The list should be formatted as follows:
 * <pre>
 * # Blank lines and lines that begin with # are ignored
 * 
 * Class.methodName type<sub>1</sub> type<sub>2</sub> ... type<sub>n</sub> k
 * Class.methodName type<sub>1</sub> type<sub>2</sub> ... type<sub>n</sub> k
 * ...
 * </pre>
 * Where type1 is the type of the first argument (and so on), and
 * <i>k</i> is the position of the argument that contains the data
 * 
 * @param scanner A scanner open on a text source formatted as above
 */
protected void readArgumentsToWatch(Scanner scanner)
{
	while (scanner.hasNextLine())
	{
		String line = scanner.nextLine().trim();
		if (line.isEmpty() || line.startsWith("#"))
		{
			continue;
		}
		
		m_argumentsToWatch.add(line);
		
	}
}

}

package ca.uqac.lif.cep.methods;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.NothingToReturnException;
import ca.uqac.lif.cep.functions.UnaryFunction;


public class ChecknFile extends UnaryFunction<Object,Boolean> 
{

	protected ArrayList <String> m_argumentsToWatch;
	protected String fileToWatch;
	protected Scanner scanner;
		public ChecknFile(String pathObj)
	{
		super(Object.class,Boolean.class);
		this.fileToWatch=pathObj;
	}

	ChecknFile()
	{
		this(null);
	}

	@Override
	public Boolean getValue(Object x) throws FunctionException ,NothingToReturnException
	{//long startTime = System.nanoTime();
		try {
			//System.out.print(fileToWatch);
			Stream<String> stream = Files.lines(Paths.get(fileToWatch));
			m_argumentsToWatch = new ArrayList<String>();
			if (stream != null)
				readArgumentsToWatch(stream);
			java.util.Iterator<String> it = m_argumentsToWatch.iterator();
			//System.out.println("checking "+x);
			while (it.hasNext()) {
				String g = it.next();
				//System.out.println("check "+g);
				if (g.equals(x.toString().trim())){			 
					return true;
					}
				}
			return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
				// TODO: handle exception
				}
		}
	protected void readArgumentsToWatch(Stream<String> strm)
	{	m_argumentsToWatch = (ArrayList<String>) strm.collect(Collectors.toList());
	}
}

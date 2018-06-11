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
package ca.uqac.lif.cep.methods;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.methods.MethodEvent.MethodCall;

/**
 * Function that 
 * @author Boussaha Mohamed Racem
 */
public class CheckAnnotation extends UnaryFunction<MethodEvent,Boolean> 
{
	
	protected Map<MethodSignature,Integer> m_argumentsToWatch;

	
	public CheckAnnotation(Scanner scanner)
	{
		super(MethodEvent.class, Boolean.class);
		m_argumentsToWatch = new HashMap<MethodSignature,Integer>();
		if (scanner != null)
			readArgumentsToWatch(scanner);
	}

	CheckAnnotation()
	{
		this(null);
	}

	@Override
	public Boolean getValue(MethodEvent x) throws FunctionException 
	{
		if (!(x instanceof MethodCall))
		{
			return false;
		}
		MethodCall call = (MethodCall) x;
		String[] arg_types = new String[call.argumentCount()];
		for (int i = 0; i < arg_types.length; i++)
		{
		
			arg_types[i] = call.m_types[i];
		}
		MethodSignature sig = new MethodSignature(call.getMethodName(), arg_types);
		if (m_argumentsToWatch.containsKey(sig))
		{
			return true;
		}
		
		return false;
	}

	
	protected void readArgumentsToWatch(Scanner scanner)
	{
		while (scanner.hasNextLine())
		{
			String line = scanner.nextLine().trim();
			if (line.isEmpty() || line.startsWith("#"))
			{
				continue;
			}
			String[] parts = line.split("\\s+");
			String name = parts[0];
			int index = Integer.parseInt(parts[parts.length - 1]);
			String[] args = new String[parts.length - 2];
			for (int i = 1; i < parts.length - 1; i++)
			{
				args[i - 1] = parts[i];
			}
			MethodSignature sig = new MethodSignature(name, args);
			//System.err.println(sig);
			m_argumentsToWatch.put(sig, index);
		}
	}

}

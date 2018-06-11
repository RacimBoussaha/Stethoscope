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

import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.methods.MethodEvent.MethodCall;
import ca.uqac.lif.cep.methods.MethodEvent.MethodReturn;

/**
 * Function that fetches the name of the method from a method event
 * @author Sylvain Hallé
 */
public class GetMethodName extends UnaryFunction<MethodEvent,String>
{
	/**
	 * A single instance of this function
	 */
	public static final GetMethodName instance = new GetMethodName();
	
	private GetMethodName() 
	{
		super(MethodEvent.class, String.class);
	}

	@Override
	public String getValue(MethodEvent x)
	{
		if (x instanceof MethodCall)
		{
			return ((MethodCall)x).m_methodName;
		}
		
		return null;
	}
}
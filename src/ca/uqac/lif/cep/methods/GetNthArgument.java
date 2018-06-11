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

import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.InvalidArgumentException;
import ca.uqac.lif.cep.functions.NothingToReturnException;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.methods.MethodEvent.MethodCall;

/**
 * Function that fetches the <i>i</i>-th argument of a method call
 * @author Sylvain Hallé
 */
public class GetNthArgument extends UnaryFunction<MethodEvent,Object>
{
	/**
	 * The index of the argument to retrieve
	 */
	protected int m_index;
	
	GetNthArgument(int index)
	{
		super(MethodEvent.class, Object.class);
		m_index = index;
	}

	@Override
	public Object getValue(MethodEvent x) throws FunctionException
	{
		if (!(x instanceof MethodCall))
		{
			throw new NothingToReturnException(this);
		}
		if (m_index < 0 || m_index >= ((MethodCall) x).argumentCount())
		{
			throw new InvalidArgumentException(this, 0);
		}
		return ((MethodCall) x).getArgument(m_index);
	}
}
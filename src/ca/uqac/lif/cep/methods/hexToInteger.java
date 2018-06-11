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

import java.math.BigInteger;

import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.methods.MethodEvent.MethodReturn;

/**
 * Function that fetches the <i>i</i>-th argument of a method call
 * 
 * @author Sylvain Hallé
 */
public class hexToInteger extends UnaryFunction<Object, Object> {
	/**
	 * The index of the argument to retrieve
	 */

	public static final hexToInteger instance = new hexToInteger();

	public hexToInteger() {
		super(Object.class, Object.class);

	}

	@Override
	public Object getValue(Object x) throws FunctionException {
		String toCast = "";
		BigInteger value;

		if (x instanceof MethodReturn)
			toCast = ((((MethodReturn) x).getreturnValue()));
		else
			toCast = (String) x;

		if (toCast.split("@").length > 1)
			value = new BigInteger(toCast.split("@")[1], 16);
		else
			return toCast.hashCode();
		return value;

	}
}
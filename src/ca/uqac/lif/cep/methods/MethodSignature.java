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

public class MethodSignature
{
	/**
	 * The name of the method
	 */
	protected String m_name;
	
	/**
	 * The type of each argument of this method
	 */
	protected String[] m_classNames;
	
	/**
	 * Creates a new method signature
	 * @param name The name of the method
	 * @param types The type of each argument of this method
	 */
	/*@ requires
	  @  name != null && \forall(int i; i >=0 && i < types.length; types[i] != null);  
	  @*/
	public MethodSignature(String name, String ... types)
	{
		super();
		m_name = name;
		m_classNames = types;
	}
	
	/**
	 * Gets the name of the method
	 * @return The name
	 */
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * Gets the type of the <i>i</i>-th argument of this signature
	 * @param index The index of the argument
	 * @return A string representing the type of this argument
	 */
	/*@ requires index >= 0 && index < m_classNames.length; @*/
	public String getArgumentType(int index)
	{
		return m_classNames[index];
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		out.append(m_name).append("(");
		for (int i = 0; i < m_classNames.length; i++)
		{
			if (i > 0)
				out.append(", ");
			out.append(m_classNames[i]);
		}
		out.append(")");
		return out.toString();
	}
	
	@Override
	public int hashCode()
	{
		return m_name.hashCode() * m_classNames.length;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof MethodSignature))
		{
			return false;
		}
		MethodSignature ms = (MethodSignature) o;
		if (ms.m_name.compareTo(m_name) != 0 || ms.m_classNames.length != m_classNames.length)
		{
			return false;
		}
		for (int i = 0; i < m_classNames.length; i++)
		{
			if (ms.m_classNames[i].compareTo(m_classNames[i]) != 0)
			{
				return false;
			}
		}
		return true;
	}
}
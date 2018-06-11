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

public abstract class MethodEvent 
{
	/**
	 * The name of the method considered by this event
	 */
	protected  String m_id;
	public MethodEvent(String id)
	{		
		m_id= id; 
	}
	public String getId()
	{
		return m_id;
	}
	

	
	/**
	 * Event representing a method call
	 */
	public static class MethodCall extends MethodEvent
	{
		/**
		 * The parameters associated to this method call
		 */
		protected  String m_returnType;
		protected  String m_methodName;
		protected  String m_level;
		protected  String m_object;
		protected  Object[] m_arguments;
		protected  String[] m_types;
		protected  MethodReturn m_return;

		
		public MethodCall(String name,String id,String returnType,MethodReturn returnM,String level,String object, String[] types ,Object [] parameters)
		{
			super(id);
			m_methodName=name;
			m_level=level;
			m_returnType=returnType;
			m_object=object;
			//System.out.println((String)object);
			m_arguments = parameters;
			m_types=types;
			m_return=returnM;
			
		}
		
		public MethodCall(String name,String id) {
			// TODO Auto-generated constructor stub
			super(id);
			this.m_methodName=name; 
		}
		
		public String getlevel()
		{
			return m_level;
		}
		
		public String getreturnType()
		{
			return m_returnType;
		}
		
		public MethodReturn getreturn()
		{
			return m_return;
		}
		
		public String getobject()
		{
			return m_object;
		}
		/**
		 * Gets the <i>i</i>-th argument of this method call
		 * @param index The index of the argument to retrieve
		 * @return The argument
		 */
		public Object getArgument(int index)
		{	
			return m_arguments[index];
			
		}
		
		public String getMethodName()
		{
			return m_methodName;
		}
		/**
		 * Gets the number of arguments passed to this method
		 * @return The number of arguments
		 */
		public int argumentCount()
		{
			if (m_arguments==null)
				return 0;
			
			return m_arguments.length;
		}
		
		@Override
		public String toString()
		{
			return "call " + m_methodName;
		}
	}

	/**
	 * Event representing a method return
	 */
	public static class MethodReturn extends MethodEvent
	{
		protected  String m_returnValue;
		
		public MethodReturn(String m_returnValue,String id)
		{
			super(id);
			this.m_returnValue=m_returnValue;
		}
		
		public String getreturnValue()
		{
			return m_returnValue;
		}
		@Override
		public String toString()
		{
			return "output " + m_returnValue;
		}
	}
	public static class MethodKey extends MethodCall
	{
		/**
		 * The parameters associated to this method call
		 */
		//protected final Object[] m_arguments;
		protected  int KeyIndex;
		
		public MethodKey(String name,String id,String returnType,MethodReturn returnValue,String level,String object,int Kindex,String[] types , Object [] parameters)
		{
			
			super(name,id,returnType,returnValue, level,object,types,parameters);
			KeyIndex=Kindex;
			
			
		}
		
		/**
		 * Gets the <i>i</i>-th argument of this method call
		 * @param index The index of the argument to retrieve
		 * @return The argument
		 */
		
		
		public Object getKey()
		{
			return getArgument(KeyIndex);
		}
	}
}

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
import ca.uqac.lif.cep.methods.MethodEvent.*;
import ca.uqac.lif.cep.util.Arrays;
/**
 * Function that parses a line of text and creates the corresponding method
 * event
 * @author Sylvain Hallé
 */
public class StringToEvent extends UnaryFunction<String,MethodEvent>
{
	/**
	 * A single instance of this function
	 */
	public static final StringToEvent instance = new StringToEvent();
	
	private StringToEvent() 
	{
		super(String.class, MethodEvent.class);
		
	}
int i=0;
	@Override
	public MethodEvent getValue(String x)
	{
		i++;
		
		String[] parts = x.split(" // ");
		
		for (int i = 0; i < parts.length; i++) {
				
		}
		
		if (parts[0].compareToIgnoreCase("call") == 0)
		{	
			//for (int h = 0;h<parts.length-1;h++)
				//System.err.print("//hadi//"+parts[h]);
			
			Object[] args = new Object[(parts.length - 6)/2];
			String [] types= new String[(parts.length - 6)/2];
			
			//System.out.println(" Types size "+args.length);
			//System.out.println(" arguments size "+types.length);	
			
			if (!parts[3].equals("NAN")){
				
				for (int i = 3, j=0,k=4 ; i < parts.length-k; i++, j++,k++)
				{	
					types[j] = parts[i];
					//System.err.println( "types: "+types[j]+" i "+i+" j "+j+" k "+k+" parts.length-k "+(parts.length-k));	
				}
			
				
				for (int i = 3+types.length,j=0,k=4; i < parts.length-k; i++,j++)
				{	
					//System.out.println(" argument "+parts[i]+" i "+i+" j "+j+" k "+k+" parts.length-k "+(parts.length-k));	
					args[j] = parts[i];
					
				}
				
				}
			else {
				types=null;
				args =null;
			}
		//System.out.println(java.util.Arrays.toString(types));
		//System.out.println(java.util.Arrays.toString(args));
			return new MethodCall( parts[2],parts[parts.length-1], parts[1],null, parts[parts.length-2], parts[parts.length-1],types ,args);
		}
		else 
		{
			if (parts[0].compareToIgnoreCase("output") == 0)
				return new MethodReturn(parts[1].trim(),parts[parts.length-1]);
		
			else{
				
				Object[] args = new Object[(parts.length - 6)/2];
				String [] types= new String[(parts.length - 6)/2];
				int ind = Integer.parseInt(parts[1].trim());
				
				for (int i = 3; i < parts.length-3; i+=2)
				{				
					types[i - 3] = parts[i];
					
				}
				
				for (int i = 4; i < parts.length-3; i+=2)
				{			
					args[i - 4] = parts[i];
					
				}
				return new MethodKey(parts[2],parts[parts.length-1], parts[1], null, parts[parts.length-2], parts[parts.length-2],ind, types,args);
				}
			}
	}
}

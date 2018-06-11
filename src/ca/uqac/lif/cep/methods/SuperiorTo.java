package ca.uqac.lif.cep.methods;

import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.UnaryFunction;

public class SuperiorTo extends UnaryFunction<Float,Boolean>
{
	float max;
	
	public SuperiorTo(float max2)
	{
		super(Float.class, Boolean.class);
		
		this.max= max2;
	}

	
	@Override
	public Boolean getValue(Float x) throws FunctionException 
	{
		if(x>=max)
			return true;
		return false;
	}
}

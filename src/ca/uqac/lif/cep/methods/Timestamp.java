package ca.uqac.lif.cep.methods;

import ca.uqac.lif.cep.functions.FunctionException;
import ca.uqac.lif.cep.functions.UnaryFunction;

public class Timestamp extends UnaryFunction<Object, Number> 
{
	protected long m_offset = 0;
	
	protected float m_scale = 0;
	
	public Timestamp(float scale, long offset)
	{
		super(Object.class, Number.class);
		m_scale = scale;
		m_offset = offset;
	}
	
	public Timestamp()
	{
		this(1f, 0);
	}

	@Override
	public Number getValue(Object x) throws FunctionException 
	{
		return (System.currentTimeMillis() - m_offset) * m_scale;
	}

}

package ca.uqac.lif.cep.methods;

import java.util.ArrayDeque;
import java.util.Queue;

import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.methods.MethodEvent.MethodReturn;
import ca.uqac.lif.cep.SingleProcessor;

public class GetOutputs extends SingleProcessor {
	public GetOutputs() {
		super(1, 1);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) {
		Object o = inputs[0];
		Object[] out = new Object[1];

		if (o instanceof MethodReturn) {
			out[0] = (MethodReturn) o;
		} else {
			return true;
		}
		outputs.add(out);
		return true;

	}

	public static void build(ArrayDeque<Object> stack) throws ConnectorException {
		// TODO
	}

	@Override
	public Processor clone() {
		// TODO Auto-generated method stub
		return null;
	}

}

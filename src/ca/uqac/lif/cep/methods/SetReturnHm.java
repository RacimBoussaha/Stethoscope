package ca.uqac.lif.cep.methods;

import java.util.HashMap;
import ca.uqac.lif.cep.functions.UnaryFunction;
import ca.uqac.lif.cep.methods.MethodEvent.MethodCall;
import ca.uqac.lif.cep.methods.MethodEvent.MethodReturn;

public class SetReturnHm extends UnaryFunction<MethodReturn,MethodCall> {
	
	private HashMap<Integer,MethodEvent> hm = new HashMap<Integer,MethodEvent>();
	private Class<?> clazz;

	public SetReturnHm(HashMap<Integer,MethodEvent> objects){
		super(MethodReturn.class, MethodCall.class);	
		clazz = objects.getClass();
		this.hm = objects;	
		}
	
	@Override
	public MethodCall getValue(MethodReturn x){
		MethodCall call=null;
		if (hm.containsKey(Integer.parseInt((x.getId()).replaceAll("[^\\d.]", "")))){
			call=(MethodCall)(hm.get(Integer.parseInt((x.getId()).replaceAll("[^\\d.]", ""))));
			call.m_return=x;
		}
		return call;
		}
	}
		
	

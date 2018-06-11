package ca.uqac.lif.cep.methods;

import java.util.HashMap;
import ca.uqac.lif.cep.functions.UnaryFunction;

public class SaveCallHashMap extends UnaryFunction<MethodEvent,Boolean> {
	
	private HashMap<Integer,MethodEvent> hm = new HashMap<Integer,MethodEvent>();
	
	public SaveCallHashMap(HashMap<Integer,MethodEvent> objects){
		super(MethodEvent.class, Boolean.class);	
		this.hm = objects;	
		}
	@Override
	public Boolean getValue(MethodEvent x){
		hm.put(Integer.parseInt((x.m_id).replaceAll("[^\\d.]", "")), x);
		return true;
	}
}

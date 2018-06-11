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

import java.util.ArrayDeque;
import java.util.Queue;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SingleProcessor;

/**
 * Stack that keeps track of the nesting of method calls in an event trace.
 * It takes two event streams as its input: the first is a stream of events
 * of an arbitrary type; the second (top side) is a stream of Boolean values. 
 * This processor internally maintains a stack of received events. To this
 * end, the Boolean stream acts as a <em>push flag</em>. When an event <i>e</i> and 
 * a Boolean value b arrive at the processor's inputs, two situations may 
 * occur. If b is true, the top of the stack (if not empty) is output but not 
 * removed, and <i>e</i> is then pushed onto the internal stack.
 * If b is false, <i>e</i> is ignored, and the element at the top of the
 * internal stack is popped and discarded. The processor outputs nothing
 * if the stack is empty when queried for an output.
 * 
 * @author Sylvain Hallé
 *
 */
public class Stack extends SingleProcessor 
{
	/**
	 * The stack that will hold the objects
	 */
	protected ArrayDeque<Object> m_stack;
	
	Stack()
	{
		super(2, 1);
		m_stack = new ArrayDeque<Object>();
	}
	
	public Stack(Object start_event)
	{
		this();
		m_stack.push(start_event);
	}

	@Override
	protected boolean compute(Object[] inputs, Queue<Object[]> outputs) 
	{
		if ((Boolean) inputs[1])
		{
			push(outputs);
			m_stack.push(inputs[0]);
		}
		else
		{
			m_stack.pop();
			push(outputs);
		}
		return true;
	}
	
	/**
	 * Peeks the top of the stack and pushes it as the output of the processor 
	 * @param outputs The output queue of this processor
	 */
	protected void push(Queue<Object[]> outputs)
	{
		if (!m_stack.isEmpty())
		{
			Object[] out = new Object[1];
			out[0] = m_stack.peek();
			outputs.add(out);
		}
	}
	
	/**
	 * Gets the size of the internal stack
	 * @return The size
	 */
	public int getSize()
	{
		return m_stack.size();
	}
	
	/**
	 * Checks if the internal stack is empty
	 * @return {@code true} if the stack is empty, {@code false}
	 * otherwise
	 */
	public boolean isEmpty()
	{
		return m_stack.isEmpty();
	}

	@Override
	public Stack clone() 
	{
		Stack s = new Stack();
		s.m_stack = m_stack.clone();
		return s;
	}

	
}

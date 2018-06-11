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


import static ca.uqac.lif.cep.Connector.INPUT;
import static ca.uqac.lif.cep.Connector.OUTPUT;
import static ca.uqac.lif.cep.Connector.connect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.Connector.ConnectorException;
import ca.uqac.lif.cep.functions.ArgumentPlaceholder;
import ca.uqac.lif.cep.functions.Constant;
import ca.uqac.lif.cep.functions.CumulativeFunction;
import ca.uqac.lif.cep.functions.CumulativeProcessor;
import ca.uqac.lif.cep.functions.Equals;
import ca.uqac.lif.cep.functions.Function;
import ca.uqac.lif.cep.functions.FunctionProcessor;
import ca.uqac.lif.cep.functions.FunctionTree;
import ca.uqac.lif.cep.io.LineReader;
import ca.uqac.lif.cep.methods.*;
import ca.uqac.lif.cep.methods.MethodEvent.MethodCall;
import ca.uqac.lif.cep.numbers.Addition;
import ca.uqac.lif.cep.sets.Multiset;
import ca.uqac.lif.cep.sets.MultisetUnion;
import ca.uqac.lif.cep.sets.ToList;
import ca.uqac.lif.cep.sets.Wrap;
import ca.uqac.lif.cep.tmf.Filter;
import ca.uqac.lif.cep.tmf.Fork;

/**
 * Chain of processors counting total bytes written by all methods in the
 * execution of the program
 * 
 * @author Sylvain Hallé
 */
public class SecurityConstraints {
	
	public Pullable LimitBytesWrittenTotal(File Trace, File Signature, float max) {
		
		Pullable p = null;
		try {
			
			LineReader feeder = new LineReader(new FileInputStream(Trace));
			Fork f1 = new Fork(2);
			Fork f2 = new Fork(1);
			//Fork f2 = new Fork(2);
			Filter fil1 = new Filter();
			FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
			connect(feeder, converter);
			connect(converter, f1);
			connect(f1, 0, fil1, 0);
			Function byte_count = new ByteCount(new Scanner(new FileInputStream(Signature)));
			Function max_fct = new SuperiorTo(max);
			FunctionProcessor byte_count_p = new FunctionProcessor(byte_count);
			FunctionProcessor max_fct_processor = new FunctionProcessor(max_fct);
			connect(f1, 1, byte_count_p, 0);
			CumulativeProcessor sum = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			connect(byte_count_p, sum);
			connect(sum, f2);
			connect(f2, max_fct_processor);
			connect(max_fct_processor, 0, fil1, 1);

			p = fil1.getPullableOutput();

		} catch (ConnectorException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// long stopTime = System.currentTimeMillis();
		// long elapsedTime = stopTime - startTime;
		// System.out.println("temp d'execution "+elapsedTime+" Millis ");
		return p;

	}

	public Pullable TotalBytesWritten(File Trace, File Signature) {
		// Pull
		Pullable p = null;
		try {
			long startTime = System.currentTimeMillis();
			String filename = "Trace.txt";
			// Setup source from an input file

			LineReader feeder = new LineReader(new FileInputStream(Trace));
			FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
			connect(feeder, converter);
			Function byte_count = new ByteCount(new Scanner(new FileInputStream(Signature)));
			FunctionProcessor byte_count_p = new FunctionProcessor(byte_count);
			connect(converter, byte_count_p);
			CumulativeProcessor sum = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			connect(byte_count_p, sum);

			p = sum.getPullableOutput();

		} catch (ConnectorException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// long stopTime = System.currentTimeMillis();
		// long elapsedTime = stopTime - startTime;
		// System.out.println("temp d'execution "+elapsedTime+" Millis ");
		return p;

	}

	public Pullable CallGraphPipe(File Trace)  {
		Pullable p = null;

			// Setup source from an input file
			LineReader feeder;
			try {
				feeder = new LineReader(new FileInputStream(Trace));
			
			FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
			connect(feeder, converter);
			Fork f5 = new Fork(4);
			connect(converter, f5);
			FunctionProcessor is_call = new FunctionProcessor(new FunctionTree(Equals.instance,
					new FunctionTree(GetEventType.instance, new ArgumentPlaceholder(0)), new Constant("call")));
			connect(f5, 0, is_call, 0);
			Fork f2 = new Fork(4);
			connect(is_call, f2);
			Filter fil_1 = new Filter();
			connect(f5, 1, fil_1, 0);
			connect(f2, 0, fil_1, 1);
			Stack stack = new Stack(new MethodEvent.MethodCall("main", null));
			connect(f5, 2, stack, 0);
			connect(f2, 2, stack, 1);
			Filter fil_2 = new Filter();
			connect(stack, OUTPUT, fil_2, 0);
			connect(f2, 1, fil_2, 1);
			FunctionProcessor f_name_1 = new FunctionProcessor(GetMethodName.instance);
			connect(fil_1, f_name_1);
			FunctionProcessor f_name_2 = new FunctionProcessor(GetMethodName.instance);
			connect(fil_2, f_name_2);
			FunctionProcessor to_list = new FunctionProcessor(new ToList(String.class, String.class));
			connect(f_name_2, OUTPUT, to_list, 0);
			connect(f_name_1, OUTPUT, to_list, 1);
			CumulativeProcessor union = new CumulativeProcessor(
					new CumulativeFunction<Multiset>(MultisetUnion.instance));
			FunctionProcessor wrap = new FunctionProcessor(Wrap.instance);
			connect(to_list, wrap);
			connect(wrap, union);
			// Pull
			p = union.getPullableOutput();
			} catch (FileNotFoundException | ConnectorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return p;
	}
	public Pullable SetCallsReturn(File Trace)  {
		Pullable p = null;
		try {
		HashMap<Integer, MethodEvent> hm = new HashMap<Integer, MethodEvent>();

		Function save_Hs = new SaveCallHashMap(hm);
		Function setReturn_Hs = new SetReturnHm(hm);

		LineReader feeder = new LineReader(new FileInputStream(Trace));

		FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
		GetCalls get_CallsPros = new GetCalls();
		GetOutputs get_OutputsPros = new GetOutputs();
		FunctionProcessor saveHs = new FunctionProcessor(save_Hs);
		FunctionProcessor setReturnHs = new FunctionProcessor(setReturn_Hs);

		Fork fork1 = new Fork(4);
		connect(feeder, converter);
		connect(converter, fork1);
		connect(fork1, 0, get_CallsPros, 0);
		connect(fork1, 1, get_OutputsPros, 0);
		connect(get_CallsPros, saveHs);
		connect(get_OutputsPros, setReturnHs);

		Pullable r = saveHs.getPullableOutput();
		p = setReturnHs.getPullableOutput();

		for (Object o : r) {
			//System.out.println(o);
		}
	} catch (ConnectorException | FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	// long stopTime = System.currentTimeMillis();
	// long elapsedTime = stopTime - startTime;
	// System.out.println("temp d'execution "+elapsedTime+" Millis ");
	return p;
	}

	
	
	public Pullable custom (File Trace, File Signature) {
		// Pull
		Pullable p = null;
		try {
			
			// Setup source from an input file

			FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
			LineReader feeder = new LineReader(new FileInputStream(Trace));
			Function check_anno = new CheckAnnotation(new Scanner(new FileInputStream(Signature)));
			FunctionProcessor check = new FunctionProcessor(check_anno);
			connect(feeder, converter);
			connect(converter, check);
			p = check.getPullableOutput();

		} catch (ConnectorException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// long stopTime = System.currentTimeMillis();
		// long elapsedTime = stopTime - startTime;
		// System.out.println("temp d'execution "+elapsedTime+" Millis ");
		return p;

	}
	
	public static void main(String[] args) throws FileNotFoundException, ConnectorException  {
		//for (int i=0;i<11;i++) {
		//File file2 = new File(SecurityConstraints.class.getResource("Trace.txt").getFile());
		//File f2 = new File(SecurityConstraints.class.getResource("write-signatures.txt").getFile());
		File file2 = new File("Trace1.txt");
		File f2 = new File(SecurityConstraints.class.getResource("Signatures/LimitBytesWrittenTotal-Signatures.txt").getFile());
		
		SecurityConstraints df = new SecurityConstraints();
		
		long startTime1=System.currentTimeMillis();
		//for (Object o : df.CallGraphPipe(file2)) {
		// for (Object o : df.TotalBytesWritten(file2,f2)) {
		//for (Object o : df.LimitBytesWrittenTotal(file2, f2, 10)) {
		//for (Object o : df.SetCallsReturn(file2)) {
			for (Object o : df.SetCallsReturn(file2)) {
				System.out.println(" Call: " + o +" ID: "+((MethodCall)o).getId()+" Output: "+((MethodCall)o).getreturn()+ "\n");
			//Scanner keyboard = new Scanner(System.in);
			//int myint = keyboard.nextInt();
		}
		long stopTime1 = System.currentTimeMillis();
		long elapsedTime1 = stopTime1 - startTime1;
		System.out.print(elapsedTime1+","); }
		
	//}

}

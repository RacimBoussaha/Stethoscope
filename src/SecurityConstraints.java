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



public class SecurityConstraints {
	
	public Pullable LimitBytesWrittenTotal(File Trace, File Signature, float max) {

		Pullable p = null;
		try {
			LineReader feeder = new LineReader(new FileInputStream(Trace));
			Fork f1 = new Fork(2);
			Fork f2 = new Fork(1);
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

		return p;
	}

	public Pullable TotalBytesWritten(File Trace, File Signature) {
		
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

	return p;
	}
	
		public Pullable SafeLock(File Trace,File sigAck,File sigRel)  {
		Pullable p = null;
		try {

			FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
			Function acq = new CheckAnnotation(new Scanner(sigAck));
			Function rels = new CheckAnnotation(new Scanner(sigRel));
			Function bToI = new booleanToint();
			Function bToI1 = new booleanToint();

			Fork f1 = new Fork(2);

			LineReader feeder = new LineReader(new FileInputStream(Trace));

			FunctionProcessor check_acq_p = new FunctionProcessor(acq);
			FunctionProcessor check_rels_p = new FunctionProcessor(rels);
			FunctionProcessor bTi_acq_p = new FunctionProcessor(bToI);
			FunctionProcessor bTi_rels_p = new FunctionProcessor(bToI1);
			FunctionProcessor to_list = new FunctionProcessor(new ToList(Number.class, Number.class));
			//ApplyFunction 
			CumulativeProcessor total1 = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			CumulativeProcessor total2 = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));

			connect(feeder, converter);
			connect(converter, f1);
			connect(f1, 1, check_acq_p,INPUT);
			connect(f1, 0, check_rels_p,INPUT);


			connect(check_acq_p,bTi_acq_p);
			connect(check_rels_p,bTi_rels_p);
			connect(bTi_acq_p,total1);
			connect(bTi_rels_p,total2);

			connect(total1,0,to_list,0);
			connect(total2,0,to_list,1);



			p = to_list.getPullableOutput();

		} catch (ConnectorException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return p;
	}

	public Pullable AsReadAsWrite(File trace,File sigRead,File sigWrite)  {
		Pullable p = null;
		try {

			FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
			Function bToI = new booleanToint();
			Function bToI1 = new booleanToint();
			Fork f1 = new Fork(2);
			LineReader feeder = new LineReader(new FileInputStream(trace));
			FunctionProcessor byteWr_count = new FunctionProcessor(new ByteCount(new Scanner(new FileInputStream(sigWrite))));
			FunctionProcessor byteRe_count = new FunctionProcessor(new ByteCount(new Scanner(new FileInputStream(sigRead))));
			FunctionProcessor superio_to = new FunctionProcessor(new SuperiorTo());
			
			//ApplyFunction 
			CumulativeProcessor totalW = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));
			CumulativeProcessor totalR = new CumulativeProcessor(new CumulativeFunction<Number>(Addition.instance));

			connect(feeder, converter);
			connect(converter, f1);
			connect(f1, 1, byteWr_count,INPUT);
			connect(f1, 0, byteRe_count,INPUT);
			
				
			connect(check_acq_p,bTi_acq_p);
			connect(check_rels_p,bTi_rels_p);
			connect(bTi_acq_p,total1);
			connect(bTi_rels_p,total2);
			
			connect(total1,0,to_list,0);
			connect(total2,0,to_list,1);



			p = to_list.getPullableOutput();

		} catch (ConnectorException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// long stopTime = System.currentTimeMillis();
		// long elapsedTime = stopTime - startTime;
		// System.out.println("temp d'execution "+elapsedTime+" Millis ");
		return p;
	}
	
	public Pullable NoSendAfterReading(File Trace,File readSigFile,File sendSigFile)  {
		Pullable p = null;
		try {

			FunctionProcessor converter = new FunctionProcessor(StringToEvent.instance);
			Fork f1 = new Fork(2);
			LineReader feeder = new LineReader(new FileInputStream(Trace));
			FunctionProcessor check_read_p = new FunctionProcessor(new CheckAnnotation(new Scanner(readSigFile)));
			FunctionProcessor check_send_p = new FunctionProcessor(new CheckAnnotation(new Scanner(sendSigFile)));
			FunctionProcessor oneR= new FunctionProcessor(new oneRealised());
			Cumulate and = new Cumulate(
					new CumulativeFunction<Boolean>(Booleans.and));
			connect(feeder, converter);
			connect(converter, f1);
			connect(f1, 1, check_read_p,INPUT);
			connect(f1, 0, check_send_p,INPUT);
			connect(check_read_p,oneR);
			connect(oneR,0,and,0);
			connect(check_send_p,0,and,1);

			p = and.getPullableOutput();

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
		
		return p;

	}
}

import org.aspectj.lang.Signature;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.aspectj.lang.reflect.MethodSignature;





public aspect testAspectBefore {

	private int _callDepth = -1;
	private String returnType = " ";
	ArrayList<ArrayList<String>> traceArray = new ArrayList<ArrayList<String>>();
	ArrayList<String> contentArray;
	ArrayList<String> argsArray;
	ArrayList<String> argsValues;
	Object target;
	String traceBefore = "";
	FileWriter fileWriter ;
	File file2;
	FileWriter fileWriter2 ;

	Class[] paramTypes;
	int i=0;
	public testAspectBefore(){
		file2 = new File("Trace1.txt");
		
		try {
		
			fileWriter2 = new FileWriter(file2);
		fileWriter2.write("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	pointcut traceMethods()
	: /*execution(* MyLine.*(..)) && */ !preinitialization(* .new(..))&&!staticinitialization(*)
	&& !get(* *.*) && !set(* *.*) && !within(testAspectBefore) && !within(SecurityConstraints) && !within(StethoUI) && !within(Trace) &&
	!within(ca.uqac..*)&& !call (* ca.uqac..*(..)) && !within(SecurityProcessors.*)&& !call (* SecurityProcessors..*(..)) 
	 && !initialization(* .new(..)) ;

	
	before() : traceMethods() {

		_callDepth++;
	}

	before(): traceMethods() {
		try {
			fileWriter2.write("");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int id=thisJoinPoint.hashCode();
		Object[] paramValues = thisJoinPoint.getArgs();
		//System.out.println((thisJoinPointStaticPart.getSignature()).get	);
		//paramTypes = ((CodeSignature) thisJoinPointStaticPart.getSignature()).getParameterTypes();
		paramTypes = new Class[paramValues.length]; 
		int i=0;
		if (paramValues.length>0){
		for (Object temp : paramValues) {
			
			try {
				paramTypes[i]= temp.getClass();
			} catch (Exception e) {
				// TODO Auto-generated catch block
			
			}
			i++;
			
		}}
		//paramTypes=al;
		//System.out.println("jjj "+thisJoinPointStaticPart.getSignature());
		Signature sig = thisJoinPointStaticPart.getSignature();
		this.target= thisJoinPoint.getTarget();
		

		try {
			print(sig, paramTypes, paramValues,id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	Object around(): traceMethods() 

	 {
				
		Object returnValue = proceed();
	
		if (returnValue!=null ){
			
			try {
				//fileWriter2.write("id // "+thisJoinPoint.hashCode()+"\n");
				fileWriter2.write("output // "+returnValue.toString()+" // "+thisJoinPoint.hashCode()+"\n");
				fileWriter2.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
			
			
		
		}
		else 
{
			
			try {
				//fileWriter2.write("id // "+thisJoinPoint.hashCode()+"\n");
				fileWriter2.write("output // "+"NAN // "+thisJoinPoint.hashCode()+"\n");
				fileWriter2.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
			
			
		
		}
		return returnValue;
	}
	
	
	
	after() : traceMethods() {

		_callDepth--;

	}
		private void print(Signature sig, Class[] paramsType, Object[] paramValues, int id) {

		// System.out.println("paramsType.length : "+paramsType.length);
		// System.out.println("paramValues.length : "+paramValues.length);

			
			
		
			
			if (sig instanceof MethodSignature) {
		  	 
		  	  Method method = ((MethodSignature)sig).getMethod();
		  	returnType = method.getReturnType().toString();
		  	
		  	}
		
		traceBefore = "";
		String methode = sig.getName();
		traceBefore = traceBefore +returnType+" // " + sig.getDeclaringType().getName() + "." + methode ;

		if (paramsType.length == 0) {

			traceBefore = traceBefore + " // NAN ";
		} else {

			for (int i = 0; i < paramsType.length; i++) {
				// args = args + paramsType[i].toString()+" ";
				try {
					traceBefore = traceBefore + " // " + paramsType[i].getName() ;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					traceBefore = traceBefore + " // NAN ";
				}
				
			}

		}

		
		String paramLen ="";
		if (paramValues.length == 0) {

			traceBefore = traceBefore + "// NAN";
		} else {
			for (int i = 0; i < paramValues.length; i++) {
				
				if(methode.equals("write") && i==0){
					
					paramLen = "//"+paramValues[0];
					
				}
				
				if(methode.equals("readline") && i==0){
					
					paramLen = ""+paramValues[0];
					
				}
				traceBefore = traceBefore + " // "+ paramValues[i]  ;
				

			}
		}
		
		if(methode.equals("write")||methode.equals("readline")){traceBefore = traceBefore +" // "+paramLen.getBytes().length; 
		}
		else{traceBefore = traceBefore  + " // NAN"; 
		}
		
		traceBefore = traceBefore + " // " +_callDepth;
		if (target!=null)
			{traceBefore = traceBefore + " // "+ System.identityHashCode(target);}
		else{traceBefore = traceBefore  + " // NAN "; 
		}
		
		try {
			
			
			
			fileWriter2.write("call // "+traceBefore+" // "+id+"\n");
			
			fileWriter2.flush();
			//fileWriter.close();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
       
		//System.out.println(traceBefore);

	}

	
			
		




}
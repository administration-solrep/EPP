package fr.sword.naiad.commons.core.utils;

/**
 * Utility class that tracks the call stack.
 * 
 * Usage :
 * WhoCalledMe.whoCalledMe
 * WhoCalledMe.getCallStack
 * 
 * @author fbarmes
 *
 */
public class WhoCalledMe {

	private static final int STACK_START_OFFSET = 2;
	
	/**
	 * get the caller of my caller
	 * @return
	 */
	public static String whoCalledMe() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement caller = stackTraceElements[STACK_START_OFFSET];
		String classname = caller.getClassName();
		String methodName = caller.getMethodName();
		int lineNumber = caller.getLineNumber();
		return classname + "." + methodName + ":" + lineNumber;
	}

	
	
	/**
	 * get the entire call stack to the caller of this method
	 * @return
	 */	
	public static String getCallStack() {
		
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		
		sb.append("\n");
		for (int i = stackTraceElements.length-1; i >=STACK_START_OFFSET ; i--) {
			StackTraceElement ste = stackTraceElements[i];
			String classname = ste.getClassName();
			String methodName = ste.getMethodName();
			String filename = ste.getFileName();
			int lineNumber = ste.getLineNumber();
			
			
			String out = "\t at ${classname}.${methodName}(${filename}:${line})\n";
			out = out.replace("${classname}", classname)
				.replace("${methodName}", methodName)
				.replace("${filename}", filename)
				.replace("${line}", Integer.toString(lineNumber));
			
			sb.append(out);			
		}
		return sb.toString();
	}
	
	
	/**
	 * get the partial call stack to the caller of this method
	 * @param limit 
	 * @return
	 */
	public static String getCallStack(int limit) {
		
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		
		sb.append("\n");
		int start = STACK_START_OFFSET + limit;
		
		if(start > stackTraceElements.length-1) {
			start = stackTraceElements.length-1;
		}
		
		for (int i = start; i >=STACK_START_OFFSET ; i--) {
			StackTraceElement ste = stackTraceElements[i];
			String classname = ste.getClassName();
			String methodName = ste.getMethodName();
			String filename = ste.getFileName();
			int lineNumber = ste.getLineNumber();
			
			
			String out = "\t at ${classname}.${methodName}(${filename}:${line})\n";
			out = out.replace("${classname}", classname)
				.replace("${methodName}", methodName)
				.replace("${filename}", filename)
				.replace("${line}", Integer.toString(lineNumber));
			
			sb.append(out);			
		}
		return sb.toString();
	}
	
}

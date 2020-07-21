package fr.dila.st.utils;

public class WhoCalledMe {

	public static void main(String[] args) {
		f();
	}

	public static void f() {
		g();
	}

	public static void g() {
		// showCallStack();
		// System.out.println();
		// System.out.println("g() was called by " + whoCalledMe());
	}

	public static String whoCalledMe() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement caller = stackTraceElements[4];
		String classname = caller.getClassName();
		String methodName = caller.getMethodName();
		int lineNumber = caller.getLineNumber();
		return classname + "." + methodName + ":" + lineNumber;
	}

	public static String getCallStack() {

		StringBuffer sb = new StringBuffer();

		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (int i = 2; i < stackTraceElements.length; i++) {
			StackTraceElement ste = stackTraceElements[i];
			String classname = ste.getClassName();
			String methodName = ste.getMethodName();
			int lineNumber = ste.getLineNumber();
			sb.append("\t" + classname + "." + methodName + ":" + lineNumber + "\n");
		}
		return sb.toString();
	}
}

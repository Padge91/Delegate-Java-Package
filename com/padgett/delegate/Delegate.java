package com.padgett.delegate;

import java.lang.reflect.*;
import java.util.ArrayList;

public class Delegate {
	
	/*USAGE:
	 * Use invoke() for methods with no return type(void)
	 * Use call() for methods with a return type other than void
	 * If multiple methods in delegate; must have same return type
	 * Overloaded methods are not reliable within delegate
	 * Return values are from most recently called method
	 */
	
	private ArrayList<Object> objectList = new ArrayList<Object>();
	private ArrayList<Method> methodList = new ArrayList<Method>();
	private ArrayList<String> stringList = new ArrayList<String>();
	private int size = 0;
	
	//default empty constructor
	public Delegate() {
	}
	
	//constructor with another Delegate as constructor
	public Delegate(Delegate d) throws DelegateException {
		add(d);
	}
	
	//constructor with object and string for method name
	public Delegate(Object o, String s) throws DelegateException {
		add(o, s);
	}
	
	//add method to add a method to delegate with a delegate object as parameter
	public void add(Delegate d) throws DelegateException {
		if (d == null) {
			throw new DelegateException("Parameters must not be null");
		}
		else if (d.getSize() >= 1) {
			for (int i = 0; i < d.getSize(); i++) {
				addToList(d.getObject(i), d.getMethod(i), d.getString(i));
			}
		}
		else {
			throw new DelegateException("Delegate must be of size greater than or equal to 1.");

		}
	}
	
	//add method to add a method to delegate with object and string as parameters
	public void add(Object o, String s) throws DelegateException {
		if ((o == null) || (s == null) || (s.length() == 0)) {
			throw new DelegateException("Parameters must not be null. String length must be greater than 0.");
		}
		
		Method m = null;
		Method[] mList = o.getClass().getDeclaredMethods();
		
		if (mList.length == 0) {
			throw new DelegateException("No declared methods found.");
		}
		
		for (Method me: mList) {
			if (me.getName().equals(s)){
				 m = me;
			}
		}
		
		if (m == null) {
			throw new DelegateException("Method name not found.");
		}
		
		addToList(o, m, s);
	}
	
	//equal method to set the method to call to a delegate
	public void setTo(Delegate d) throws DelegateException {
		removeAll();
		add(d);
	}
	
	//equal method to set method to call a object
	public void setTo(Object o, String s) throws DelegateException {
		removeAll();
		add(o, s);
	}

	//remove method to remove a specific delegate
	public void remove(Delegate d) throws DelegateException {
		boolean found = false;
		
		if (d == null) {
			throw new DelegateException("Paramaters must not be null.");
		}
		
		for (int i = 0; i < size; i++) {
			if (d.getString(0).equals(stringList.get(i))){
				removeFromList(i);
				found = true;
			}
		}
		
		if (!found) {
			throw new DelegateException("Delegate method not found");
		}
	}
	
	//remove method to remove a method from the delegate
	public void remove(Object o, String s) throws DelegateException {
		boolean found = false;
		
		if ((o == null) || (s == null) || (s.length() == 0)){
			throw new DelegateException("Paramaters must not be null. String length must not be 0.");
		}
		
		for (int i = 0; i < size; i++) {
			if (s.equals(stringList.get(i))){
				removeFromList(i);
				found = true;
			}
		}
		
		if (!found) {
			throw new DelegateException("Delegate method not found");
		}
		
	}
	
	//invoke method to pass in arguments
	/***** NO RETURN VALUE 
	 * @throws DelegateException *****/
	public void invoke(Object...args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, DelegateException {
		for (int i = 0; i < size; i++) {
			try {
				methodList.get(i).invoke(objectList.get(i), args);
			}
			catch (IllegalArgumentException e) {
				throw new DelegateException("Illegal Argument Exception. All paramaters must have same signature.");
			}
		}
	}
	
	//invoke method with no parameters passed
	/***** NO RETURN VALUE 
	 * @throws DelegateException *****/
	public void invoke() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, DelegateException {
		for (int i = 0; i < size; i++) {
			try {
				methodList.get(i).invoke(objectList.get(i), (Object[])null);
			}
			catch(IllegalArgumentException e) {
				throw new DelegateException("Illegal Argument Exception. All paramaters must have same signature.");
			}
		}
	}
	
	//call method to pass in parameters
	@SuppressWarnings("unchecked")
	public <T> T call(Object...args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		T returnValue = null;		
		
		for (int i = 0; i < size; i++) {
			returnValue = (T)methodList.get(i).invoke(objectList.get(i), args);
		}
		return returnValue;
		
	}
	
	//call method with no parameters passed
	@SuppressWarnings("unchecked")
	public <T> T call() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		T returnValue = null;
		
		for (int i = 0; i < size; i++) {
			returnValue = (T)methodList.get(i).invoke(objectList.get(i), 0);
		}
		return returnValue;
	}
	
	private void removeFromList(int i) {
		objectList.remove(i);
		methodList.remove(i);
		stringList.remove(i);
		size--;
	}
	
	private void addToList(Object o, Method m, String s) throws DelegateException{
		//enforce that method has same return types and signatures
		
		if (size > 0) {
			if (!m.getReturnType().equals(methodList.get(0).getReturnType())){
				throw new DelegateException("Method return type mis-match. All methods must share same return type.");
			}
		
			if (!m.getParameterTypes().equals(methodList.get(0).getParameterTypes())){
				throw new DelegateException("Method paramater signature mis-match. All methods must share same paramaters.");
				
			}
		}
		
		objectList.add(o);
		methodList.add(m);
		stringList.add(s);
		size++;	
	}
	
	//returns number of methods held in delegate
	public int getSize() {
		return size;
	}
	
	//get the method name at position i
	public String getString(int i) {
		return stringList.get(i);
	}
	
	//get method object at position i
	public Method getMethod(int i) {
		return methodList.get(i);
	}
	
	//get object to call method on at position i
	public Object getObject(int i) {
		return objectList.get(i);
	}
	
	//reset the delegate
	public void removeAll() {
		objectList.removeAll(objectList);
		methodList.removeAll(methodList);
		stringList.removeAll(stringList);
		size = 0;
	}
	
}

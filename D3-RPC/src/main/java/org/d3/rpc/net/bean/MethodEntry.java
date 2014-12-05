package org.d3.rpc.net.bean;

import java.lang.reflect.Method;

import org.d3.rpc.util.Reflections;

public class MethodEntry {
	
	private String 		name;
	private String 		paramTypes;
	private Object[] 	args;
	
	public MethodEntry(Method method, Object[] args){
		this.name = method.getName();
		this.paramTypes = Reflections.paramType2String(method);
		this.args = args;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParamTypes() {
		return paramTypes;
	}

	public void setParamTypes(String paramTypes) {
		this.paramTypes = paramTypes;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((paramTypes == null) ? 0 : paramTypes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodEntry other = (MethodEntry) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (paramTypes == null) {
			if (other.paramTypes != null)
				return false;
		} else if (!paramTypes.equals(other.paramTypes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MethodEntry [name=" + name + ", paramTypes=" + paramTypes + "]";
	}
	
}

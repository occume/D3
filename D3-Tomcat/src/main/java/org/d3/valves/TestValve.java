package org.d3.valves;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Loader;
import org.apache.catalina.Manager;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.d3.std.Printer;

public class TestValve extends ValveBase {

	private Context context;

	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {
		System.out.println(context.getClass().getClassLoader());
		System.out.println(context.getParentClassLoader());
		System.out.println(context.getParent().getClass().getClassLoader());
		System.out.println(context.getParent().getParent().getClass().getClassLoader());
		getNext().invoke(request, response);
	}

	private void printWebResource(WebResourceSet[] wrset) {
		for (WebResourceSet set : wrset) {
			System.out.println("baseUrl: " + set.getBaseUrl());
			System.out.println("list: " + Arrays.toString(set.list("/")));
		}
	}

	@Override
	protected void initInternal() throws LifecycleException {
		super.initInternal();
		if (getContainer() instanceof Context) {
			context = (Context) getContainer();
		}
		containerLog = getContainer().getLogger();
	}

	@Override
	public void backgroundProcess() {
		// System.out.println("--- backgroundProcess in TestValve ---");
	}

	public static void main(String... strings) throws IOException,
			ServletException {
		new TestValve().invoke(null, null);
	}
}

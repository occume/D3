package org.d3.monitor.marshal.service.provider;

import org.d3.monitor.service.Report;
import org.d3.monitor.vo.Usage;

public class ReportProvider implements Report{

	@Override
	public void report(Usage usage) {
		System.out.println(usage);
	}
}

package com.google.code.lightssh.common.report.jr;

import java.util.List;


import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 * report date source
 * @author yangxiaojin
 */
public class ListDataSource extends AbstractDataSource implements JRDataSource{
	@SuppressWarnings("rawtypes")
	private List dataSource;
	
	private int index = -1;

	@SuppressWarnings("rawtypes")
	public ListDataSource(List dataSource ) {
		super();
		this.dataSource = dataSource;
	}

	public Object getFieldValue(JRField jrField) throws JRException {
		return fieldValue( dataSource.get(index), jrField.getName() );
	}

	public boolean next() throws JRException {
		return (dataSource != null) && ++index < dataSource.size();
	}

}

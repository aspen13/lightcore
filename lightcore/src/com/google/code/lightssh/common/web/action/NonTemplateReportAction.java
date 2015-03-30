package com.google.code.lightssh.common.web.action;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.report.ReportConstants;
import com.google.code.lightssh.common.report.jr.DynamicJasperEngine;
import com.google.code.lightssh.common.report.jr.JasperEngine;
import com.google.code.lightssh.common.report.jr.ReportParameter;
import com.google.code.lightssh.common.util.StringUtil;

/**
 * 无Jrxml 模块文件的导出
 * @author YangXiaojin
 *
 * @param <T>
 */
public abstract class NonTemplateReportAction<T> extends ReportAction<T>{

	private static final long serialVersionUID = 7946094980588145337L;
	
	private static Logger log = LoggerFactory.getLogger(NonTemplateReportAction.class);
	
	/** 
	 * nontemplate jasperreport engine 
	 * 
	 */
	protected DynamicJasperEngine jasperEngine;
	
	public DynamicJasperEngine getJasperEngine() {
		return jasperEngine;
	}

	public void setJasperEngine(DynamicJasperEngine jasperEngine) {
		this.jasperEngine = jasperEngine;
	}

	/**
	 * 导出参数(行,标题...)
	 * @return
	 */
	public abstract ReportParameter buildReportParameter( );

	/**
	 * 编译后的 Jasper 文件名
	 */
	public abstract String getTemplateFileName();
		
	/**
	 * 导出
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String report( ){		
		String reqType = request.getParameter("type");		
		if( reqType == null || "".equals( reqType )){
			reqType = ReportConstants.FORMAT_PDF;
		}

		if( jasperEngine == null ){	
			jasperEngine = new DynamicJasperEngine( );
		}
		
		if( StringUtil.clean(jasperEngine.getDefaultDirectory()) == null ){
			jasperEngine.setDefaultDirectory(
					ServletActionContext.getServletContext().getRealPath( 
							JasperEngine.DEFAULT_JRXML_FILE_DIRECTORY ) );
		}
		jasperEngine.setFileName( getTemplateFileName() );
				
		List datasouces = getDataSource();
		
		//parameters
		String path = ServletActionContext.getServletContext().getRealPath( "/");
		Map params = getReportParams();
		if( path != null )
			params.put("app_base_dir", new File( path ) );
		
		try {		
			inputStream = jasperEngine.execute(
					datasouces, params, reqType ,buildReportParameter() );
			
			return SUCCESS;
		} catch( Exception e ){
			e.printStackTrace();
			log.error( e.getMessage() );
		}

		return INPUT;		
	}

}

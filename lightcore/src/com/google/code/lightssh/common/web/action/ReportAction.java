package com.google.code.lightssh.common.web.action;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.report.ReportConstants;
import com.google.code.lightssh.common.report.jr.JasperEngine;

/**
 * 导出\打印Action
 * 
 * 		<!-- JASPER REPORT EXAMPLE -->
 * 		<action name="queryCompanyPersonnel" class="myExampleAction" method="print">
 * 			<result name="input">/WEB-INF/jsp/humanresources/company/queryCompanyPersonnel.jsp</result>
 * 			<result name="success" type="stream">
 * 				<param name="inputName">inputStream</param>
 * 				<param name="contentType">${jasperEngine.contentType}</param>
 * 				<param name="contentDisposition">	filename="JR_EXPORT"</param>
 * 				<param name="bufferSize">1024</param>
 * 			</result>
 * 		</action>
 *  
 * @author: YangXiaojin
 */
public abstract class ReportAction<T> extends BaseAction{

	private static final long serialVersionUID = -4419451884450096906L;
	
	private static Logger log = LoggerFactory.getLogger(ReportAction.class);

	/** jasperreport engine */
	protected JasperEngine jasperEngine;
	
	/** content type */
	protected String contentType;

	/** inputstrem */
	protected InputStream inputStream;
	
	protected String type;
	
	//-- getters and setters ---------------------------------------------------
	
	public JasperEngine getJasperEngine() {
		return jasperEngine;
	}

	public void setJasperEngine(JasperEngine jasperEngine) {
		this.jasperEngine = jasperEngine;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
		
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * data source for print
	 * @return
	 */
	public abstract List<T> getDataSource( );
	
	/**
	 * template file name 
	 * @return
	 */
	public abstract String getTemplateFileName( );
	
	/**
	 * report parames 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Map getReportParams(){
		Map params = new HashMap();
		return params;
	}
	
	/**
	 * 模版文件导出
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String report( ){		
		if( type == null || "".equals( type )){
			type = ReportConstants.FORMAT_PDF;
		}
		
		jasperEngine.setDefaultDirectory(ServletActionContext.getServletContext()
				.getRealPath( JasperEngine.DEFAULT_JRXML_FILE_DIRECTORY ) );
		jasperEngine.setFileName( getTemplateFileName( ) );			
		
		//parameters
		Map params = getReportParams();
		params.put("app_base_dir", new File(
				ServletActionContext.getServletContext().getRealPath( "/")));
		
		try {
			inputStream = jasperEngine.execute(getDataSource(), params, type ,true);
			return SUCCESS;
		} catch( Exception e ){
			this.saveErrorMessage("报表异常:"+e.getMessage());
			log.error( "报表异常：",e );
		}

		return INPUT;		
	}

}

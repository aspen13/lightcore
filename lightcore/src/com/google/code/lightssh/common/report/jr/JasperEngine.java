package com.google.code.lightssh.common.report.jr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.report.ReportConstants;

/**
 * JasperReport main class
 * @author yangxiaojin
 * @version 1.0 Date:2007-12-3
 */
public class JasperEngine implements ReportConstants{
	
	/** log */
    private static Logger log = LoggerFactory.getLogger(JasperEngine.class);
    
	
	/** file separator */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	/** dot */
	public static final String DOT = ".";
			
	/** jrxml file suffix */
	public static final String JRXML_FILE_SUFFIX = "jrxml";
	
	/** jasper file suffix */
	public static final String JASPER_FILE_SUFFIX = "jasper";
	
	/** default directory of jrxml file */
	public static final String DEFAULT_JRXML_FILE_DIRECTORY = "/files/reports";	

	//-- var -------------------------------------------------------------------
	
	/** the directory that jrxml and jasper file in */
	protected String defaultDirectory ;
	
	/** the jrxml and jasper file name */
	protected String fileName;
	
	/** content type */
	protected String contentType;
	
	/** 是否使用JASPER 文件 */
	protected boolean useJasperFile;
			
	public String getDefaultDirectory() {
		return defaultDirectory;
	}

	public void setDefaultDirectory(String defaultDirectory) {
		this.defaultDirectory = defaultDirectory;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public boolean isUseJasperFile() {
		return useJasperFile;
	}

	public void setUseJasperFile(boolean useJasperFile) {
		this.useJasperFile = useJasperFile;
	}
		
	//-- main methods ----------------------------------------------------------	

	public JasperEngine(){
		
	}
	
	public JasperEngine( boolean useJasperFile ){
		this.useJasperFile = useJasperFile;
	}

	/**
	 * compile jrxml to jsper
	 */
	public boolean compile( String fileName ){	
		try{	

			String srcFilePath =  sourceFileName(fileName);			
			String destFilaName = destinationFileName(fileName);
			
			if( ! fileIsExists( srcFilePath, JRXML_FILE_SUFFIX ) ){
				log.error("Not found file:'" + srcFilePath +"'");
				return false;
			}
				
			JasperCompileManager.compileReportToFile( srcFilePath ,destFilaName );			
		}catch (Exception e){
			//do something …		
			log.error("编译 JRXML文件出错:{}", e );
			return false;
		}
		
		return true;
	}
	
	/**
	 * comile jrxml to jsper , use current file name
	 * @return
	 */
	public boolean compile( ){	
		return compile( getFileName() );
	}
	
	/**
	 * fill date 
	 * @param fileName
	 * @param parameters
	 * @param list
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public JasperPrint fill( File jasperFile ,Map parameters,List list ){
		JasperPrint jasperPrint = null;
		
		try{			
			JasperReport jasperReport =(JasperReport)JRLoader.loadObject( jasperFile );
			jasperPrint = JasperFillManager.fillReport(
					jasperReport,parameters,new ListDataSource( list ));
			
		}catch (JRException e){
			//do something …
		}
		
		return jasperPrint;
	}
	
	/**
	 * fill date by a appointed filename
	 * @param fileName
	 * @param parameters
	 * @param list
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public JasperPrint fill( String fileName ,Map parameters,List list){
		String jsperFileName = wrapFileName( fileName, JASPER_FILE_SUFFIX );	
		return fill( new File(jsperFileName) ,parameters,list);
	}
	
	/**
	 * fill date use current file
	 * @param parameters param for export
	 * @param list date for export
	 * @param clazz class 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public JasperPrint fill( Map parameters,List list){
		return fill( getRealFileName() , parameters, list );
	}
	
	/**
	 * exprot report to bytes 
	 * @param jasperPrint
	 * @param exporter
	 * @return
	 * @throws JRException
	 */
    protected byte[] exportReportToBytes(JasperPrint jasperPrint, String type ) throws JRException {    
    	JRExporter exporter = null;
        
		if( FORMAT_PDF.equalsIgnoreCase( type ) ){	
			contentType = "application/pdf";
			exporter = new JRPdfExporter( );
		}else if( FORMAT_XML.equalsIgnoreCase( type ) ){
			contentType = "text/xml";
			exporter = new JRXmlExporter();
		}else if( FORMAT_HTML.equalsIgnoreCase( type ) ){
			contentType = "text/html";
			exporter = new JRHtmlExporter();
			//... ...
		}else if( FORMAT_CSV.equalsIgnoreCase( type ) ){
			contentType = "text/csv";
			exporter = new JRCsvExporter();
		}else if( FORMAT_RTF.equalsIgnoreCase( type ) ){
			contentType = "application/msword";
			exporter = new JRRtfExporter();
		}else if( FORMAT_XLS.equalsIgnoreCase( type ) ){
			contentType = "application/vnd.ms-excel";
			//exporter = new JRXlsExporter();
			exporter = new JExcelApiExporter();
		}else{
			log.warn( "Export type not in ['PDF','XML','CSV','RTF','XLS','HTML'], use 'XLS'");
			contentType = "application/vnd.ms-excel";
			exporter = new JExcelApiExporter();
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);   
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.exportReport();

        return baos.toByteArray();
    }
		
	/**
	 * export 
	 * @param response
	 * @param jasperPrint
	 * @param type
	 * @throws Exception
	 */
	public void export( HttpServletResponse response ,
			JasperPrint jasperPrint , String type )throws Exception {
		
		response.setContentType( getContentType() );	
		byte[] output = exportReportToBytes( jasperPrint, type );	
		response.setContentLength( output.length );
		
		ServletOutputStream ouputStream = response.getOutputStream();
		ouputStream.write(output);
        ouputStream.flush();
        ouputStream.close();
	}
	
	/**
	 * create inputstream
	 */
	@SuppressWarnings("rawtypes")
	public InputStream execute( List list,Map parameters,String type ,boolean forceComplie )
	throws Exception {
		File dir = new File(getDefaultDirectory());
		if( !dir.exists() )
			dir.mkdirs();
		
		String srcFilePath = getDefaultDirectory() + FILE_SEPARATOR + getFileName() ;
		if( ! fileIsExists( srcFilePath, JASPER_FILE_SUFFIX ) || forceComplie ){
			compile();
		}
		
		JasperPrint jasperPrint = fill( parameters,list );		
		byte[] ert_bytes = exportReportToBytes( jasperPrint, type );

		return new ByteArrayInputStream( ert_bytes ); 
	}
	
	/**
	 * 执行打印操作
	 * @param list
	 * @param parameters
	 * @param type
	 * @param forceComplie
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public JasperPrint executePrint( List list,Map parameters,String type ,boolean forceComplie )
	throws Exception {
		String srcFilePath = getDefaultDirectory() + FILE_SEPARATOR + getFileName() ;
		if( (!fileIsExists( srcFilePath, JASPER_FILE_SUFFIX ) || forceComplie) && !useJasperFile){
			compile();
		}
		
		JasperPrint jasperPrint = fill( parameters,list );
		return jasperPrint;
		//	byte[] ert_bytes = exportReportToBytes( jasperPrint, type );

		//return new ByteArrayInputStream( ert_bytes ); 
	}
	
	/**
	 * create inputstream
	 */
	@SuppressWarnings("rawtypes")
	public InputStream execute( List list,Map parameters, String type )
	throws Exception {
		return execute( list, parameters, type, false );
	}
	
	//-- assistant methods -----------------------------------------------------
		
	/**
	 * source file name JRXML
	 */
	protected String sourceFileName( String fileName ){
		return defaultDirectory + FILE_SEPARATOR + fileName + DOT + JRXML_FILE_SUFFIX;	
	}
	
	protected String sourceFileName( ){
		return sourceFileName( getFileName() );
	}
	
	/**
	 * destination file name JASPER
	 */
	protected String destinationFileName( String fileName ){
		return defaultDirectory + FILE_SEPARATOR + fileName + DOT + JASPER_FILE_SUFFIX;	
	}
	
	protected String destinationFileName( ){
		return destinationFileName( getFileName() );
	}
	
	/**
	 * file is exists
	 */
	protected boolean fileIsExists( String pathFileName,String suffix ){
		File file = new File( wrapFileName( pathFileName, suffix ) );
		
		return file.exists();
	}
	
	/**
	 * wrap file and ignore error
	 * @param pathFileName
	 * @param suffix
	 * @return
	 */
	protected String wrapFileName( String pathFileName,String suffix  ){
		return pathFileName.replaceAll("\\."+suffix + "$", "") + "." + suffix;
	}
	
    /**
     * get real file name
     * @return
     */
    public String getRealFileName( ){
    	return getDefaultDirectory() + FILE_SEPARATOR + getFileName();
    }

}

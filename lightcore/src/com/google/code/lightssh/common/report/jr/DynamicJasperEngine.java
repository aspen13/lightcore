package com.google.code.lightssh.common.report.jr;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.util.StringUtil;


/**
 * dynamic column JasperReport 
 * @author YangXiaojin
 *
 */
public class DynamicJasperEngine extends JasperEngine{
	/** normal style */
	public static final String DEFAULT_STYLE_NAME = "normalStyle";
	
	/** title sytle */
	public static final String TITLE_STYLE_NAME = "titleStyle";
	
	/** column header style */
	public static final String CAPTION_STYLE_NAME = "columnHeaderStyle";
	
	public static final int WIDTH = 0, HEIGHT = 1;
	public static final int TOP=0, RIGHT=1, BOTTOM=2, LEFT=3;
	
	/** page width and hieght */
	public static final int[ ] PAGE_A4 = {595,842}; 
	
	//~8000 
	public static final int[ ] PAGE_8000 = {1500,842}; 
	
	/** page margin,top-right-bottom-left */
	public static final int[ ] PAGE_MARGIN = { 20,30,20,30}; //{0,0,0,0};//
	
	//-- var -------------------------------------------------------------------
	
	/**
	 * log
	 */
	private static Logger log = LoggerFactory.getLogger( DynamicJasperEngine.class );
		
	/**
	 * jasper design,JRXML file in memory
	 */
	protected JasperDesign design;
	
	/**
	 * report parameters support by user
	 */
	protected ReportParameter reportParam;
	
	/**
	 * 显示元素的总宽
	 */
	private int allTextFieldWidth;
	
	/**
	 * max text field height
	 */
	private int maxTextFieldHeight;
	
	//-- constructors ----------------------------------------------------------
	
	public DynamicJasperEngine() {
		super();
	}
	
	public DynamicJasperEngine( ReportParameter reportParam ) {		
		super();
		this.reportParam = reportParam;
	}
	
	//-- getters and setters ---------------------------------------------------

	public ReportParameter getReportParam() {
		return reportParam;
	}

	public void setReportParam(ReportParameter reportParam) {
		this.reportParam = reportParam;
	}
	
	public int getAllTextFieldWidth() {
		return allTextFieldWidth;
	}

	public void setAllTextFieldWidth(int allTextFieldWidth) {
		this.allTextFieldWidth = allTextFieldWidth;
	}
	
	public JasperDesign getDesign() {
		return design;
	}

	public void setDesign(JasperDesign design) {
		this.design = design;
	}

	//-- util methods ----------------------------------------------------------
	
	/**
	 * default jasper design
	 */
	public void newJasperDesign( ) throws JRException{
		JasperDesign jasperDesign = new JasperDesign( );
		
		jasperDesign.setName("DefaultReport");
		jasperDesign.setPageWidth( PAGE_A4[ WIDTH ] );
		jasperDesign.setPageHeight( PAGE_A4[ HEIGHT ] );
		jasperDesign.setColumnWidth(50);
		//jasperDesign.setColumnSpacing(5);			
		jasperDesign.setTopMargin( PAGE_MARGIN[ TOP ] );
		jasperDesign.setRightMargin( PAGE_MARGIN[ RIGHT ] );
		jasperDesign.setBottomMargin( PAGE_MARGIN[ BOTTOM ] );
		jasperDesign.setLeftMargin( PAGE_MARGIN[ LEFT ] );	

		//normal style 
		JRDesignStyle normalStyle = new JRDesignStyle();
		normalStyle.setName( DEFAULT_STYLE_NAME );
		normalStyle.setDefault(true);
		//normalStyle.setFontName("Arial");
		normalStyle.setFontName(getAvailableFontFamilyName("宋体"));
		normalStyle.setFontSize(10);
		normalStyle.setPdfFontName("STSong-Light");
		normalStyle.setPdfEncoding("UniGB-UCS2-H");
		normalStyle.setPdfEmbedded( true );	
		normalStyle.setHorizontalAlignment( HorizontalAlignEnum.JUSTIFIED);
				
		//title style
		JRDesignStyle titleStyle = new JRDesignStyle();
		titleStyle.setName( TITLE_STYLE_NAME );
		titleStyle.setDefault(false);
		titleStyle.setFontName(getAvailableFontFamilyName("Arial"));
		titleStyle.setFontSize(10);
		titleStyle.setPdfFontName("STSong-Light");
		titleStyle.setPdfEncoding("UniGB-UCS2-H");
		titleStyle.setPdfEmbedded( true );	
		titleStyle.setHorizontalAlignment( HorizontalAlignEnum.LEFT );	
		titleStyle.setForecolor( Color.LIGHT_GRAY );
		
		//caption style
		JRDesignStyle captionStyle = new JRDesignStyle();
		captionStyle.setName( CAPTION_STYLE_NAME );
		captionStyle.setDefault(false);
		captionStyle.setFontName(getAvailableFontFamilyName("Arial"));
		captionStyle.setFontSize(10);
		captionStyle.setPdfFontName("STSong-Light");
		captionStyle.setPdfEncoding("UniGB-UCS2-H");
		captionStyle.setPdfEmbedded( true );	
		captionStyle.setHorizontalAlignment( HorizontalAlignEnum.LEFT );	
		//captionStyle.setForecolor( Color.ORANGE );
		captionStyle.setBold( true );
		//captionStyle.setBackcolor( Color.LIGHT_GRAY );
		
		//column header style
				
		jasperDesign.addStyle(normalStyle);
		jasperDesign.addStyle(titleStyle);
		jasperDesign.addStyle(captionStyle);
		
		this.design = jasperDesign;
	}
	
	/**
	 * 获取可用的字体名
	 */
	protected String getAvailableFontFamilyName( String font ){
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getAvailableFontFamilyNames();
		if( StringUtil.clean(font) == null )
			return fonts[0];
		
		for( String item:fonts )
			if( item.equals(font) )
				return font;
		
		return fonts[0];
	}
	
	/**
	 * add report Title 
	 * @param title
	 * @throws NullJasperDesignException
	 * @throws JRException
	 */
	public void addTitle( String title ) throws NullJasperDesignException, JRException{
		JRDesignBand band = new JRDesignBand();
		band.setHeight(50);
		
		JRDesignStaticText staticText = new JRDesignStaticText();
		staticText.setX(10);
		staticText.setY(0);
		staticText.setWidth( design.getPageWidth() );
		staticText.setHeight(15);
		staticText.setStyleNameReference( TITLE_STYLE_NAME );

		staticText.setText( title );
		band.addElement(staticText);
		
		design.setTitle(band);
	}
	
	/**
	 * add all field 
	 */
	public void addFields( ) throws NullJasperDesignException, JRException{
		if( design == null ) 
			throw new NullJasperDesignException("Null JasperDesign when add fileds") ;
		
		for( DynamicColumn item: reportParam.getDynamicColumns() ){
			JRDesignField field = new JRDesignField();
			field.setName( item.getField() );
			field.setValueClass( item.getClazz() );
			//field.setDescription( item.getCaption() );
			design.addField( field );
		}
	}
	
	/**
	 * locate field (x,y)
	 * @param dynamicFields
	 * @return
	 */
	public void locateTextFields( ){		
		int xPoint = 0, lastCeilWidth=0;
		maxTextFieldHeight  = -1;
		for( DynamicColumn item: reportParam.getDynamicColumns() ){
			if( !item.isHidden() ){
				item.setX( xPoint );
				lastCeilWidth =  item.getWidth();
				xPoint += item.getWidth();
				if( maxTextFieldHeight < item.getHeight() ) 
					maxTextFieldHeight = item.getHeight();
			}
		}
		
		setAllTextFieldWidth( xPoint + lastCeilWidth );		
	}
	
	/**
	 * add textFields
	 *
	 */
	@SuppressWarnings("deprecation")
	public void addTextFields( )	throws NullJasperDesignException{
		if( getDesign() == null ) 
			throw new NullJasperDesignException("Null JasperDesign when add text fields") ;
		
		locateTextFields( );	
		//JRDesignBand jrdBand = (JRDesignBand)getDesign().getDetail();	
				
		JRDesignBand textFieldBand = new JRDesignBand( );		
		textFieldBand.setHeight( 20 ); //^~
		
		JRDesignBand columnHeaderBand = new JRDesignBand( );
		columnHeaderBand.setHeight( 20 ); //^~
		
		for( DynamicColumn item: reportParam.getDynamicColumns() ){
			if( !item.isHidden() ){
				//add text field
				JRDesignTextField textField = new JRDesignTextField();
				textField.setX( item.getX() );
				textField.setY( item.getY() );
				textField.setWidth( item.getWidth() );
				textField.setHeight( item.getHeight() );
				//textField.setPositionType(JRElement.POSITION_TYPE_FLOAT); //
				textField.setHorizontalAlignment( HorizontalAlignEnum.LEFT );
				//textField.setStyle( design.getDefaultStyle() );
				
				JRDesignExpression expression = new JRDesignExpression();
				expression.setValueClass( item.getClazz() );
				expression.setText( "$F{" + item.getField() +"}" );
				textField.setExpression(expression);	
				textField.setBlankWhenNull( true );
				
				textFieldBand.addElement( textField );	
				
				//add column header
				JRDesignStaticText columnHeaderText = new JRDesignStaticText();			
				columnHeaderText.setText( item.getCaption() );
				columnHeaderText.setX( item.getX() );
				columnHeaderText.setY( 0 );
				columnHeaderText.setWidth( item.getWidth() );
				columnHeaderText.setHeight( 20 );//~^
				columnHeaderText.setHorizontalAlignment( HorizontalAlignEnum.CENTER );
				//columnHeaderText.setStyleNameReference( CAPTION_STYLE_NAME );
				columnHeaderText.setBold( true );
				columnHeaderBand.addElement( columnHeaderText );
			}
		}
		
		design.setDetail( textFieldBand );
		design.setColumnHeader( columnHeaderBand );
	}
	
	@SuppressWarnings("deprecation")
	public void addSummary( String summary ){
		JRDesignBand band = new JRDesignBand();
		band.setHeight(50);
		
		JRDesignStaticText staticText = new JRDesignStaticText();
		staticText.setX(10);
		staticText.setY(0);
		staticText.setWidth( design.getPageWidth() );
		staticText.setHeight(15);
		staticText.setHorizontalAlignment( JRAlignment.HORIZONTAL_ALIGN_CENTER );

		staticText.setText( summary );
		band.addElement(staticText);
		
		design.setSummary( band );
	}
	
	public void finallyAdjust( ){
		if( design.getPageWidth() < allTextFieldWidth )
			design.setPageWidth( allTextFieldWidth );		
	}

	/**
	 * design jrxml file 
	 */
	public JasperDesign designJRXml( ){
		try {
			//design = JRXmlLoader.load( sourceFileName() );
			if( design == null )
				newJasperDesign();			
			
			//addTitle( reportParam.getTitle() ); //标题
			//addSummary( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) );
			
			//add field
			addFields( );			
			
			//add detail
			addTextFields( );
			
			finallyAdjust( ); 
			
		}catch (JRException e) {
			e.printStackTrace();
		}catch( NullJasperDesignException e ){
			log.error( e.getMessage() );
		}
		
		return this.getDesign();	
	}	
	
	/**
	 * complie to jasper file
	 * @param jasperDesign
	 * @return
	 */
	public boolean compile( JasperDesign jasperDesign ){	
		try{	
			String destFilaName = destinationFileName(fileName);
			
			JasperCompileManager.compileReportToFile( jasperDesign ,destFilaName );			
		}catch (JRException e){
			//do something …		
			e.printStackTrace();
			log.error("编译 JRXML 文件出错!\n"  );
			return false;
		}
		
		return true;
	}
	
	/**
	 * override execute method 
	 */
	@SuppressWarnings("rawtypes")
	public InputStream execute( List list,Map parameters,String type, ReportParameter reportParam )
	throws Exception {
		setReportParam( reportParam );
		compile( designJRXml( ) );
		
		JasperPrint jasperPrint = fill( parameters,list );		
		byte[] ert_bytes = exportReportToBytes( jasperPrint, type );

		return new ByteArrayInputStream( ert_bytes ); 
	}
	
}

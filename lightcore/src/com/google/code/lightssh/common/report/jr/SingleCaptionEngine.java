package com.google.code.lightssh.common.report.jr;

import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;

/**
 * 列标题只在首页显示一次
 * 
 * @author YangXiaojin
 *
 */
public class SingleCaptionEngine extends DynamicJasperEngine{
	
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
		
		//Title band
		JRDesignBand titleBand = new JRDesignBand( );
		titleBand.setHeight( 20 ); 
		
		//titleBand.setSplitAllowed(true);
		
		for( DynamicColumn item: reportParam.getDynamicColumns() ){
			if( !item.isHidden() ){
				//add text field
				JRDesignTextField textField = new JRDesignTextField();
				textField.setX( item.getX() );
				textField.setY( item.getY() );
				textField.setWidth( item.getWidth() );
				textField.setHeight( item.getHeight() );
				//textField.setPositionType(JRElement.POSITION_TYPE_FLOAT); //
				textField.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_LEFT );
				//textField.setStyle( design.getDefaultStyle() );
				
				JRDesignExpression expression = new JRDesignExpression();
				expression.setValueClass( item.getClazz() );
				expression.setText( "$F{" + item.getField() +"}" );
				textField.setExpression(expression);
				
				textFieldBand.addElement( textField );	
				
				//add column header
				JRDesignStaticText columnHeaderText = new JRDesignStaticText();			
				columnHeaderText.setText( item.getCaption() );
				columnHeaderText.setX( item.getX() );
				columnHeaderText.setY( 0 );
				columnHeaderText.setWidth( item.getWidth() );
				columnHeaderText.setHeight( 20 );//~^
				columnHeaderText.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_LEFT );
				columnHeaderText.setStyleNameReference( CAPTION_STYLE_NAME );
				titleBand.addElement( columnHeaderText );
			}
		}
		
		design.setDetail( textFieldBand );
		design.setTitle( titleBand );
	}

}

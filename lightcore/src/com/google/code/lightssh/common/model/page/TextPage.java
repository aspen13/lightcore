package com.google.code.lightssh.common.model.page;


/**
 * text page
 * @author YangXiaojin
 */
public class TextPage extends AbstractPage{
	
	private static final long serialVersionUID = -5999881253423132768L;

	/** 每页默认文本大小 */
	public static final int DEFAULT_TEXT_SIZE = 300;
	
	private String text;
	
	private boolean showAll;
					
	public TextPage(int allSize, int size, int number, String text) {
		super(allSize, size, number);
		this.text = text;
	}	
	
	public TextPage( String text ) {
		this( text==null?0:text.length(),300,1,text );
	}
		
	public TextPage() {
		this( DEFAULT_TEXT_SIZE );
	}

	public TextPage(int size) {
		super(size);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		this.setAllSize( this.text==null?0:this.text.length() );
	}

	/**
	 * 取一页文本
	 * @param pageNumber
	 * @return
	 */
	public String getPageText( int pageNumber ){
		if( text == null )
			return null;
		
		if( this.isShowAll() )
			this.setSize( Integer.MAX_VALUE );
		
		pageNumber = Math.max(1, Math.abs(pageNumber));
		int start = Math.max(0, (pageNumber-1)*getSize());		
		int end = Math.min(text.length(), start + size );
		
		return text.substring(start, end).replaceAll("\n|\r", "<br/>");
	}
	
	public String getPageText( ){
		return this.getPageText( getNumber() );
	}

	public boolean isShowAll() {
		return showAll;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	@Override
	public int getDefaultSize() {
		return DEFAULT_TEXT_SIZE;
	}
	
}

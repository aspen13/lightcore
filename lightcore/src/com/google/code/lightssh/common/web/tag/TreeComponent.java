package com.google.code.lightssh.common.web.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.components.ContextBean;

import com.google.code.lightssh.common.util.ReflectionUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class TreeComponent extends ContextBean{
	
	private static final Logger LOG = LoggerFactory.getLogger(TreeComponent.class);
	
	public TreeComponent(ValueStack stack) {
		super(stack);
	}

	private String value;
	
	private Object tree;
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean start(Writer writer) {
        boolean result = super.start(writer);
        tree = findValue(value);
        
        List list = new ArrayList();
        if( tree instanceof String[] ){
        	for( String t:(String[])tree)
        		list.add(t);
        }else if( tree instanceof List){
        	list = (List)tree;
        }else{
        	list.add( tree );
        }
        
        
        try{
        	for(int i=0;i<list.size();i++){
            	if( i == 0 )
            		writer.write( "<ul>" );
            	
            	//TODO
            	writer.write("<li>"+ ReflectionUtil.reflectGetValue(list.get(i),"name")+"</li>");
            	
            	if( i== list.size()-1 )
            		writer.write("<ul>");
            }
        }catch (IOException e){
        	LOG.info("Could not print out value '" + value + "'", e);
        }	
        
        return result;
    }


	public boolean end(Writer writer, String body) {
		ValueStack stack = getStack();
		if (tree != null) {
		    stack.pop(); //?
		}
		
		return true;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setTree(Object tree) {
		this.tree = tree;
	}

}

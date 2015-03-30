package com.google.code.lightssh.common.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.lightssh.common.entity.Persistence;
import com.google.code.lightssh.common.model.Nameable;
import com.google.code.lightssh.common.model.page.ListPage;
import com.google.code.lightssh.common.service.BaseManager;
import com.google.code.lightssh.common.service.BaseManagerImpl;

/**
 *  base action
 * @author YangXiaojin
 *
 */
public class CrudAction<T extends Persistence<?>> extends BaseAction{
	
	private static Logger log = LoggerFactory.getLogger(CrudAction.class);

	private static final long serialVersionUID = 1L;
		
	protected BaseManager<T> manager;
	   
    protected T model;
   
    protected ListPage<T> page = new ListPage<T>();
    
    protected boolean unique;
    
    /** result type */
    public static final String NEXT = "next";
   
    //-- constructors ----------------------------------------------------------
               
    public CrudAction() {
        super();
        this.manager = new BaseManagerImpl<T>();
    }   

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }
     
	public BaseManager<T> getManager() {
		return manager;
	}

	public void setManager(BaseManager<T> manager) {
		this.manager = manager;
	}

	public ListPage<T> getPage() {
		return page;
	}

	public void setPage(ListPage<T> page) {
		this.page = page;
	}
	
	//@JSON(name="unique") 子类不能生成JSON!!!
	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	/**
    * edit
    * @return
    */
    public String edit( ){
        if( model != null && model.getIdentity() != null ){
            model = manager.get( model.getIdentity() );
        }
        
        return SUCCESS;
    }
   
    /**
    * save
    * @return
    */
    public String save( ){    	
        if( model == null ){
            return INPUT;
        }
        
        boolean isInsert = model.isInsert();
        
        try{
            manager.save(model);
        }catch( Exception e ){ //other exception
        	//GenerationType.SEQUENCE 未插入成功,Oracle 也会生成ID
        	if( isInsert )
        		model.postInsertFailure();
        	
            addActionError( e.getMessage() );
            log.error( e.getMessage() );
            return INPUT;
        } 
        
        String hint =  (model instanceof Nameable)?("["+((Nameable)model).getName()+"]"):""+"保存成功！";
        saveSuccessMessage( hint );
        String saveAndNext = request.getParameter("saveAndNext");
        if( saveAndNext != null && !"".equals( saveAndNext.trim() ) ){
        	return NEXT;
        }else{        	
        	return SUCCESS;
        }
    }
   
    /**
    * list
    * @return
    */
    public String list( ){       
        if( page == null ){
            page = new ListPage<T>();
        }
       
        page = manager.list( page , model );
        request.setAttribute( "list", page );
       
        return SUCCESS;
    }

    /**
    * delete
    * @return
    */
    public String delete( ){
        if( model == null || model.getIdentity() == null ){
            return INPUT;
        }
       
        try{
        	manager.remove( model.getIdentity() );
        	saveSuccessMessage( "成功删除数据！" );
        }catch( Exception e ){ //other exception
            saveErrorMessage( "删除发生异常：" + e.getMessage() );
            log.error( e.getMessage() );
            return INPUT;
        } 
       
        return SUCCESS;
    }

    /**
     * 属性是否唯一
     */
    public String unique( ){
		this.unique = this.getManager().isUniqueProperty( model );
		return SUCCESS;
	}
}

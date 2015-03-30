package com.google.code.lightssh.common.entity.base;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.google.code.lightssh.common.entity.Persistence;

/**
 * 模型基类,依赖数据生成ID
 * @author YangXiaojin
 */
@MappedSuperclass
public abstract class BaseModel implements Persistence<Long>{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * identity
	 */
	@Id
	@Column( name="ID" )
	@GeneratedValue( strategy=GenerationType.SEQUENCE )
	protected Long id;

	public Long getIdentity() {
		return this.id;
	}
	
	public boolean isInsert( ){
		return this.getIdentity() == null;
	}
	
	public void postInsertFailure( ){
		this.id = null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void preInsert( ){
		//do nothing
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		BaseModel other = (BaseModel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		
		return true;
	}
	
}

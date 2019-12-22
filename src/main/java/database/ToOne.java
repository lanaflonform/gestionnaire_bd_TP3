package database;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import annotations.DBJoin;
import annotations.DBTable;

public class ToOne {
	
	private BeanReflectorRetrieve innerBeanReflector;
	private Field innerBeanField;
	private Class<?> innerBeanClassRep;
	private BeanReflectorRetrieve outerBeanReflector;
	private DBJoin fieldAnnotation;
	
	public ToOne (Field innerBeanField, BeanReflectorRetrieve outerBeanReflector) {
		
		// Creation de parametres du bean ToOne a partir des info du BeanReflector
		this.innerBeanField = innerBeanField;
		this.outerBeanReflector = outerBeanReflector;
		this.innerBeanClassRep = innerBeanField.getType();
		this.innerBeanReflector = new BeanReflectorRetrieve(innerBeanField.getType());
        fieldAnnotation = innerBeanField.getAnnotation(DBJoin.class);
	}

	
	public BeanReflectorRetrieve getInnerBeanReflector() {
		return innerBeanReflector;
	}

	public Field getInnerBeanField() {
		return innerBeanField;
	}

	public Class<?> getInnerBeanClassRep() {
		return innerBeanClassRep;
	}

	public BeanReflectorRetrieve getOuterBeanReflector() {
		return outerBeanReflector;
	}

	public DBJoin getFieldAnnotation() {
		return fieldAnnotation;
	}



	public String getInnerSQL(ResultSet results) {
		try {
			return "SELECT * FROM " + getInnerBeanClassRep().getAnnotation(DBTable.class).tableName() 
			+ " WHERE " + getFieldAnnotation().externJKey() + " = "  
			+ results.getObject(results.findColumn(getFieldAnnotation().internJKey()));
		} catch (SQLException e) {
			e.printStackTrace();
			return null; 
		}
	}
	
	
	
}

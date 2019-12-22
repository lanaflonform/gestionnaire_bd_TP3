package database;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;

import annotations.DBJoin;
import annotations.DBTable;

public class ToMany {
	
	private BeanReflectorRetrieve innerBeanReflector;
	private Field innerCollectionField;
	private Class<?> innerCollectionClassRep;
	private BeanReflectorRetrieve outerBeanReflector;
	private DBJoin fieldAnnotation;
	
	
	public ToMany (Field innerBeanField, BeanReflectorRetrieve outerBeanReflector) {
		
		// Creation de parametres du bean ToMany a partir des info du BeanReflector
		innerCollectionField = innerBeanField;
		this.outerBeanReflector = outerBeanReflector;
		
		ParameterizedType type = (ParameterizedType) innerCollectionField.getGenericType();
		innerCollectionClassRep = (Class<?>) type.getActualTypeArguments()[0];
				
		this.innerBeanReflector = new BeanReflectorRetrieve(innerCollectionClassRep);
		fieldAnnotation = innerBeanField.getAnnotation(DBJoin.class);
		
		
	}


	public BeanReflectorRetrieve getInnerBeanReflector() {
		return innerBeanReflector;
	}

	public Field getInnerCollectionField() {
		return innerCollectionField;
	}

	public Class<?> getInnerCollectionClassRep() {
		return innerCollectionClassRep;
	}

	public BeanReflectorRetrieve getOuterBeanReflector() {
		return outerBeanReflector;
	}

	public DBJoin getFieldAnnotation() {
		return fieldAnnotation;
	}


	public String getInnerBeanSQL(ResultSet results) {
		DBTable tableannotation = getInnerCollectionClassRep().getAnnotation(DBTable.class);
		DBJoin joinAnnotation = getFieldAnnotation();

		try {
			return "SELECT * FROM " + tableannotation.tableName() 
			+ " WHERE " + joinAnnotation.externJKey() + " = "  
			+ results.getObject(results.findColumn(joinAnnotation.internJKey()));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

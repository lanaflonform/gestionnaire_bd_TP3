package database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import annotations.DBAutoIncrement;
import annotations.DBColumn;
import annotations.DBPrimaryKey;
import annotations.DBTable;
import database.ToOne;
import database.ToMany;

public class BeanReflectorInsert {
	
	private Class<?> beanRepresentative;
	private DBTable annotationDBTable;
	private DBPrimaryKey annotationDBPrimaryKey;
	private DBAutoIncrement annotationDBAutoIncrement;
	private List<DBColumn> listDBColumnNames; 
	private List<BeanReflectorInsert> listBeanReflectorInsertToOne; 
	private List<BeanReflectorInsert> listBeanReflectorInsertToMany; 
	

	public BeanReflectorInsert(Class<?> beanRepresent) {
		
		listDBColumnNames = new ArrayList<DBColumn>();
		listBeanReflectorInsertToOne = new ArrayList<BeanReflectorInsert>();
		listBeanReflectorInsertToMany = new ArrayList<BeanReflectorInsert>();
		this.beanRepresentative = beanRepresent;
		this.annotationDBTable = beanRepresentative.getAnnotation(DBTable.class);
		
		BeanReflectorRetrieve brr = new BeanReflectorRetrieve(beanRepresentative);
		
		// FIELD du bean
		for(Field f : brr.getListFields()) {
			if (f.isAnnotationPresent(DBPrimaryKey.class)) {
				annotationDBPrimaryKey = f.getAnnotation(DBPrimaryKey.class);
			}
			
			if (f.isAnnotationPresent(DBAutoIncrement.class)) {
				annotationDBAutoIncrement = f.getAnnotation(DBAutoIncrement.class);
			}
			
			if (f.isAnnotationPresent(DBColumn.class)) {
				listDBColumnNames.add(f.getAnnotation(DBColumn.class));
			}
		}
		
		// Reflector des toOne
		for(ToOne to : brr.getListToOne()) {
			listBeanReflectorInsertToOne.add(new BeanReflectorInsert(to.getInnerBeanClassRep()));
		}
		
		// Reflector des toMany
		for(ToMany tm : brr.getListToMany()) {
			listBeanReflectorInsertToMany.add(new BeanReflectorInsert(tm.getInnerCollectionClassRep()));
		}
		
		
	}
	
	
	// GETTERS

	public Class<?> getBeanRepresentative() {
		return beanRepresentative;
	}

	public DBTable getAnnotationDBTable() {
		return annotationDBTable;
	}

	public DBPrimaryKey getAnnotationDBPrimaryKey() {
		return annotationDBPrimaryKey;
	}
	
	public DBAutoIncrement getAnnotationDBAutoIncrement() {
		return annotationDBAutoIncrement;
	}
	
	public List<DBColumn> getListDBColumnNames() {
		return listDBColumnNames;
	}

	public List<BeanReflectorInsert> getListBeanReflectorInsertToOne() {
		return listBeanReflectorInsertToOne;
	}

	public List<BeanReflectorInsert> getListBeanReflectorInsertToMany() {
		return listBeanReflectorInsertToMany;
	}
	
	
	// Retourne la liste de bean ToOne à Insérer



	
	

}

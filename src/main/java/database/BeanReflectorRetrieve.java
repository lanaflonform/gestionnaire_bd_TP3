package database;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import annotations.DBExclude;
import annotations.DBTable;

public class BeanReflectorRetrieve {
	private List<Field> listFields;
	private List<Field> listNotBeanFields;
	private List<ToOne> listToOne;
	private List<ToMany> listToMany;
	private Class<?> beanRepresentative; 
	
	public BeanReflectorRetrieve (Class<?> beanRepresentative)
	{
		try {
		this.beanRepresentative = beanRepresentative;
		
		//System.out.println(this.getBeanRepresentative());
		
		listFields = getFields(this.getBeanRepresentative());
		listToMany = new ArrayList<ToMany>();
		listToOne = new ArrayList<ToOne>();
		listNotBeanFields = new ArrayList<Field>();
		// iteration de tous les fields pour verifier si un des field est un bean ou une liste de bean
			for(Field f : listFields) {
				
				// verification du type de field
				Class<?> fieldType = f.getType();
				
				// Verifier si le field est une liste de bean
				if(fieldType == List.class && getListType(f).getAnnotation(DBTable.class) != null)
				{
					//System.out.println("\tToMany field : "+f.getName());
					ToMany toManyBean = new ToMany(f, this);
					listToMany.add(toManyBean);
				}
				
				// Verifier si le field est un bean
				else if(fieldType.getAnnotation(DBTable.class) != null && fieldType != List.class)
				{
					//System.out.println("\tToOne field : "+f.getName());
					ToOne toOneBean = new ToOne(f , this);
					listToOne.add(toOneBean);
				}
				else {
					//System.out.println("\tNotBean field : "+f.getName());
					listNotBeanFields.add(f);
				}
				
				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	// methode pour retourner l'ensemble des fields déclarés et exclus de la base de données
	private ArrayList<Field> getFields(Class<?> beanClass) {
		
		ArrayList<Field> fieldsList = new ArrayList<Field>();
		Field[] fieldsTable = beanClass.getDeclaredFields();
		
		for(Field f : fieldsTable) {
			
			// On vérifie que le field ne porte pas l'annotation DBExclude
			if(f.getAnnotation(DBExclude.class) == null) {
				fieldsList.add(f);
			}
		}
		return fieldsList;
	}
	
	public List<Field> getListFields() {
		return listFields;
	}
	
	public List<Field> getListNotBeanFields() {
		return listNotBeanFields;
	}


	public List<ToOne> getListToOne() {
		return listToOne;
	}

	public List<ToMany> getListToMany() {
		return listToMany;
	}

	// Methode pour retrouver le type d'une liste
	public Class<?> getListType(Field f){
		ParameterizedType type = (ParameterizedType) f.getGenericType();
		return (Class<?>) type.getActualTypeArguments()[0];
	}

	public Class<?> getBeanRepresentative() {
		return beanRepresentative;
	}

	
}
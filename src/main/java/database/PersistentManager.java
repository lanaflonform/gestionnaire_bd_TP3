package database;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import annotations.DBAutoIncrement;
import annotations.DBColumn;
import annotations.DBJoin;
import annotations.DBPrimaryKey;
import annotations.DBTable;
import annotations.Singleton;
import exceptions.PersistantManagerException;
import transaction.Transactional;

@Singleton
public class PersistentManager {
	

	// variable statique single_instance de type Singleton
	private static PersistentManager single_instance = null;
	
	// Variable pour la connection
	private static Connection connection;

	// CONSTRUCTEUR PRIVÉE pour Singleton
	private PersistentManager() {
		connection = DBConnection.getConnection(); 	// Connexion à la base de données
	}


	// méthode statique pour créer l'instance PersistantManager en Singleton
	public static PersistentManager getInstance() {
		if (single_instance == null)
			single_instance = new PersistentManager();
		return single_instance;
	}

	// Méthode pour retrouver un set de Beans dans la BD
	public <T> List<T> retrieveSet(Class<T> beanClass, String sqlRequest) {

		List<T> beansList = new ArrayList<T>();
		try {

			Statement statementSQL = connection.createStatement();

			ResultSet results = statementSQL.executeQuery(sqlRequest);

			// creation du bean reflector pour analyse le bean
			BeanReflectorRetrieve beanReflector = new BeanReflectorRetrieve(beanClass);

			// Tant qu'il y a des objets a traiter
			while (results.next()) {

				T currentBean = beanClass.getDeclaredConstructor().newInstance();

				// Initialisation des champs qui ne sont pas des beans
				for (Field f : beanReflector.getListNotBeanFields()) {
					Object fieldObject = results.getObject(results.findColumn(f.getName()));
					f.setAccessible(true);
					f.set(currentBean, fieldObject);

				}

				// Initialisation des champs qui sont des liste beans(ToMany)
				for (ToMany tm : beanReflector.getListToMany()) {

					String innerBeanSQL = tm.getInnerBeanSQL(results);
					// System.out.println(innerBeanSQL);

					try {

						tm.getInnerCollectionField().setAccessible(true);
						tm.getInnerCollectionField().set(currentBean,
								retrieveSet(tm.getInnerBeanReflector(), innerBeanSQL));

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// Initialisation des champs qui sont des beans(ToOne)
				for (ToOne to : beanReflector.getListToOne()) {

					String innerBeanSQL = to.getInnerSQL(results);

					// System.out.println(innerBeanSQL);

					try {

						List<?> listBeanToOne = retrieveSet(to.getInnerBeanReflector(), innerBeanSQL);
						to.getInnerBeanField().setAccessible(true);
						to.getInnerBeanField().set(currentBean, listBeanToOne.get(0)); // car, retourne qu'un Bean

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				beansList.add(currentBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Retourner la liste d'objet reconstruite
		return beansList;
	}

	// Méthode pour retrouver un seul Bean dans la BD
	// Lance une erreur si l'objet à récupérerer n'est pas un bean
	private <T> List<T> retrieveSet(BeanReflectorRetrieve b, String sqlRequest) throws PersistantManagerException {

		List<T> beansList = new ArrayList<T>();
		try {

			Statement statementSQL = connection.createStatement();

			ResultSet results = statementSQL.executeQuery(sqlRequest);

			// copie de la référence du bean reflector pour analyse le bean
			BeanReflectorRetrieve beanReflector = b;

			// Tant qu'il y a des objets a traiter
			while (results.next()) {

				@SuppressWarnings("unchecked")
				T currentBean = (T) b.getBeanRepresentative().getDeclaredConstructor().newInstance();

				// Vérification le le représentant du bean recherché est bel et bien un bean
				if (beanReflector.getBeanRepresentative().getAnnotation(DBTable.class) == null)
					throw new PersistantManagerException("L'objet recherche n'est pas un bean dans la BD");
				
				// Initialisation des champs qui ne sont pas des beans
				for (Field f : beanReflector.getListNotBeanFields()) {
					Object fieldObject = results.getObject(results.findColumn(f.getName()));
					f.setAccessible(true);
					f.set(currentBean, fieldObject);

				}

				// Initialisation des champs qui sont des liste beans(ToMany)
				for (ToMany tm : beanReflector.getListToMany()) {
					
					
					String innerBeanSQL = tm.getInnerBeanSQL(results);

					// System.out.println(innerBeanSQL);

					try {

						tm.getInnerCollectionField().setAccessible(true);
						tm.getInnerCollectionField().set(currentBean,
								retrieveSet(tm.getInnerBeanReflector(), innerBeanSQL));

					} catch (PersistantManagerException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// Initialisation des champs qui sont des beans(ToOne)
				for (ToOne to : beanReflector.getListToOne()) {

					String innerBeanSQL = to.getInnerSQL(results);

					try {

						List<?> listBeanToOne = retrieveSet(to.getInnerBeanReflector(), innerBeanSQL);
						to.getInnerBeanField().setAccessible(true);
						to.getInnerBeanField().set(currentBean, listBeanToOne.get(0)); // car, retourne qu'un Bean

					} catch (PersistantManagerException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				beansList.add(currentBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Retourner la liste d'objet reconstruite
		return beansList;
	}

	// Méthode pour inserer un bean dans la base de donnée
	@Transactional
	public <T> int insert(T bean) throws Exception {
		
		BeanReflectorInsert beanReflectorInsert = new BeanReflectorInsert(bean.getClass());
		Field[] Beanfields = bean.getClass().getDeclaredFields();
		int insertedRows = 0;
		
		//-----------------------------------------------------------------
		// On insère les bean toOne en premier, s'il y en a
		//-----------------------------------------------------------------
		if(beanReflectorInsert.getListBeanReflectorInsertToOne().size() > 0) {
			
				Beanfields = bean.getClass().getDeclaredFields();
		        
		        // Si le bean n'existe pas dans la BD, on l'insert
		        if(!IsBeanExistInDB(Beanfields, bean)) {

		        	for (int i = 0; i < Beanfields.length; i++) { 
			  		  
			            // get value of the fields 
			        	Beanfields[i].setAccessible(true);
			        
			        	// C'EST UN BEAN TO ONE
			        	if(Beanfields[i].isAnnotationPresent(DBJoin.class)) {
			                
			    			try {
			    				
			    				// INSERTION DU BEAN TOONE 
			    				insertedRows += insert(Beanfields[i].get(bean));
			    				
			    			} catch (IllegalArgumentException e) {
			    				// TODO Auto-generated catch block
			    				e.printStackTrace();
			    			} catch (IllegalAccessException e) {
			    				// TODO Auto-generated catch block
			    				e.printStackTrace();
			    			} 
			      
			        	}
			        } 
			}
			
		}
		
		
		//-------------------------------------------------------------------
		// On insère le bean ensuite
		//--------------------------------------------------------------------
  
        System.out.println("\n\nBEAN À INSÉRER DANS LA BD " + bean.getClass());
        
        
        // Si le bean n'existe pas dans la BD, on l'insert
        if(!IsBeanExistInDB(Beanfields, bean)) {
        
        	String BeanInsertSQL = getInsertBeanSQL(Beanfields, bean);
        	System.out.print("BEAN SQL --> " + BeanInsertSQL);
        	insertedRows++;
        	
        	
        	int idGenerated = 0;
        	 
        	// INSERTION DANS LA BASE DE DONNÉE
            try (PreparedStatement pstmt = connection.prepareStatement(BeanInsertSQL, Statement.RETURN_GENERATED_KEYS)) {
     
                int affectedRows = pstmt.executeUpdate();
                // check the affected rows 
                if (affectedRows > 0) {
                    // get the ID back
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                        	
                        	idGenerated = rs.getInt(1);
                            
                            // set le nouvel id dans l'objet inséré 
                        	System.out.println("\nID GENERATED : " + idGenerated);
                        	for(int i = 0; i < Beanfields.length; i++) {
                        		
                        		Beanfields[i].setAccessible(true);
                        		
                        		if(Beanfields[i].isAnnotationPresent(DBPrimaryKey.class)) {
                        			Beanfields[i].set(bean, idGenerated);
                        			break;
                        		}
                        	}
                        	
                        	
                        }
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            

        	
        	

        }

		
	
		//-------------------------------------------------------------------
		// On insère les BEAN toMANY Finalement
		//--------------------------------------------------------------------
		if(beanReflectorInsert.getListBeanReflectorInsertToMany().size() > 0) {
			
			// Pour chacun des beans toMany
			for(BeanReflectorInsert toManyBean : beanReflectorInsert.getListBeanReflectorInsertToMany()){
				
				
		        for (int i = 0; i < Beanfields.length; i++) { 
		  		  
		            // get value of the fields 
		        	Beanfields[i].setAccessible(true);
		        	
		        	Class<?> fieldType = Beanfields[i].getType();
					
					// Verifier si le field est une liste de bean
					if(fieldType == List.class && getListType(Beanfields[i]).getAnnotation(DBTable.class) != null){

			        	if(Beanfields[i].isAnnotationPresent(DBJoin.class) && getListType(Beanfields[i]) == toManyBean.getBeanRepresentative()) {
			        		
			    			try {
			    				
			    				List<?> CollectionofbeanToMany = (List<?>) Beanfields[i].get(bean);
			    				
			    		        // INSERTION DES BEANS TOMANY
			    		        for (int j = 0; j < CollectionofbeanToMany.size(); j++) { 
			    		        	insertedRows += insert(CollectionofbeanToMany.get(j));
			    		        	
			    		        }
	
			    			} catch (IllegalArgumentException e) {
			    				// TODO Auto-generated catch block
			    				e.printStackTrace();
			    			} catch (IllegalAccessException e) {
			    				// TODO Auto-generated catch block
			    				e.printStackTrace();
			    			} 
			      
			        	}
					}
		    		
		        }
		        
			}
			
		}
		
		
		
		return insertedRows;
	}
	
	// GET SQL POUR L'INSERTION
	private <T> String getInsertBeanSQL(Field[] beanfields, T bean) {
        ArrayList<String> listValuesSQLBean = new ArrayList<String>();
        ArrayList<String> listofFieldToInsert = new ArrayList<String>();
        String SQL = "INSERT INTO " + bean.getClass().getAnnotation(DBTable.class).tableName();
        
        for (int i = 0; i < beanfields.length; i++) { 
  
            // get value of the fields 
        	beanfields[i].setAccessible(true);
        	
        
        	if(beanfields[i].isAnnotationPresent(DBColumn.class) && !(beanfields[i].isAnnotationPresent(DBPrimaryKey.class) && beanfields[i].isAnnotationPresent(DBAutoIncrement.class))) {
                
    			try {
    				

    				listofFieldToInsert.add(beanfields[i].getAnnotation(DBColumn.class).columnName());
    		

    				if(beanfields[i].getType().equals(int.class))
    					listValuesSQLBean.add(beanfields[i].get(bean).toString());
    				else 
    					listValuesSQLBean.add("'" + beanfields[i].get(bean).toString() + "'");

    				
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			} catch (IllegalAccessException e) {
    				e.printStackTrace();
    			} 
      
        	}

        } 
        
        // columns names
        SQL += " (";
        for (int i = 0; i < listofFieldToInsert.size(); i++) { 
        	if (i == listofFieldToInsert.size() - 1) // dernier
        		SQL += listofFieldToInsert.get(i) + ")"; 
        	else
        		SQL += listofFieldToInsert.get(i) + ", "; 
        		
        }
        
        // values
        SQL += " VALUES (";
        for (int i = 0; i < listValuesSQLBean.size(); i++) { 
        	if (i == listValuesSQLBean.size() - 1) // dernier
        		SQL += listValuesSQLBean.get(i) + ")"; 
        	else
        		SQL += listValuesSQLBean.get(i) + ", "; 
        		
        }
        
        
        return SQL + ";";
	}

	

	// Méthode pour inserer une liste de beans dans la base de donnée
	@Transactional
	public <T> int bulkInsert(List<T> beansList) {
		int insertedRows = 0;
		
		for(T b : beansList) {
			try {
				insertedRows += insert(b);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return insertedRows;
	}
	
	
	//----------------------------------------------------------------------------------
	// AUTRES MÉTHODES
	//----------------------------------------------------------------------------------
	
	// Methode pour retrouver le type d'une liste
	private Class<?> getListType(Field f){
		ParameterizedType type = (ParameterizedType) f.getGenericType();
		return (Class<?>) type.getActualTypeArguments()[0];
	}
	
	
	// MÉTHODE POUR VÉRIFIER SI LE BEAN EXISTE DÉJÀ DANS LA BASE DE DONNÉE
	private <T> boolean IsBeanExistInDB(Field[] beanFields, T bean) {
		
		boolean ExistInDB = false;
		  
        for (int i = 0; i < beanFields.length; i++) { 
        	if(beanFields[i].isAnnotationPresent(DBPrimaryKey.class)) {
        
        		beanFields[i].setAccessible(true);
        		
        		try {
					if(beanFields[i].getInt(bean) != 0) { // si l'id = 0, le bean n'a jamais été inséré
						System.out.println("CE BEAN EXISTE DÉJÀ DANS LA BASE DE DONNÉE !");
						ExistInDB = true; // Le bean existe dans la DB, donc pas d'insertion
						break;
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
        	}
        }
        
        return ExistInDB;
	}	
	
}

package transaction;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;

import java.sql.Connection;
import java.sql.Savepoint;

public class InterceptorTransactional implements MethodInterceptor {
    
	private static int transactionsCount = 0;

	@Inject
	private Connection connection;
	
	// Constructor
	public InterceptorTransactional() {}

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        
    	Class<?> declaringClass = invocation.getMethod().getDeclaringClass(); // La classe dans laquelle la méthode est définie

        connection.setAutoCommit(false);
        
        transactionsCount++; // Une annotation @Transactional rencontrée
        System.out.println("\n Nombres de transactions : " + transactionsCount);
        
        String methodName = declaringClass.getName();
       
        Savepoint savePoint = connection.setSavepoint(methodName);
        
        Object result; 
        
        try {
			
			result = invocation.proceed();
			connection.commit();
			transactionsCount--;
			System.out.println("\n Nombres de transactions : " + transactionsCount);
			return result;
			
		} catch (Throwable e) {

			connection.rollback(savePoint);
			transactionsCount--;
			System.out.println("\n Nombres de transactions : " + transactionsCount);
			throw e;
			
		} finally {
			if (transactionsCount == 0) {
				connection.setAutoCommit(true);
			}
		}
    }
}

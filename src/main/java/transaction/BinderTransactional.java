package transaction;

import java.sql.Connection;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;

import database.DBConnection;

public class BinderTransactional extends AbstractModule {
	
    @Override
    protected void configure() {
    	InterceptorTransactional transactionalInterceptor = new InterceptorTransactional();
    	requestInjection(transactionalInterceptor);
    	bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), transactionalInterceptor);
    }
    
	@Provides
	Connection provideConnection()  { 
		return DBConnection.getConnection();
	}

}

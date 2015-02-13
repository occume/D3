package org.d3.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.d3.context.SpringConfig;
import org.d3.std.Stopwatch;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


public class JDBCTemplateTest {
	
	static ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
	private static JdbcTemplate template = new JdbcTemplate();
	
	private static Lock lock = new ReentrantLock();

	public static void main(String[] args) {
		for(int i = 0; i < 5; i++)
			doUpdate();
//		update();
	}
	
	public static void doUpdate(){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				Stopwatch sw = Stopwatch.newStopwatch();
				for(int i = 0; i < 10000; i++){
					update();
				}
				sw.printLong();
			}
		});
		t.start();
	}

	public static void update(){
//		
//		PlatformTransactionManager transaction = (PlatformTransactionManager) context.getBean("transactionManager");
//		TransactionTemplate tt =
//				new TransactionTemplate(transaction);
//		tt.execute(new TransactionCallback<Object>() {
//			@Override
//			public Object doInTransaction(TransactionStatus arg0) {
//				DataSource dataSource = (DataSource)context.getBean("dataSource");
//				template.setDataSource(dataSource);
//				String sql = "select email from d3_user where id = 1";
//				final String[] emails = {null};
//				template.query(sql, new Object[]{}, new RowCallbackHandler() {
//					@Override
//					public void processRow(ResultSet rs) throws SQLException {
//						emails[0] = rs.getString("email");
//					}
//				});
//				
//				int next  = Integer.valueOf(emails[0]) + 1;
//				
//				sql = "update d3_user set email = ? where id = 1";
//				template.update(sql, new Object[]{next});
//				return null;
//			}
//			});
		
		DataSource dataSource = (DataSource)context.getBean("dataSource");
		template.setDataSource(dataSource);
		String sql = "select email from d3_user where id = 1";
		final String[] emails = {null};
		
		lock.lock();
		template.query(sql, new Object[]{}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				emails[0] = rs.getString("email");
			}
		});
		
		int next  = Integer.valueOf(emails[0]) + 1;
		
		sql = "update d3_user set email = ? where id = 1";
		template.update(sql, new Object[]{next});
		lock.unlock();
	}
}

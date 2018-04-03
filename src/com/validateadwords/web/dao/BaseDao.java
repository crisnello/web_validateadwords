package com.validateadwords.web.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class BaseDao {
	
	protected Connection con;
	
	private Context ctx;
	
	protected PreparedStatement pstm;
	
	protected ResultSet rs;
	
	protected ResultSet rs2;
	
	protected Logger logger = Logger.getLogger(BaseDao.class);
	
	/**
	 * criar um data source do MySql no glassfish com as configuracoes abaixo
	 * 
	 * portNumber = 3306
	 * databaseName = rastreamento
	 * URL = jdbc:mysql://localhost:3306/rastreamento 
	 * serverName = localhost
	 * user = root
	 * password = root
	 * 
	 * @throws Exception
	 */
	
//	protected void conectar() throws Exception{
//		con = 
//		con.setAutoCommit(false);
//	}
	
	protected void conectar() throws Exception{
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("validateadwords");
		con = ds.getConnection();
		con.setAutoCommit(false);
	}
	
	protected void desconectar(){
		if(rs!=null){
			try {
				rs.close();
			} catch (Exception e) {
				//logger.debug(e);
			}
		}
		if(rs2!=null){
			try {
				rs2.close();
			} catch (Exception e) {
				//logger.debug(e);
			}
		}
		if(pstm!=null){
			try {
				pstm.close();
			} catch (Exception e) {
				//logger.debug(e);
			}
		}
		try {
        	con.commit();
        	con.close();
        } catch (SQLException e) {
        	//logger.debug(e);
        }
		try{
			ctx.close();
		}catch(Exception e){
			//logger.debug(e);
		}
	}

}

package com.chat.core.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Dao {

	private static Logger LOGGER = LoggerFactory.getLogger(Dao.class);

	private Connection connection = null;

	private DataSource dataSource = null;

	private PreparedStatement prepareStatement;

	private ResultSet rs;

	{
		try {
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/chat");
		} catch (NamingException e) {
			LOGGER.error("Fail getting datasource", e);
		}

	}

	private Connection getConnection() throws SQLException{
		try {
			return connection = (Connection) dataSource.getConnection();
		} catch (SQLException e) {
			LOGGER.error("fail getting connection", e);
			throw new SQLException("fail getting statemen", e);
		}
	}

	protected PreparedStatement getPrepareStatement(String SQL) throws SQLException{
		try {
			return prepareStatement = (PreparedStatement) getConnection().prepareStatement(SQL);
		} catch (SQLException e) {
			LOGGER.error("fail getting statement", e);
			throw new SQLException("fail getting statemen", e);
		}
	}

	protected ResultSet getResulsetOf(String SQL) throws SQLException{
		try {
			System.out.println("SQL : " + SQL);
			return rs = getPrepareStatement(SQL).executeQuery();
		} catch (SQLException e) {
			LOGGER.error("fail getting resultset", e);
		    throw new SQLException("fail get resultset", e);
		}
	}

	protected ResultSet getResulsetOf(String SQL, Map<Integer, Object> params) throws SQLException{
		try {
			System.out.println("SQL : " + SQL);
			System.out.println("Params : " + params);			
			PreparedStatement ps = getPrepareStatement(SQL);
			for(Integer index : params.keySet()) {
				ps.setObject(index, params.get(index));
			}
			return rs = ps.executeQuery();
		} catch (SQLException e) {
			LOGGER.error("fail getting resultset", e);
		    throw new SQLException("fail get resultset", e);
		}
	}
	
	protected int executeUpdate(String SQL, Map<Integer, Object> params) throws SQLException{
		try {
			System.out.println("executeUpdate : " + SQL);
			System.out.println("executeUpdate : " + params);
			PreparedStatement ps = getPrepareStatement(SQL);
			for(Integer index : params.keySet()) {
				ps.setObject(index, params.get(index));
			}
			int result = ps.executeUpdate();
			return result;
		} catch (SQLException e) {
			LOGGER.error("fail getting resultset", e);
		    throw new SQLException("fail get resultset", e);
		}
	}

	protected int[] executeBatch(String SQL, List<Map<Integer, Object>> batchParamsList) throws SQLException{
		try {
			System.out.println("executeUpdate : " + SQL);
			System.out.println("executeUpdate : " + batchParamsList);
			PreparedStatement ps = getPrepareStatement(SQL);
			for(Map<Integer, Object> params : batchParamsList) {
				for(Integer index : params.keySet()) {
					ps.setObject(index, params.get(index));
				}
				ps.addBatch();
				ps.clearParameters();
			}
			int result[] = ps.executeBatch();
			return result;
		} catch (SQLException e) {
			LOGGER.error("fail getting resultset", e);
		    throw new SQLException("fail get resultset", e);
		}
	}

	
	private void closeConection() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			LOGGER.error("fail close connection", e);
		}
	}

	private void closeStatement() {
		try {
			if (prepareStatement != null) {
				prepareStatement.close();
			}
		} catch (SQLException e) {
			LOGGER.error("fail close prepareStatement", e);
		}
	}
	
	private void closeResultset() {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				LOGGER.error("fail close resultset", e);
			}
		}
	}
	
	protected void closeConections() {
		closeResultset();
		closeStatement();
		closeConection();
	}
	
	protected String ReplaceQueryParams(String sql, Map<String, Object> replacementQueryParams) throws SQLException {
		try {
			if (replacementQueryParams != null && !replacementQueryParams.isEmpty()) {

				for (Map.Entry<String, Object> entry : replacementQueryParams.entrySet()) {
					sql = sql.replaceAll(entry.getKey(), String.valueOf(entry.getValue()));
				}
			}
		} catch (Exception e) {
			LOGGER.error("fail to replace params into sql {} ", sql, e);
			throw new SQLException("fail to replace params into sql:  " + sql,e);
		}

		return sql;
	}

}

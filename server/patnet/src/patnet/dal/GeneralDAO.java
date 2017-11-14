package patnet.dal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.function.Function;
import javax.naming.NamingException;

import java.sql.PreparedStatement;

public class GeneralDAO {
	protected static PreparedStatement prepareStatement(Connection conn, String query, Object... args) {
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			int i = 1;
			for(Object arg: args) {
				if (String.class.isInstance(arg)) {
					statement.setString(i, (String) arg);
				} else if (Integer.class.isInstance(arg)) {
					statement.setInt(i, (int) arg);
				} else if (Date.class.isInstance(arg)) {
					statement.setDate(i, new java.sql.Date(((Date) arg).getTime()));
				} else if (Double.class.isInstance(arg)) {
					statement.setDouble(i, (double) arg);
				}  else if (Boolean.class.isInstance(arg)) {
					statement.setBoolean(i, (boolean) arg);
				}  else if (Long.class.isInstance(arg)) {
					statement.setLong(i, (long) arg);
				}
				i += 1;
			}	
		} catch (SQLException e) {
			// TODO: handle exception
		}
		return statement;
	}
	
	protected <T> T execReadQuery(Function<Connection, PreparedStatement> statementBuilder, Function<ResultSet, T> retrivalFunction){
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		T results = null;
		try {
			con = ConnectionManager.getConnection();
			statement = statementBuilder.apply(con);
			if (statement == null) {
				throw new SQLException("Failed to prepare statment");
			}
			rs = statement.executeQuery();
			results = retrivalFunction.apply(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rs != null) rs.close();
				if(statement != null) statement.close();
				if(con != null) con.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
		}
		return results;
	}
	
	
	// todo: update and delete different from insert as they dont generate ID.
	protected Long execWriteQuery(Function<Connection, PreparedStatement> statementBuilder){
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		Long autoGeneratedId = null;
		try {
			con = ConnectionManager.getConnection();
			statement = statementBuilder.apply(con);
			if (statement == null) {
				throw new SQLException("Failed to prepare statment");
			} 

			statement.executeUpdate();
			ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
            		autoGeneratedId = generatedKeys.getLong(1);
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rs != null) rs.close();
				if(statement != null) statement.close();
				if(con != null) con.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
		}
		return autoGeneratedId;
	}
}

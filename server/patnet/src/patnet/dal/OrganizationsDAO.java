package patnet.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import patnet.model.*;
import patnet.dal.ConnectionManager;


public class OrganizationsDAO extends GeneralDAO{
	
	protected ConnectionManager connectionManager;
	private static OrganizationsDAO instance = null;
	
	public OrganizationsDAO() {
		connectionManager = new ConnectionManager();
	}
	
	public static OrganizationsDAO getInstance() {
		if (instance == null)
		{
			instance = new OrganizationsDAO();
		}
		return instance;
	}
	
	private Function<ResultSet, List<Organizations>> retrivalFunction = 
			rs -> {
		List<Organizations> organizations = new ArrayList<Organizations>();
		try {
			while (rs.next()) {
				organizations.add(new Organizations(
						rs.getLong("OrganizationId"),
						rs.getString("Name"),
						rs.getString("Address"),
						rs.getString("City"),
						rs.getString("State"),
						rs.getString("ZipCode"),
						rs.getString("Phone"),
						rs.getString("Location")));
			} 
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return organizations;
	};
			
	public Organizations create(Organizations organization) {
		Function<Connection, PreparedStatement> statementBuilder =
				conn -> GeneralDAO.prepareStatement(conn, 
						"INSERT INTO Organizations (Name,Address,City,State,ZipCode,Phone,Location) " +
						"  VALUES(?,?,?,?,?,?,?);", 
						organization.getName(),
						organization.getAddress(),
						organization.getCity(),
						organization.getState(),
						organization.getZipCode(),
						organization.getPhone(),
						organization.getLocation());
		Long id = this.execWriteQuery(statementBuilder);
		organization.setOrganizationId(id);
		return organization;
	}
				
	public List<Organizations> getOrganizationByField(String fieldName, Object fieldValue) {
		String queryString = String.format("SELECT * FROM Organizations WHERE %s = ?;", fieldName);
		Function<Connection, PreparedStatement> statementBuilder =
				conn -> GeneralDAO.prepareStatement(conn, 
						queryString, 
						fieldValue);
		return this.<List<Organizations>>execReadQuery(statementBuilder, retrivalFunction);
	}
	
	public Organizations getOrganizationById(Long id) {
		return this.getOrganizationByField("OrganizationId", id).get(0);
	}
	
	public Organizations getOrganizationByName(String name) {
		return this.getOrganizationByField("Name", name).get(0);
	}
	
	public List<Organizations> getOrganizationByCity(String city) {
		return this.getOrganizationByField("City", city);
	}
	
	public List<Organizations> getOrganizationByState(String state) {
		return this.getOrganizationByField("State", state);
	}
	public List<Organizations> getOrganizationByZipCode(int zip) {
		return this.getOrganizationByField("ZipCode", zip);
	}
	
	public Organizations deleteOrganization(Organizations organization) {
		Function<Connection, PreparedStatement> statementBuilder =
				conn -> GeneralDAO.prepareStatement(conn, 
						"delete from Organizations where OrganizationId = ?", 
						organization.getOrganizationId());
		
		if (this.execWriteQuery(statementBuilder) != null) {
			return null;
		}
		return organization;
	}
	
	public Organizations updateOrganizations(Organizations organization) {
		Function<Connection, PreparedStatement> statementBuilder = 
				conn -> GeneralDAO.prepareStatement(conn, 
								"update Organizations set "
								+ "Name = ?,"
								+ "Address = ?,"
								+ "City = ?,"
								+ "State = ?,"
								+ "ZipCode = ?,"
								+ "Phone = ?,"
								+ "Location = ? "
								+ " where OrganizationId = ?"
								+ "", 
								organization.getName(),
								organization.getAddress(),
								organization.getCity(),
								organization.getState(),
								organization.getZipCode(),
								organization.getPhone(),
								organization.getLocation(),
								organization.getOrganizationId());
		this.execWriteQuery(statementBuilder);
		return organization;
	}
	
	public Organizations updateOrganizationByField(Organizations organization, String fieldName, Object newValue) {
		String updateQuery = String.format("update Organizations set %s = ? where OrganizationId = ?", fieldName);
		Function<Connection, PreparedStatement> statementBuilder =
				conn -> GeneralDAO.prepareStatement(conn, 
						updateQuery, 
						newValue,
						organization.getOrganizationId());
		
		if (this.execWriteQuery(statementBuilder) != null) {
			organization.update(fieldName, newValue);
		}
		return organization;
	}
}

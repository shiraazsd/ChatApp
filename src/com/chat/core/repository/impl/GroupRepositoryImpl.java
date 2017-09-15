package com.chat.core.repository.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.core.dao.Dao;
import com.chat.core.dao.ex.SQLException;
import com.chat.core.domain.Group;
import com.chat.core.repository.GroupRepository;

public class GroupRepositoryImpl extends Dao implements GroupRepository {

	private static Logger LOGGER = LoggerFactory.getLogger(GroupRepositoryImpl.class);

	private String sql;

	private static GroupRepositoryImpl groupRepositoryImpl;

	private GroupRepositoryImpl() {

	}

	/**
	 * pattern singleton
	 * 
	 * @return
	 */
	public static GroupRepositoryImpl getInstance() {
		if (groupRepositoryImpl == null) {
			groupRepositoryImpl = new GroupRepositoryImpl();
		}
		return groupRepositoryImpl;
	}

	@Override
	public Group findById(Long id) throws SQLException {
		try {
			sql = "select * from team where id_team = :id_team";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put(":id_team", id);
			return getGroupByRS(getResulsetOf(ReplaceQueryParams(sql, parameters)));
		} catch (Exception e) {
			LOGGER.error("fail findById group :", id, e);
			throw new SQLException("fail findById group", e);
		} finally {
			closeConections();
		}
	}

	@Override
	public void create(Group domain) throws SQLException {
	}

	@Override
	public void update(Group domain) throws SQLException {

	}

	@Override
	public void delete(Group domain) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Group> findAll() throws SQLException {
		try {
			sql = "select * from team";
			return getGroupsByRS(getResulsetOf(sql));
		} catch (Exception e) {
			LOGGER.error("fail findAll users", e);
			throw new SQLException("fail findAll users", e);
		} finally {
			closeConections();
		}
	}

	/**
	 * this method is for get the list of users  
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<Group> getGroupsByRS(ResultSet rs) throws SQLException {
		try {
			List<Group> groups = new ArrayList<>();
			while (rs.next()) {
				groups.add(getFullGrouprByRs(rs));
			}
			return groups;
		} catch (Exception e) {
			LOGGER.error("fail to the getGroupsByRS", e);
			throw new SQLException("fail to the getGroupsByRS", e);
		}
	}

	/**
	 * this method is for  setter the {@link ResultSet} } getFullGrouprByRs 
	 * and can  be fulled, this is for reuse code
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Group getGroupByRS(ResultSet rs) throws SQLException {
		try {
			Group group = null;
			while (rs.next()) {
				group = getFullGrouprByRs(rs);
			}
			return group;
		} catch (Exception e) {
			LOGGER.error("fail to the getGroupByRS", e);
			throw new SQLException("fail to the getGroupByRS", e);
		}
	}

	
	/**
	 * full the object Group of the resulset 
	 * 
	 * @param rs
	 * @return the object {@link Group}
	 * @throws SQLException
	 */
	private Group getFullGrouprByRs(ResultSet rs) throws SQLException {
		try {
			Group team = new Group();
			team.setId(rs.getLong("id_team"));
			team.setGroupName(rs.getString("team_name"));
			return team;
		} catch (Exception e) {
			LOGGER.error("fail to the getFullGrouprByRs", e);
			throw new SQLException("fail to the getFullGrouprByRs", e);
		}
	}

}

package com.jlfex.hermes.service.web;

import java.sql.SQLException;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import com.jlfex.hermes.common.Logger;
import com.jlfex.hermes.repository.CommonRepository;
import com.jlfex.hermes.service.common.Query;

@Component
public class HermesDataSourceInitializer {

	@Autowired
	private CommonRepository commonRepository;
	@Value("classpath:sql/data.sql")
	private Resource dataScript;
	@Autowired
	private DataSource dataSource;

	@PostConstruct
	public void initData() throws SQLException {
		Logger.info("start check for update");
		Query query = new Query("from User");
		Long userCount = commonRepository.count(query.getCount(), new HashMap<String, Object>());
		if (userCount == 0) {
			Logger.info("first launch.need init db");
			ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
			populator.setSqlScriptEncoding("utf-8");
			populator.addScript(dataScript);
			populator.populate(dataSource.getConnection());
			Logger.info("init db done.");
			return;
		}
		Logger.info("no need for update");
	}
}
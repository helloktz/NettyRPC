package com.newlandframework.rpc.services.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newlandframework.rpc.services.JdbcPersonManage;
import com.newlandframework.rpc.services.pojo.Person;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JdbcPersonManageImpl implements JdbcPersonManage {
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private String toString(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	@Transactional
	@Override
	public int save(Person p) {
		// your business logic code here!
		log.info("jdbc Person data[" + p + "] has save!");
		log.info(p.toString());
		String sql = "insert into person(id,name,age,birthday) values(?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'))";
		log.info(sql);
		JdbcTemplate template = new JdbcTemplate(this.dataSource);
		template.update(sql, p.getId(), p.getName(), p.getAge(), toString(p.getBirthday()));

		return 0;
	}

	@Override
	public void query(Person p) {
		// your business logic code here!
		log.info("jdbc Person data[" + p + "] has query!");
		String sql = String.format("select * from person where id = %d", p.getId());
		JdbcTemplate template = new JdbcTemplate(this.dataSource);
		List<Map<String, Object>> rows = template.queryForList(sql);
		if (rows.isEmpty()) {
			log.info("record doesn't exist!");
		} else {
			rows.stream().forEach(row -> {
				log.info(row.get("ID").toString());
				log.info((String) row.get("NAME"));
				log.info(row.get("AGE").toString());
				log.info(toString((Date) row.get("BIRTHDAY")));
			});
		}
	}

	@Override
	public List<Person> query() {
		// your business logic code here!
		log.info("jdbc Person query!");

		String sql = "select * from person";
		JdbcTemplate template = new JdbcTemplate(this.dataSource);
		List<Map<String, Object>> rows = template.queryForList(sql);
		List<Person> list = rows.stream().map(row -> {
			Person p = new Person();
			p.setId(Integer.parseInt(row.get("ID").toString()));
			p.setName((String) row.get("NAME"));
			p.setAge(Integer.parseInt(row.get("AGE").toString()));
			p.setBirthday((Date) row.get("BIRTHDAY"));
			return p;
		}).collect(Collectors.toList());
		return list;
	}
}

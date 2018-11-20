package es.grupoica.cyted.bbdd.service;

import es.grupoica.cyted.bbdd.MyBatisUtil;

import org.apache.ibatis.session.SqlSessionFactory;
public abstract class AbstractService {

	protected SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	} public AbstractService() {
		this.sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
	}

	private SqlSessionFactory sqlSessionFactory;

}
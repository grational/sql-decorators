package it.italiaonline.rnd.db

import spock.lang.*
import java.lang.reflect.Field

class HikariConfUSpec extends Specification {

	def 'Should return a NullPointerException if one of the required env vars is not defined'() {
		when:
			def hc = new HikariConf (env_prefix: 'PREFIX')
		then:
			def error = thrown(NullPointerException)
			error.message == "'PREFIX_DB_JDBC_URL' env variable is required"
	}

	def 'Should correctly handle the set env variables'() {
		given:
			Map env = [
				PREFIX_DB_JDBC_URL: 'jdbc_url',
				PREFIX_DB_USERNAME: 'username',
				PREFIX_DB_PASSWORD: 'password',
				PREFIX_DB_DRIVER:   'com.mysql.cj.jdbc.Driver'
			]
		and:
			setEnv(env)
		when:
			def db = new HikariConf(env_prefix: 'PREFIX').conf
		then:
			noExceptionThrown()
		and:
			db.jdbcUrl         == env.PREFIX_DB_JDBC_URL
			db.username        == env.PREFIX_DB_USERNAME
			db.password        == env.PREFIX_DB_PASSWORD
			db.driverClassName == env.PREFIX_DB_DRIVER
	}

	def 'Should correctly set all the Hikari configuration'() {
		given:
			def env = [
				PREFIX_DB_JDBC_URL: 'jdbc_url',
				PREFIX_DB_USERNAME: 'username',
				PREFIX_DB_PASSWORD: 'password',
				PREFIX_DB_DRIVER:   'com.mysql.cj.jdbc.Driver'
			]
		and:
			def cores = Runtime.runtime.availableProcessors()
		and:
			def prefix = 'PREFIX'
		and:
			def dataSourceProperties = [
				useSSL: 'false',
				tinyInt1isBit: 'false'
			]
		when:
			setEnv(env)
		and:
			def hikari = new HikariConf (
				env_prefix:            prefix,
				datasource_properties: dataSourceProperties
			)
		then:
			hikari.conf.with {
				jdbcUrl                              == env.PREFIX_DB_JDBC_URL
				username                             == env.PREFIX_DB_USERNAME
				password                             == env.PREFIX_DB_PASSWORD
				driverClassName                      == env.PREFIX_DB_DRIVER
				maximumPoolSize                      == (cores * 2)
				minimumIdle                          == cores
				dataSourceProperties.'useSSL'        == 'false'
				dataSourceProperties.'tinyInt1isBit' == 'false'
			}
	}

	void setEnv(Map envVars) {
		Map immutableEnv = System.getenv()
		Class<?> cl      = immutableEnv.getClass()
		Field field      = cl.getDeclaredField("m")
		field.accessible = true
		Map mutableEnv   = field.get(immutableEnv)
		mutableEnv << envVars
	}
}

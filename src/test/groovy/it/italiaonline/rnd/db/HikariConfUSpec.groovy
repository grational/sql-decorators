package it.italiaonline.rnd.db

import spock.lang.*
import it.grational.specification.Environment

class HikariConfUSpec extends Specification {

	@Shared String prefix = 'PREFIX'

	@Shared
	Environment env = new Environment (
		"${prefix}_DB_JDBC_URL": 'jdbc_url',
		"${prefix}_DB_USERNAME": 'username',
		"${prefix}_DB_PASSWORD": 'password',
		"${prefix}_DB_DRIVER":   'com.mysql.cj.jdbc.Driver'
	)

	def 'Should return a NullPointerException if one of the required env vars is not defined'() {
		when:
			def hc = new HikariConf(env_prefix: prefix)
		then:
			def error = thrown(IllegalStateException)
			error.message == "[HikariConf] The environment variable ${prefix}_DB_JDBC_URL is required"
	}

	def 'Should correctly handle the set env variables'() {
		given:
			env.insert()

		when:
			def db = new HikariConf(env_prefix: prefix).conf

		then:
			noExceptionThrown()
		and:
			db.jdbcUrl         == System.getenv()."${prefix}_DB_JDBC_URL"
			db.username        == System.getenv()."${prefix}_DB_USERNAME"
			db.password        == System.getenv()."${prefix}_DB_PASSWORD"
			db.driverClassName == System.getenv()."${prefix}_DB_DRIVER"

		cleanup:
			env.clean()
	}

	def 'Should correctly set all the Hikari configuration'() {
		given:
			def cores = Runtime.runtime.availableProcessors()
		and:
			def dataSourceProperties = [
				useSSL: 'false',
				tinyInt1isBit: 'false'
			]
		and:
			env.insert()
		
		when:
			def hikari = new HikariConf (
				env_prefix:            prefix,
				datasource_properties: dataSourceProperties
			)

		then:
			hikari.conf.with {
				jdbcUrl                              == System.getenv()."${prefix}_DB_JDBC_URL"
				username                             == System.getenv()."${prefix}_DB_USERNAME"
				password                             == System.getenv()."${prefix}_DB_PASSWORD"
				driverClassName                      == System.getenv()."${prefix}_DB_DRIVER"
				maximumPoolSize                      == (cores * 2)
				minimumIdle                          == cores
				dataSourceProperties.'useSSL'        == 'false'
				dataSourceProperties.'tinyInt1isBit' == 'false'
			}

		cleanup:
			env.clean()
	}

}

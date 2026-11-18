package it.italiaonline.rnd.db

import spock.lang.*

class HikariConfUSpec extends Specification {

	@Shared String prefix = 'PREFIX'

	def 'Should report the missing variable if one of the required env vars is not defined'() {
		when:
			new HikariConf(env_prefix: 'MISSING')
		then:
			def error = thrown(IllegalStateException)
			error.message == "[HikariConf] The environment variable MISSING_DB_JDBC_URL is required"
	}

	def 'Should correctly handle the set env variables'() {
		when:
			def db = new HikariConf(env_prefix: prefix).conf

		then:
			noExceptionThrown()
		and:
			db.jdbcUrl         == System.getenv()."${prefix}_DB_JDBC_URL"
			db.username        == System.getenv()."${prefix}_DB_USERNAME"
			db.password        == System.getenv()."${prefix}_DB_PASSWORD"
			db.driverClassName == System.getenv()."${prefix}_DB_DRIVER"
	}

	def 'Should correctly set all the Hikari configuration'() {
		given:
			def cores = Runtime.runtime.availableProcessors()
		and:
			def dataSourceProperties = [
				useSSL: 'false',
				tinyInt1isBit: 'false'
			]

		when:
			def hikari = new HikariConf (
				env_prefix:            prefix,
				datasource_properties: dataSourceProperties
			)

		then:
			with(hikari.conf) {
				jdbcUrl                              == System.getenv()."${prefix}_DB_JDBC_URL"
				username                             == System.getenv()."${prefix}_DB_USERNAME"
				password                             == System.getenv()."${prefix}_DB_PASSWORD"
				driverClassName                      == System.getenv()."${prefix}_DB_DRIVER"
				maximumPoolSize                      == cores
				minimumIdle                          == 0
				dataSourceProperties.'useSSL'        == 'false'
				dataSourceProperties.'tinyInt1isBit' == 'false'
			}
	}

}

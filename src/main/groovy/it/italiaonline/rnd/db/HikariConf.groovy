package it.italiaonline.rnd.db
import com.zaxxer.hikari.HikariConfig

class HikariConf {

	final HikariConfig conf
	private final String  urlEnv
	private final String  usernameEnv
	private final String  passwordEnv
	private final Integer cores = Runtime.runtime.availableProcessors()

	HikariConf(Map params) {
		this.urlEnv      = "${params.env_prefix}_DB_JDBC_URL"
		this.usernameEnv = "${params.env_prefix}_DB_USERNAME"
		this.passwordEnv = "${params.env_prefix}_DB_PASSWORD"

		this.conf = new HikariConfig()
		this.conf.with {
			// authentication
			jdbcUrl  = Objects.requireNonNull (
				System.getenv()[urlEnv],
				"'${urlEnv}' env variable is required"
			)
			username = Objects.requireNonNull (
				System.getenv()[usernameEnv],
				"'${usernameEnv}' env variable is required"
			)
			password = Objects.requireNonNull (
				System.getenv()[passwordEnv],
				"'${passwordEnv}' env variable is required"
			)
			// performance tuning
			maximumPoolSize = cores * 2
			minimumIdle     = cores
			params?.datasource_properties?.each { property, value ->
				addDataSourceProperty(property, value)
			}
		}
	}
}

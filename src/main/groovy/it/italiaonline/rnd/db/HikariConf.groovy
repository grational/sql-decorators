package it.italiaonline.rnd.db
import com.zaxxer.hikari.HikariConfig

class HikariConf {

	final HikariConfig conf
	private final String  driverEnv
	private final String  urlEnv
	private final String  usernameEnv
	private final String  passwordEnv
	private final Integer cores = Runtime.runtime.availableProcessors()

	HikariConf(Map params) {
		this.driverEnv   = "${params.env_prefix}_DB_DRIVER"
		this.urlEnv      = "${params.env_prefix}_DB_JDBC_URL"
		this.usernameEnv = "${params.env_prefix}_DB_USERNAME"
		this.passwordEnv = "${params.env_prefix}_DB_PASSWORD"

		this.conf = new HikariConfig()
		this.conf.with {
			// authentication
			driverClassName = this.getEnv(driverEnv)
			jdbcUrl         = this.getEnv(urlEnv)
			username        = this.getEnv(usernameEnv)
			password        = this.getEnv(passwordEnv)
			// performance tuning
			maximumPoolSize = params.max_pool_size ?: (cores * 2)
			minimumIdle     = params.min_idle ?: cores
			// additional params
			params?.datasource_properties?.each { property, value ->
				addDataSourceProperty(property, value)
			}
		}
	}

	private String getEnv(String varName) {
		Objects.requireNonNull (
			System.getenv()[varName],
			"'${varName}' env variable is required"
		)
	}
}
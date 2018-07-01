package it.italiaonline.rnd.db
import com.zaxxer.hikari.HikariConfig

class HikariConf {

	final HikariConfig conf
	private final String  urlEnv
	private final String  usernameEnv
	private final String  passwordEnv
	private final String  driverEnv
	private final Integer cores = Runtime.runtime.availableProcessors()

	HikariConf(Map params) {
		this.urlEnv      = "${params.env_prefix}_DB_JDBC_URL"
		this.usernameEnv = "${params.env_prefix}_DB_USERNAME"
		this.passwordEnv = "${params.env_prefix}_DB_PASSWORD"
		this.driverEnv   = "${params.env_prefix}_DB_DRIVER"

		this.conf = new HikariConfig()
		this.conf.with {
			// authentication
			jdbcUrl         = this.getEnv(this.urlEnv)
			username        = this.getEnv(this.usernameEnv)
			password        = this.getEnv(this.passwordEnv)
			driverClassName = this.getEnv(this.driverEnv)
			// performance tuning
			maximumPoolSize = params.max_pool_size ?: cores
			minimumIdle     = params.min_idle ?: 0
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

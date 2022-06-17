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
			jdbcUrl         = this.bulletproofEnv(this.urlEnv)
			username        = this.bulletproofEnv(this.usernameEnv)
			password        = this.bulletproofEnv(this.passwordEnv)
			driverClassName = this.bulletproofEnv(this.driverEnv)
			// performance tuning
			maximumPoolSize = params.max_pool_size ?: cores
			minimumIdle     = params.min_idle ?: 0
			// additional params
			params?.datasource_properties?.each { property, value ->
				addDataSourceProperty(property, value)
			}
		}
	}

	private String bulletproofEnv(String var) {
		System.getenv()."${var}" ?: { throw new IllegalStateException("[${this.class.simpleName}] The environment variable ${var} is required") }()
	}

}

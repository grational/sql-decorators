package it.italiaonline.rnd.db

import groovy.sql.Sql
import javax.sql.DataSource

/**
 * Extends Sql class to use the withCloseable groovy construct
 */
class CloseableSql extends Sql implements Closeable {
	CloseableSql(DataSource ds) {
		super(
			Objects.requireNonNull(ds,"'DataSource' must not be null")
		)
	}

	@Override
	void close() {
		super.close()
	}
}

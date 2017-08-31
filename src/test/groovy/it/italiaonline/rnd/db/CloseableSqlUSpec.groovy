package it.italiaonline.rnd.db

import spock.lang.*
import javax.sql.DataSource

class CloseableSqlUSpec extends Specification {

	def 'Should return an AssertionError if the DataSource passed is null'() {
		when:
			def csql = new CloseableSql()
		then:
			def error = thrown(NullPointerException)
			error.message == "'DataSource' must not be null"
	}
}

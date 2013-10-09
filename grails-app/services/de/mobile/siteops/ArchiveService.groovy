package de.mobile.siteops

import groovy.sql.Sql
import java.text.SimpleDateFormat

class ArchiveService {

    javax.sql.DataSource dataSource

    static transactional = true

    def archive(Calendar calendar) {
        def sql = new Sql(dataSource)
        def correctedMonthNumber = calendar.get(Calendar.MONTH) + 1
        def year = calendar.get(Calendar.YEAR)
        def archiveTableName = year + '_' + new SimpleDateFormat("MMM").format(calendar.getTime())+ '_deployment_queue_entry'
        try {
            sql.execute("create table " + archiveTableName +
                " select * from deployment_queue_entry where MONTH(date_created) = " +
                correctedMonthNumber +
                " AND YEAR(date_created) = " + year + ";")
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException me) {
            log.info "problem creating table: " + me.message
        } finally {
            sql.close()
        }

    }
}

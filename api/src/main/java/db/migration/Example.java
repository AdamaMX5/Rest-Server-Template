package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

// Namingconvention: V002__Category
public class Example extends BaseJavaMigration {

    @Override
    public void migrate(Context context) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(
                new SingleConnectionDataSource(context.getConnection(), true));

        //       jdbcTemplate.execute("ALTER TABLE account ADD COLUMN is_special BOOLEAN");

        //       for (String name : AccountType.getNamesOfSpecialAccounts()) {
        //           jdbcTemplate.update("UPDATE account SET is_special = true WHERE type = ?", name);
        //       }
        //      for (String name : AccountType.getNamesOfRegularAccounts()) {
//            jdbcTemplate.update("UPDATE account SET is_special = false WHERE type = ?", name);
        //       }
    }
}

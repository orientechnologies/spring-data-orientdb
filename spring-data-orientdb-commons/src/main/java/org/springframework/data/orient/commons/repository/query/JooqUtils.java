package org.springframework.data.orient.commons.repository.query;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

public class JooqUtils {

    public static DSLContext context() {
        return DSL.using(SQLDialect.DEFAULT);
    }
    
    public static SelectJoinStep<? extends Record> from(String source) {
        return context().select().from(source);
    }
}

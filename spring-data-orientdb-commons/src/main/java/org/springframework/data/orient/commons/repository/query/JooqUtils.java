package org.springframework.data.orient.commons.repository.query;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

public final class JooqUtils {

    private JooqUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    public static DSLContext context() {
        return DSL.using(SQLDialect.MYSQL);
    }
    
    public static SelectJoinStep<? extends Record> from(String source) {
        return context().select().from(source);
    }
}

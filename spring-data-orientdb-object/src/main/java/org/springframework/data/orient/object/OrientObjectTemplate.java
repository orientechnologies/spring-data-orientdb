package org.springframework.data.orient.object;

import com.orientechnologies.orient.core.annotation.OId;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.springframework.data.orient.commons.core.AbstractOrientOperations;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class OrientObjectTemplate extends AbstractOrientOperations<Object> implements OrientObjectOperations {

    public OrientObjectTemplate(OrientObjectDatabaseFactory dbf) {
        super(dbf);
    }

    @Override
    public OObjectDatabaseTx getObjectDatabase() {
        return (OObjectDatabaseTx) dbf.db();
    }

    @Override
    public String getRid(Object entity) {
        Class<?> clazz = entity.getClass();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                OId ridAnnotation = field.getAnnotation(OId.class);
                if (ridAnnotation != null) {
                    field.setAccessible(true);
                    try {
                        Object rid = field.get(entity);
                        if (rid == null) {
                            Method method = clazz.getDeclaredMethod(getterName(field.getName()));
                            rid = method.invoke(entity);
                        }
                        return rid != null ? rid.toString() : null;
                    } catch (IllegalAccessException | IllegalArgumentException
                            | NoSuchMethodException | InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private String getterName(String propertyName) {
        return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1).toLowerCase();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detach(RET entities) {
        // NOTE: this re-implementation avoids unnecessary calls to getObjectDatabase()
        final OObjectDatabaseTx db = getObjectDatabase();

        List<Object> result = new ArrayList<>(entities.size());

        for (Object entity : entities) {
            result.add(db.detach(entity, true));
        }

        return (RET) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detachAll(RET entities) {
        // NOTE: this re-implementation avoids unnecessary calls to getObjectDatabase()
        final OObjectDatabaseTx db = getObjectDatabase();

        List<Object> result = new ArrayList<>(entities.size());

        for (Object entity : entities) {
            result.add(db.detachAll(entity, true));
        }

        return (RET) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET> RET detach(RET entity) {
        return (RET) getObjectDatabase().detach(entity, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET> RET detachAll(RET entity) {
        return (RET) getObjectDatabase().detachAll(entity, true);
    }


}

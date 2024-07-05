package com.phoenix_sat.phoenix_sat_backend.entity.converter;

import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ContentTypeConverter implements UserType<ContentType> {
    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<ContentType> returnedClass() {
        return ContentType.class;
    }

    @Override
    public boolean equals(ContentType x, ContentType y) {
        return x == y;
    }

    @Override
    public int hashCode(ContentType x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public ContentType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        if (value != null) {
            return ContentType.valueOf(value);
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, ContentType value, int index, SharedSessionContractImplementor session) throws SQLException {
        if(value == null) {
            st.setNull( index, Types.OTHER );
        } else {
            st.setObject(
                    index,
                    value.toString(),
                    Types.OTHER
            );
        }
    }

    @Override
    public ContentType deepCopy(ContentType value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(ContentType value) {
        return value.name();
    }

    @Override
    public ContentType assemble(Serializable cached, Object owner) {
        return ContentType.valueOf((String) cached);
    }
}

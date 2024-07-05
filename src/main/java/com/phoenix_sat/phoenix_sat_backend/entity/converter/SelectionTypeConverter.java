package com.phoenix_sat.phoenix_sat_backend.entity.converter;

import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;
import com.phoenix_sat.phoenix_sat_backend.enums.SelectionType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SelectionTypeConverter implements UserType<SelectionType> {
    @Override
    public int getSqlType() {
        return Types.OTHER;

    }

    @Override
    public Class<SelectionType> returnedClass() {
        return SelectionType.class;
    }

    @Override
    public boolean equals(SelectionType x, SelectionType y) {
        return x == y;
    }

    @Override
    public int hashCode(SelectionType x) {
        return x==null ? 0 : x.hashCode();
    }

    @Override
    public SelectionType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        if (value != null) {
            return SelectionType.valueOf(value);
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, SelectionType value, int index, SharedSessionContractImplementor session) throws SQLException {
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
    public SelectionType deepCopy(SelectionType value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(SelectionType value) {
        return value.name();
    }

    @Override
    public SelectionType assemble(Serializable cached, Object owner) {
        return SelectionType.valueOf((String) cached);
    }
}

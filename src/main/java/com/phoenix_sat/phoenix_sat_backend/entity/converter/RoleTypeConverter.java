package com.phoenix_sat.phoenix_sat_backend.entity.converter;

import com.phoenix_sat.phoenix_sat_backend.enums.RoleType;
import jakarta.persistence.Converter;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class RoleTypeConverter implements UserType<RoleType> {
    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<RoleType> returnedClass() {
        return RoleType.class;
    }

    @Override
    public boolean equals(RoleType x, RoleType y) {
        return x == y;
    }

    @Override
    public int hashCode(RoleType x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public RoleType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        if (value != null) {
            return RoleType.valueOf(value);
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, RoleType value, int index, SharedSessionContractImplementor session) throws SQLException {
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
    public RoleType deepCopy(RoleType value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(RoleType value) {
        return value.name();
    }

    @Override
    public RoleType assemble(Serializable cached, Object owner) {
        return RoleType.valueOf((String) cached);
    }
}

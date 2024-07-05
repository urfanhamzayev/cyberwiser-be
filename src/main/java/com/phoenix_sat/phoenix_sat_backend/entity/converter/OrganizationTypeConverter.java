package com.phoenix_sat.phoenix_sat_backend.entity.converter;

import com.phoenix_sat.phoenix_sat_backend.enums.OrganizationType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class OrganizationTypeConverter implements UserType<OrganizationType> {
    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<OrganizationType> returnedClass() {

        return OrganizationType.class;
    }

    @Override
    public boolean equals(OrganizationType x, OrganizationType y) {
        return x==y;
    }

    @Override
    public int hashCode(OrganizationType x) {
        return x==null ? 0 : x.hashCode();
    }

    @Override
    public OrganizationType nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        if (value != null) {
            return OrganizationType.valueOf(value);
        }
        return null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, OrganizationType value, int index, SharedSessionContractImplementor session) throws SQLException {
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
    public OrganizationType deepCopy(OrganizationType value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(OrganizationType value) {
        return value.name();
    }

    @Override
    public OrganizationType assemble(Serializable cached, Object owner) {
        return OrganizationType.valueOf((String) cached);
    }
}

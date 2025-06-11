package io.github.lklbjn.passkey.passkeydemo.config;

import com.yubico.webauthn.data.ByteArray;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author lklbjn
 */
@MappedTypes(ByteArray.class)
public class ByteArrayTypeHandler extends BaseTypeHandler<ByteArray> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ByteArray parameter, JdbcType jdbcType) throws SQLException {
        ps.setBytes(i, parameter.getBytes());
    }

    @Override
    public ByteArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte[] bytes = rs.getBytes(columnName);
        return bytes == null ? null : new ByteArray(bytes);
    }

    @Override
    public ByteArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte[] bytes = rs.getBytes(columnIndex);
        return bytes == null ? null : new ByteArray(bytes);
    }

    @Override
    public ByteArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte[] bytes = cs.getBytes(columnIndex);
        return bytes == null ? null : new ByteArray(bytes);
    }
}

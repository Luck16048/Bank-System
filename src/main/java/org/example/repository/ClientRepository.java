package org.example.repository;

import org.example.config.ConnectDb;
import org.example.entity.ClientEntity;
import org.example.entity.RoleEnum;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;


public class ClientRepository {
    public ClientEntity getByAccountNumber(String accountNumber) throws SQLException {
        DSLContext dsl = ConnectDb.getDSL();

        Record record = dsl.select()
                .from("clients")
                .where(field("account_number").eq(accountNumber))
                .fetchOne();

        return record != null ? mapToEntity(record) : null;
    }

    public ClientEntity getByPesel(String pesel) throws SQLException {
        DSLContext dsl = ConnectDb.getDSL();

        Record record = dsl.select()
                .from("clients")
                .where(field("pesel").eq(pesel))
                .fetchOne();

        return record != null ? mapToEntity(record) : null;
    }

    public boolean existsByAccountNumber(String accountNumber) throws SQLException {
        DSLContext dsl = ConnectDb.getDSL();

        return dsl.fetchExists(
                dsl.select()
                        .from("clients")
                        .where(field("account_number").eq(accountNumber))
                        .and(field("active").eq(true))
        );
    }

    public void save(ClientEntity client) throws SQLException {
        DSLContext dsl = ConnectDb.getDSL();

        dsl.insertInto(table("clients"),
                        field("first_name"),
                        field("last_name"),
                        field("pesel"),
                        field("account_number"),
                        field("password_hash"),
                        field("balance"),
                        field("role"))
                .values(
                        client.getFirstName(),
                        client.getLastName(),
                        client.getPesel(),
                        client.getAccountNumber(),
                        client.getPasswordHash(),
                        client.getBalance(),
                        client.getRole().name())
                .execute();
    }

    public void updateBalance(String accountNumber, BigDecimal newBalance) throws SQLException {
        DSLContext dsl = ConnectDb.getDSL();

        dsl.update(table("clients"))
                .set(field("balance"), newBalance)
                .where(field("account_number").eq(accountNumber))
                .execute();
    }

    public void updateById(ClientEntity client) throws SQLException {
        DSLContext dsl = ConnectDb.getDSL();

        dsl.update(table("clients"))
                .set(field("first_name"), client.getFirstName())
                .set(field("last_name"), client.getLastName())
                .set(field("pesel"), client.getPesel())
                .where(field("account_number").eq(client.getAccountNumber()))
                .execute();
    }

    public void deleteById(String accountNumber) throws SQLException {
        DSLContext dsl = ConnectDb.getDSL();

        dsl.update(table("clients"))
                .set(field("active"), false)
                .where(field("account_number").eq(accountNumber))
                .execute();
    }

    private ClientEntity mapToEntity(Record record) {
        ClientEntity client = new ClientEntity();
        client.setId(record.get("id", Integer.class));
        client.setFirstName(record.get("first_name", String.class));
        client.setLastName(record.get("last_name", String.class));
        client.setPesel(record.get("pesel", String.class));
        client.setAccountNumber(record.get("account_number", String.class));
        client.setPasswordHash(record.get("password_hash", String.class));
        client.setBalance(record.get("balance", BigDecimal.class));
        client.setRole(RoleEnum.valueOf(record.get("role", String.class)));
        client.setActive(record.get("active", Boolean.class));
        client.setCreatedAt(record.get("created_at", LocalDateTime.class));
        return client;
    }
}

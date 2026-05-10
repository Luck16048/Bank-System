package org.example.repository;

import org.example.config.ConnectDb;
import org.example.entity.OperationEntity;
import org.example.entity.OperationTypeEnum;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class OperationRepository {
    public void save(OperationEntity operation) throws SQLException {
        DSLContext dsl = ConnectDb.getDSL();

        dsl.insertInto(table("operations"),
                        field("account_number"),
                        field("type"),
                        field("amount"),
                        field("target_account"))
                .values(
                        operation.getAccountNumber(),
                        operation.getType().name(),
                        operation.getAmount(),
                        DSL.val(operation.getTargetAccount(), String.class))
                .execute();
    }

    public List<OperationEntity> getByAccountNumber(String accountNumber) throws SQLException {
        DSLContext dsl = ConnectDb.getDSL();

        Result<Record> result = dsl.select()
                .from("operations")
                .where(field("account_number").eq(accountNumber))
                .orderBy(field("created_at").desc())
                .fetch();

        List<OperationEntity> operations = new ArrayList<>();
        for (Record record : result) {
            operations.add(mapToEntity(record));
        }
        return operations;
    }

    private OperationEntity mapToEntity(Record record) {
        OperationEntity op = new OperationEntity();
        op.setId(record.get("id", Integer.class));
        op.setAccountNumber(record.get("account_number", String.class));
        op.setType(OperationTypeEnum.valueOf(record.get("type", String.class)));
        op.setAmount(record.get("amount", BigDecimal.class));
        op.setTargetAccount(record.get("target_account", String.class));
        op.setCreatedAt(record.get("created_at", LocalDateTime.class));
        return op;
    }
}

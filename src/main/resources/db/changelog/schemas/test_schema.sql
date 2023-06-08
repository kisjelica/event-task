--liquibase formatted sql
--changeset test_user:test_schema splitStatements:true endDelimiter:; dbms:postgresql runOnChange:true runInTransaction:true context:dev, qa, prod logicalFilePath:schema/test_schema.sql
--preconditions onFail:HALT onError:HALT


CREATE TABLE event
(
    id         SERIAL PRIMARY KEY,
    source     character varying(100),
    url        character varying(100),
    args       JSONB,
    recordedAt timestamp with time zone
);
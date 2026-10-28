-- Hibernate-specific seed script, executed only with ddl-auto=create/create-drop.
-- H2 SQL (the original demo's ExampleDS and sequence naming were app-server-specific).
INSERT INTO user_account (id, username, age, gender) VALUES (NEXT VALUE FOR user_account_id_seq, 'Homer', 37, 'MALE');
INSERT INTO user_account (id, username, age, gender) VALUES (NEXT VALUE FOR user_account_id_seq, 'Bart', 19, 'MALE');
INSERT INTO user_account (id, username, age, gender) VALUES (NEXT VALUE FOR user_account_id_seq, 'Marge', 35, 'FEMALE');

-- Identical three sample users as master; standard H2 sequence syntax.
INSERT INTO USERACCOUNT (id, username, age, gender) VALUES (NEXT VALUE FOR userAccountId_seq, 'Homer', 37, 'MALE');
INSERT INTO USERACCOUNT (id, username, age, gender) VALUES (NEXT VALUE FOR userAccountId_seq, 'Bart', 19, 'MALE');
INSERT INTO USERACCOUNT (id, username, age, gender) VALUES (NEXT VALUE FOR userAccountId_seq, 'Marge', 35, 'FEMALE');

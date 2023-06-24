CREATE TABLE alias_partner(
alias varchar(50) NOT NULL,
partner_id VARCHAR(50) NOT NULL,
user_id VARCHAR(10) NOT NULL
);

CREATE UNIQUE INDEX alias_idx ON alias_partner(partner_id,user_id);
CREATE USER alias_user PASSWORD 'password';
GRANT SELECT, INSERT ON alias_partner TO alias_user;
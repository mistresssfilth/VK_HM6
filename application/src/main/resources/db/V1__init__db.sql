CREATE TABLE organizations
(
    inn              SERIAL,
    name             VARCHAR NOT NULL,
    checking_account int     NOT NULL,
    CONSTRAINT org_pk PRIMARY KEY (inn)
);
CREATE TABLE invoices
(
    id     SERIAL,
    date   DATE NOT NULL,
    org_id int  NOT NULL REFERENCES organizations (inn) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT invoices_pk PRIMARY KEY (id)
);
CREATE TABLE products
(
    code SERIAL,
    name VARCHAR NOT NULL,
    CONSTRAINT products_pk PRIMARY KEY (code)
);
CREATE TABLE positions
(
    id         SERIAL,
    price      int NOT NULL,
    product_id int NOT NULL REFERENCES products (code) ON UPDATE CASCADE ON DELETE CASCADE,
    invoice_id int NOT NULL REFERENCES invoices(id) ON UPDATE CASCADE ON DELETE CASCADE,
    count      int,
    CONSTRAINT pos_pk PRIMARY KEY (id)
);
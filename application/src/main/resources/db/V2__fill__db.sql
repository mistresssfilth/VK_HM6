INSERT INTO products(code, name)
VALUES
    (101, 'Item 1'),
    (112, 'Item 2'),
    (141, 'Item 3'),
    (353, 'Item 4'),
    (242, 'Item 5');
INSERT INTO organizations(inn, name, checking_account)
VALUES
    (1287, 'Provider 1', 258193),
    (1589, 'Provider 2', 158538),
    (1940, 'Provider 3', 563139),
    (1481, 'Provider 4', 614783),
    (1417, 'Provider 5', 157850);
INSERT INTO invoices(date, org_id)
VALUES
    ('2022-05-03', 1287),
    ('2021-08-10', 1287),
    ('2022-11-13', 1481),
    ('2019-03-06', 1589),
    ('2022-11-08', 1940);
INSERT INTO positions(price, product_id, invoice_id, count)
VALUES
    (1100, 101, 1, 1),
    (1300, 353, 1, 2),
    (12200, 141, 3, 1),
    (3369, 112, 4, 4),
    (390, 242, 2, 10);
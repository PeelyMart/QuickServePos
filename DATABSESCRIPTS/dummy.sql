USE restaurantdb;

-- ================================================
-- STAFF
-- ================================================
INSERT INTO staff (staff_pin, first_name, last_name, contact_number)
VALUES
    (1234, 'John', 'Doe', '0917123456'),
    (5678, 'Jane', 'Smith', '0917987654'),
    (9012, 'Michael', 'Brown', '0917123987'),
    (3456, 'Emily', 'Davis', '0917234567');

-- ================================================
-- STAFF TRACKER
-- ================================================
INSERT INTO staff_tracker (staff_id, date, time_in, time_out, session_minutes)
VALUES
    (1, '2024-05-01', '2024-05-01 09:00:00', '2024-05-01 17:00:00', 480),
    (2, '2024-05-01', '2024-05-01 10:00:00', '2024-05-01 18:00:00', 480);

-- ================================================
-- TABLES
-- ================================================
INSERT INTO tables (capacity, is_available)
VALUES
    (4, 1),
    (2, 1),
    (6, 1),
    (4, 1),
    (8, 1);

-- ================================================
-- LOYALTY MEMBERS
-- ================================================
INSERT INTO loyalty_members (first_name, last_name, contact_number, join_date, points, is_active)
VALUES
    ('Alice', 'Reyes', '0917001122', '2024-01-15', 120, 1),
    ('Mark', 'Santos', '0917555333', '2024-02-05', 90, 1),
    ('Lea', 'Torres', '0917888999', '2024-03-10', 50, 1);

-- ================================================
-- MENU ITEMS
-- ================================================
INSERT INTO menu_items (name, description, price, is_available)
VALUES
    ('Burger', 'Juicy beef burger', 100.00, 1),
    ('Fried Chicken', '2-piece crispy chicken', 150.00, 1),
    ('Pasta', 'Creamy carbonara pasta', 250.00, 1),
    ('Milkshake', 'Vanilla milkshake', 80.00, 1),
    ('Steak', 'Grilled sirloin steak', 350.00, 1),
    ('Pizza', '12-inch cheese pizza', 499.00, 1);

-- ================================================
-- RESERVATIONS
-- ================================================
INSERT INTO reservations (table_id, reserve_name, date_and_time)
VALUES
    (1, 'Carlos', '2024-05-12 18:00:00'),
    (3, 'Mira', '2024-05-12 20:00:00');

-- ================================================
-- FIVE PAID ORDERS
-- ================================================

-- ORDER 1
INSERT INTO order_header (table_id, staff_id, total_cost, status)
VALUES (1, 1, 450.00, 'closed');

INSERT INTO order_item (order_id, menu_id, quantity, subtotal) VALUES
(1, 1, 2, 200.00),
(1, 3, 1, 250.00);

INSERT INTO payments (order_id, amount_paid, staff_id, payment_method, loyal_customer_id)
VALUES (1, 450.00, 1, 'CASH', 1);

-- ORDER 2
INSERT INTO order_header (table_id, staff_id, total_cost, status)
VALUES (2, 2, 780.00, 'closed');

INSERT INTO order_item (order_id, menu_id, quantity, subtotal) VALUES
(2, 2, 3, 450.00),
(2, 4, 2, 330.00);

INSERT INTO payments (order_id, amount_paid, staff_id, payment_method, unknown_customer_name)
VALUES (2, 780.00, 2, 'DEBIT', 'Walk-in Customer');

-- ORDER 3
INSERT INTO order_header (table_id, staff_id, total_cost, status)
VALUES (3, 3, 1200.00, 'closed');

INSERT INTO order_item (order_id, menu_id, quantity, subtotal) VALUES
(3, 5, 2, 700.00),
(3, 6, 1, 500.00);

INSERT INTO payments (order_id, amount_paid, staff_id, payment_method)
VALUES (3, 1200.00, 3, 'CREDIT');

-- ORDER 4
INSERT INTO order_header (table_id, staff_id, total_cost, status)
VALUES (4, 1, 300.00, 'closed');

INSERT INTO order_item (order_id, menu_id, quantity, subtotal) VALUES
(4, 3, 1, 250.00),
(4, 4, 1, 50.00);

INSERT INTO payments (order_id, amount_paid, staff_id, payment_method, loyal_customer_id)
VALUES (4, 300.00, 1, 'CASH', 2);

-- ORDER 5
INSERT INTO order_header (table_id, staff_id, total_cost, status)
VALUES (5, 4, 999.00, 'closed');

INSERT INTO order_item (order_id, menu_id, quantity, subtotal) VALUES
(5, 2, 1, 150.00),
(5, 5, 1, 350.00),
(5, 6, 1, 499.00);

INSERT INTO payments (order_id, amount_paid, staff_id, payment_method, unknown_customer_name)
VALUES (5, 999.00, 4, 'CREDIT', 'Mr. Garcia');

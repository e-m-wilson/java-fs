
-- challenge day 1 answers

SELECT * FROM customer;


SELECT * 
FROM customer
WHERE state = 'AZ';


SELECT *
FROM invoice
WHERE invoice_date >= NOW() - INTERVAL '6 months';


UPDATE customer
SET phone = NULL
WHERE phone !~ '^\+\d \(\d{3}\) \d{3}-\d{4}$';


-- UPDATE customer
-- SET phone = REGEXP_REPLACE(phone, '^\+\d ', '')
-- WHERE phone ~ '^\+\d \(\d{3}\) \d{3}-\d{4}$';


-- ALTER TABLE customer
-- ADD CONSTRAINT phone_us
-- CHECK (phone ~ '^\(\d{3}\) \d{3}-\d{4}$');



SELECT * FROM track
WHERE milliseconds > 180000;



UPDATE customer
SET country='USA', address=NULL, city=NULL, state=NULL
WHERE country != 'USA';


CREATE OR REPLACE FUNCTION get_customer_spending(
    c_id INT
)
RETURNS NUMERIC
LANGUAGE plpgsql
AS $$
DECLARE
    total_spending NUMERIC;
BEGIN
    SELECT COALESCE(SUM(total), 0)
    INTO total_spending
    FROM invoice
    WHERE customer_id = c_id;

    RETURN total_spending;
END;
$$;

SELECT get_customer_spending(5);


CREATE OR REPLACE PROCEDURE update_employee_manager(
    p_employee_id INT,
    p_new_manager_id INT
)
LANGUAGE plpgsql
AS $$
BEGIN

    -- Check if employee exists
    IF NOT EXISTS (
        SELECT 1
        FROM employee
        WHERE employee_id = p_employee_id
    ) THEN
        RAISE EXCEPTION 'Employee does not exist';
    END IF;


    -- Prevent employee from reporting to themselves
    IF p_employee_id = p_new_manager_id THEN
        RAISE EXCEPTION 'Employee cannot report to themselves';
    END IF;


    -- Check if manager exists
    IF NOT EXISTS (
        SELECT 1
        FROM employee
        WHERE employee_id = p_new_manager_id
    ) THEN
        RAISE EXCEPTION 'Manager does not exist';
    END IF;


    -- Check for circular management relationship
    IF EXISTS (
        WITH RECURSIVE manager_chain AS (

            SELECT employee_id, reports_to
            FROM employee
            WHERE employee_id = p_new_manager_id

            UNION

            SELECT e.employee_id, e.reports_to
            FROM employee e
            JOIN manager_chain mc
                ON e.employee_id = mc.reports_to
        )

        SELECT 1
        FROM manager_chain
        WHERE employee_id = p_employee_id

    ) THEN
        RAISE EXCEPTION 'Circular management relationship detected';
    END IF;

    -- Update manager
    UPDATE employee
    SET reports_to = p_new_manager_id
    WHERE employee_id = p_employee_id;

END;
$$;


-- Example procedure call
CALL update_employee_manager(5, 2);


SELECT * FROM employee;
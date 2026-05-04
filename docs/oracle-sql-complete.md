# 📘 Oracle SQL & PL/SQL — Tài liệu tham khảo toàn diện

> Schema mẫu dùng xuyên suốt tài liệu

```sql
CREATE TABLE departments (
    dept_id NUMBER PRIMARY KEY, dept_name VARCHAR2(100) NOT NULL,
    location VARCHAR2(100), manager_id NUMBER, budget NUMBER(15,2)
);
CREATE TABLE employees (
    emp_id NUMBER PRIMARY KEY, first_name VARCHAR2(50), last_name VARCHAR2(50),
    email VARCHAR2(100) UNIQUE, salary NUMBER(10,2), commission_pct NUMBER(3,2),
    hire_date DATE DEFAULT SYSDATE, dept_id NUMBER REFERENCES departments(dept_id),
    manager_id NUMBER REFERENCES employees(emp_id), status VARCHAR2(20) DEFAULT 'ACTIVE',
    phone VARCHAR2(20), address VARCHAR2(500)
);
CREATE TABLE orders (
    order_id NUMBER PRIMARY KEY, emp_id NUMBER REFERENCES employees(emp_id),
    customer_id NUMBER, order_date DATE, total NUMBER(12,2), status VARCHAR2(20)
);
CREATE TABLE products (
    product_id NUMBER PRIMARY KEY, product_name VARCHAR2(200),
    category VARCHAR2(50), price NUMBER(10,2), stock NUMBER
);
CREATE TABLE order_items (
    item_id NUMBER PRIMARY KEY, order_id NUMBER REFERENCES orders(order_id),
    product_id NUMBER REFERENCES products(product_id), quantity NUMBER, unit_price NUMBER(10,2)
);
```

---

# PHẦN 1: CORE SQL

---

## 1. SELECT

Truy xuất dữ liệu. Oracle xử lý theo thứ tự: `FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY`.

```sql
-- Cơ bản
SELECT * FROM employees;
SELECT emp_id, first_name, salary FROM employees;

-- Alias + biểu thức
SELECT first_name || ' ' || last_name AS full_name,
       salary AS "Lương tháng", salary * 12 AS "Lương năm",
       ROUND(salary / 22, 0) AS daily_rate
FROM employees;

-- DUAL — bảng ảo Oracle
SELECT SYSDATE, SYSTIMESTAMP, USER,
       SYS_CONTEXT('USERENV','DB_NAME') AS db_name,
       SYS_CONTEXT('USERENV','IP_ADDRESS') AS client_ip
FROM dual;

-- Subquery trong SELECT (scalar subquery)
SELECT e.first_name, e.salary,
       (SELECT d.dept_name FROM departments d WHERE d.dept_id = e.dept_id) AS dept_name,
       (SELECT AVG(salary) FROM employees WHERE dept_id = e.dept_id) AS dept_avg
FROM employees e;

-- WITH clause (CTE — Common Table Expression)
WITH dept_stats AS (
    SELECT dept_id, COUNT(*) AS cnt, AVG(salary) AS avg_sal, SUM(salary) AS total_sal
    FROM employees WHERE status = 'ACTIVE' GROUP BY dept_id
),
company_avg AS (
    SELECT AVG(salary) AS overall_avg FROM employees WHERE status = 'ACTIVE'
)
SELECT d.dept_name, ds.cnt, ROUND(ds.avg_sal) AS avg_salary,
       ROUND(ds.avg_sal - ca.overall_avg) AS diff_from_company_avg
FROM dept_stats ds
JOIN departments d ON ds.dept_id = d.dept_id
CROSS JOIN company_avg ca
ORDER BY ds.avg_sal DESC;

-- Recursive CTE — cây tổ chức
WITH org_tree (emp_id, full_name, manager_id, lvl, path) AS (
    SELECT emp_id, first_name||' '||last_name, manager_id, 1,
           CAST(first_name||' '||last_name AS VARCHAR2(4000))
    FROM employees WHERE manager_id IS NULL
    UNION ALL
    SELECT e.emp_id, e.first_name||' '||e.last_name, e.manager_id, t.lvl+1,
           t.path || ' → ' || e.first_name||' '||e.last_name
    FROM employees e JOIN org_tree t ON e.manager_id = t.emp_id
)
SELECT LPAD(' ', (lvl-1)*4) || full_name AS org_chart, lvl, path FROM org_tree ORDER BY path;

-- PIVOT — chuyển dòng thành cột
SELECT * FROM (
    SELECT dept_id, status, salary FROM employees
)
PIVOT (
    COUNT(*) AS cnt, SUM(salary) AS total
    FOR status IN ('ACTIVE' AS active, 'INACTIVE' AS inactive, 'TERMINATED' AS termed)
);

-- UNPIVOT — chuyển cột thành dòng
SELECT * FROM (
    SELECT emp_id, first_name, salary, commission_pct FROM employees
)
UNPIVOT (
    amount FOR comp_type IN (salary AS 'SALARY', commission_pct AS 'COMMISSION')
);
```

---

## 2. INSERT

```sql
-- Single row
INSERT INTO employees (emp_id, first_name, last_name, email, salary, dept_id)
VALUES (1001, 'Nguyen', 'Van A', 'nva@company.com', 15000000, 10);

-- RETURNING — lấy giá trị sau insert
DECLARE v_id NUMBER;
BEGIN
    INSERT INTO employees (emp_id, first_name, last_name, email, salary, dept_id)
    VALUES (seq_emp.NEXTVAL, 'Le', 'Van B', 'lvb@company.com', 18000000, 20)
    RETURNING emp_id INTO v_id;
    DBMS_OUTPUT.PUT_LINE('New ID: ' || v_id);
END;
/

-- Insert from SELECT
INSERT INTO employees_archive SELECT * FROM employees WHERE status = 'TERMINATED';

-- INSERT ALL — multi-table unconditional
INSERT ALL
    INTO emp_contacts (emp_id, email) VALUES (emp_id, email)
    INTO emp_salaries (emp_id, salary, dept_id) VALUES (emp_id, salary, dept_id)
SELECT emp_id, email, salary, dept_id FROM employees WHERE hire_date = TRUNC(SYSDATE);

-- INSERT ALL conditional (WHEN)
INSERT ALL
    WHEN total >= 10000000 THEN INTO orders_vip VALUES (order_id, customer_id, total)
    WHEN total >= 1000000 THEN INTO orders_normal VALUES (order_id, customer_id, total)
    ELSE INTO orders_small VALUES (order_id, customer_id, total)
SELECT order_id, customer_id, total FROM orders WHERE order_date = TRUNC(SYSDATE);

-- INSERT FIRST — chỉ insert vào bảng đầu tiên match
INSERT FIRST
    WHEN salary > 30000000 THEN INTO emp_senior VALUES (emp_id, salary)
    WHEN salary > 15000000 THEN INTO emp_mid VALUES (emp_id, salary)
    ELSE INTO emp_junior VALUES (emp_id, salary)
SELECT emp_id, salary FROM employees;

-- Direct-path INSERT (bypass buffer cache — nhanh cho bulk)
INSERT /*+ APPEND */ INTO employees_archive
SELECT * FROM employees WHERE status = 'INACTIVE';
COMMIT; -- bắt buộc commit ngay sau APPEND

-- INSERT với error logging (skip lỗi thay vì abort)
INSERT INTO employees SELECT * FROM employees_staging
LOG ERRORS INTO err_employees ('batch_2025') REJECT LIMIT UNLIMITED;
```

---

## 3. UPDATE

```sql
-- Cơ bản
UPDATE employees SET salary = salary * 1.1 WHERE dept_id = 10;

-- Update nhiều cột
UPDATE employees SET salary = 25000000, status = 'ACTIVE', dept_id = 20 WHERE emp_id = 1001;

-- RETURNING
UPDATE employees SET salary = salary * 1.15 WHERE emp_id = 1001
RETURNING emp_id, salary INTO :v_id, :v_new_sal;

-- Correlated subquery update
UPDATE employees e SET salary = (
    SELECT AVG(salary) * 1.1 FROM employees WHERE dept_id = e.dept_id
) WHERE e.salary < (SELECT AVG(salary) FROM employees WHERE dept_id = e.dept_id);

-- Update từ bảng khác
UPDATE employees e SET (salary, dept_id) = (
    SELECT s.salary, s.dept_id FROM employees_staging s WHERE s.emp_id = e.emp_id
) WHERE EXISTS (SELECT 1 FROM employees_staging s WHERE s.emp_id = e.emp_id);

-- Update với CASE
UPDATE employees SET salary = CASE
    WHEN dept_id = 10 AND salary < 20000000 THEN salary * 1.15
    WHEN dept_id = 20 AND salary < 25000000 THEN salary * 1.10
    ELSE salary * 1.05
END WHERE status = 'ACTIVE';

-- Update với JOIN (Oracle syntax)
UPDATE (
    SELECT e.salary AS emp_salary, s.new_salary
    FROM employees e
    JOIN salary_adjustments s ON e.emp_id = s.emp_id
    WHERE s.effective_date = TRUNC(SYSDATE)
) SET emp_salary = new_salary;
-- ⚠️ Yêu cầu: bảng join phải có key-preserved (unique constraint trên join key)

-- Inline view update alternative
MERGE INTO employees e
USING salary_adjustments s ON (e.emp_id = s.emp_id)
WHEN MATCHED THEN UPDATE SET e.salary = s.new_salary;
```

---

## 4. DELETE

```sql
-- Cơ bản
DELETE FROM orders WHERE status = 'CANCELLED' AND order_date < ADD_MONTHS(SYSDATE, -12);

-- RETURNING
DELETE FROM employees WHERE emp_id = 1001 RETURNING first_name, salary INTO :v_name, :v_sal;

-- Subquery delete
DELETE FROM employees WHERE dept_id IN (
    SELECT dept_id FROM departments WHERE location = 'OLD_OFFICE'
);

-- Correlated delete — xóa trùng lặp giữ mới nhất
DELETE FROM employees e1 WHERE ROWID < (
    SELECT MAX(ROWID) FROM employees e2 WHERE e1.email = e2.email
);

-- Xóa trùng lặp dùng ROW_NUMBER
DELETE FROM employees WHERE ROWID IN (
    SELECT rid FROM (
        SELECT ROWID AS rid, ROW_NUMBER() OVER (PARTITION BY email ORDER BY emp_id DESC) AS rn
        FROM employees
    ) WHERE rn > 1
);

-- DELETE với error logging
DELETE FROM order_items WHERE order_id IN (SELECT order_id FROM orders WHERE status = 'VOID')
LOG ERRORS INTO err_order_items REJECT LIMIT 100;
```

---

## 5. MERGE (UPSERT)

```sql
-- MERGE đầy đủ — đồng bộ staging → production
MERGE INTO employees tgt
USING employees_staging src ON (tgt.emp_id = src.emp_id)
WHEN MATCHED THEN UPDATE SET
    tgt.first_name = src.first_name, tgt.salary = src.salary,
    tgt.dept_id = src.dept_id, tgt.status = src.status
    DELETE WHERE src.status = 'TERMINATED'  -- xóa luôn dòng matched nếu terminated
WHEN NOT MATCHED THEN INSERT
    (emp_id, first_name, last_name, email, salary, dept_id, status)
    VALUES (src.emp_id, src.first_name, src.last_name, src.email, src.salary, src.dept_id, 'ACTIVE');

-- MERGE chỉ UPDATE (không insert)
MERGE INTO products p
USING (SELECT product_id, SUM(quantity) AS sold FROM order_items GROUP BY product_id) s
ON (p.product_id = s.product_id)
WHEN MATCHED THEN UPDATE SET p.stock = p.stock - s.sold WHERE p.stock >= s.sold;

-- MERGE chỉ INSERT (không update)
MERGE INTO employees tgt
USING new_hires src ON (tgt.email = src.email)
WHEN NOT MATCHED THEN INSERT (emp_id, first_name, last_name, email, salary, dept_id)
VALUES (seq_emp.NEXTVAL, src.first_name, src.last_name, src.email, src.salary, src.dept_id);

-- MERGE với điều kiện phức tạp
MERGE INTO inventory tgt
USING (
    SELECT product_id, warehouse_id, SUM(qty) AS total_qty, MAX(receipt_date) AS last_date
    FROM incoming_shipments WHERE processed = 'N' GROUP BY product_id, warehouse_id
) src ON (tgt.product_id = src.product_id AND tgt.warehouse_id = src.warehouse_id)
WHEN MATCHED THEN UPDATE SET
    tgt.quantity = tgt.quantity + src.total_qty, tgt.last_updated = SYSDATE
WHEN NOT MATCHED THEN INSERT (product_id, warehouse_id, quantity, last_updated)
VALUES (src.product_id, src.warehouse_id, src.total_qty, SYSDATE);
```

---

## 6. WHERE — Điều kiện lọc

```sql
-- So sánh
WHERE salary > 20000000
WHERE salary BETWEEN 15000000 AND 30000000   -- inclusive cả hai đầu
WHERE dept_id IN (10, 20, 30)
WHERE first_name LIKE 'Nguyen%'              -- bắt đầu bằng
WHERE email LIKE '%@gmail.com'               -- kết thúc bằng
WHERE first_name LIKE '__an%'                -- 2 ký tự bất kỳ + "an"
WHERE manager_id IS NULL
WHERE manager_id IS NOT NULL

-- Logic
WHERE dept_id = 10 AND salary > 15000000 AND status = 'ACTIVE'
WHERE dept_id = 10 OR dept_id = 20
WHERE NOT (status = 'TERMINATED')

-- Regular Expression (Oracle specific)
WHERE REGEXP_LIKE(email, '^[a-z]+\.[a-z]+@company\.com$')
WHERE REGEXP_LIKE(phone, '^\+84[0-9]{9,10}$')
WHERE REGEXP_LIKE(first_name, '^(Nguyen|Tran|Le)', 'i')  -- case insensitive

-- REGEXP functions
SELECT REGEXP_SUBSTR('abc-123-def', '[0-9]+') FROM dual;           -- '123'
SELECT REGEXP_REPLACE('phone: 0901234567', '[^0-9]', '') FROM dual; -- '0901234567'
SELECT REGEXP_INSTR('hello world 123', '[0-9]') FROM dual;         -- 13
SELECT REGEXP_COUNT('aaa bbb aaa ccc aaa', 'aaa') FROM dual;       -- 3

-- EXISTS (correlated subquery — thường nhanh hơn IN)
WHERE EXISTS (SELECT 1 FROM orders o WHERE o.emp_id = e.emp_id AND o.total > 5000000)
WHERE NOT EXISTS (SELECT 1 FROM orders o WHERE o.emp_id = e.emp_id)

-- Subquery operators
WHERE salary > ANY (SELECT salary FROM employees WHERE dept_id = 20)  -- > MIN
WHERE salary > ALL (SELECT salary FROM employees WHERE dept_id = 20)  -- > MAX
WHERE dept_id = ANY (10, 20, 30)  -- tương đương IN

-- ⚠️ NOT IN vs NOT EXISTS khi có NULL
-- ❌ NOT IN trả 0 dòng nếu subquery chứa NULL
SELECT * FROM employees WHERE dept_id NOT IN (SELECT dept_id FROM departments);
-- ✅ Dùng NOT EXISTS an toàn hơn
SELECT * FROM employees e WHERE NOT EXISTS (
    SELECT 1 FROM departments d WHERE d.dept_id = e.dept_id);
```

---

## 7. GROUP BY & HAVING

```sql
-- Cơ bản
SELECT dept_id, COUNT(*) AS cnt, AVG(salary) AS avg_sal
FROM employees GROUP BY dept_id HAVING COUNT(*) > 5;

-- ROLLUP — subtotals theo thứ bậc
SELECT dept_id, status, COUNT(*), SUM(salary)
FROM employees GROUP BY ROLLUP(dept_id, status);
-- Tạo subtotal: (dept,status), (dept), (grand total)

-- CUBE — subtotals mọi tổ hợp
SELECT dept_id, status, COUNT(*), SUM(salary)
FROM employees GROUP BY CUBE(dept_id, status);
-- Tạo subtotal: (dept,status), (dept), (status), (grand total)

-- GROUPING SETS — chọn tổ hợp cụ thể
SELECT dept_id, TO_CHAR(hire_date,'YYYY') AS yr, COUNT(*), SUM(salary)
FROM employees
GROUP BY GROUPING SETS ((dept_id), (TO_CHAR(hire_date,'YYYY')), ());
-- Tổng theo dept, tổng theo năm, grand total — không có cross

-- GROUPING() — phân biệt NULL thật vs subtotal NULL
SELECT
    CASE WHEN GROUPING(dept_id) = 1 THEN 'ALL DEPTS' ELSE TO_CHAR(dept_id) END AS dept,
    CASE WHEN GROUPING(status) = 1 THEN 'ALL STATUS' ELSE status END AS status,
    COUNT(*), SUM(salary)
FROM employees GROUP BY ROLLUP(dept_id, status);

-- LISTAGG — gom giá trị thành chuỗi
SELECT dept_id,
       LISTAGG(first_name, ', ') WITHIN GROUP (ORDER BY first_name) AS emp_names
FROM employees GROUP BY dept_id;

-- LISTAGG với DISTINCT (19c+)
SELECT dept_id,
       LISTAGG(DISTINCT status, ', ') WITHIN GROUP (ORDER BY status) AS statuses
FROM employees GROUP BY dept_id;
```

---

## 8. ORDER BY & DISTINCT

```sql
-- Sắp xếp
ORDER BY salary DESC
ORDER BY dept_id ASC, salary DESC
ORDER BY 3 DESC                              -- theo cột thứ 3 (không khuyến khích)
ORDER BY manager_id NULLS LAST               -- Oracle specific: NULL ở cuối
ORDER BY manager_id NULLS FIRST              -- NULL ở đầu
ORDER BY DECODE(status, 'URGENT',1, 'PENDING',2, 'DONE',3, 4)  -- custom order

-- DISTINCT
SELECT DISTINCT dept_id FROM employees;
SELECT DISTINCT dept_id, status FROM employees;
SELECT COUNT(DISTINCT dept_id) AS unique_depts FROM employees;

-- FETCH FIRST (12c+) — thay thế ROWNUM
SELECT * FROM employees ORDER BY salary DESC FETCH FIRST 10 ROWS ONLY;
SELECT * FROM employees ORDER BY salary DESC FETCH FIRST 10 PERCENT ROWS ONLY;
SELECT * FROM employees ORDER BY salary DESC OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY;
SELECT * FROM employees ORDER BY salary DESC FETCH FIRST 5 ROWS WITH TIES;
```

---

## 9. JOIN

```sql
-- INNER JOIN
SELECT e.first_name, d.dept_name FROM employees e
INNER JOIN departments d ON e.dept_id = d.dept_id;

-- LEFT OUTER JOIN — tất cả employees, kể cả chưa có phòng ban
SELECT e.first_name, d.dept_name FROM employees e
LEFT JOIN departments d ON e.dept_id = d.dept_id;

-- RIGHT OUTER JOIN
SELECT e.first_name, d.dept_name FROM employees e
RIGHT JOIN departments d ON e.dept_id = d.dept_id;

-- FULL OUTER JOIN
SELECT e.first_name, d.dept_name FROM employees e
FULL OUTER JOIN departments d ON e.dept_id = d.dept_id;

-- CROSS JOIN (Cartesian product)
SELECT e.first_name, p.product_name FROM employees e CROSS JOIN products p;

-- SELF JOIN — tìm manager
SELECT e.first_name AS employee, m.first_name AS manager
FROM employees e LEFT JOIN employees m ON e.manager_id = m.emp_id;

-- USING (khi tên cột giống nhau)
SELECT emp_id, first_name, dept_name FROM employees JOIN departments USING (dept_id);

-- NATURAL JOIN (tự match tất cả cột trùng tên — ⚠️ nguy hiểm)
SELECT * FROM employees NATURAL JOIN departments;

-- Multi-table JOIN
SELECT e.first_name, d.dept_name, o.order_id, oi.quantity, p.product_name
FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
JOIN orders o ON e.emp_id = o.emp_id
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p ON oi.product_id = p.product_id
WHERE o.order_date >= DATE '2025-01-01';

-- LEFT JOIN tìm orphan records
SELECT d.dept_name FROM departments d
LEFT JOIN employees e ON d.dept_id = e.dept_id WHERE e.emp_id IS NULL;
-- → Phòng ban không có nhân viên

-- Oracle old-style (+) syntax
SELECT e.first_name, d.dept_name
FROM employees e, departments d WHERE e.dept_id = d.dept_id(+);  -- = LEFT JOIN
-- 🔥 Luôn dùng ANSI JOIN thay vì (+)

-- LATERAL (12c+) — inline view tham chiếu bảng bên ngoài
SELECT d.dept_name, lat.top_salary
FROM departments d,
LATERAL (
    SELECT MAX(e.salary) AS top_salary FROM employees e WHERE e.dept_id = d.dept_id
) lat;

-- CROSS APPLY / OUTER APPLY (tương đương LATERAL)
SELECT d.dept_name, e.first_name, e.salary
FROM departments d
OUTER APPLY (
    SELECT * FROM employees e WHERE e.dept_id = d.dept_id ORDER BY salary DESC FETCH FIRST 3 ROWS ONLY
) e;
```

---

## 10. Aggregate Functions

```sql
SELECT
    COUNT(*) AS total,                -- đếm tất cả (kể cả NULL)
    COUNT(commission_pct) AS has_comm, -- đếm non-NULL
    COUNT(DISTINCT dept_id) AS depts,
    SUM(salary) AS total_sal,
    AVG(salary) AS avg_sal,
    MEDIAN(salary) AS median_sal,     -- Oracle specific
    MIN(salary) AS min_sal,
    MAX(salary) AS max_sal,
    STDDEV(salary) AS std_dev,        -- độ lệch chuẩn
    VARIANCE(salary) AS variance,
    STATS_MODE(dept_id) AS most_common_dept  -- Oracle specific: giá trị xuất hiện nhiều nhất
FROM employees WHERE status = 'ACTIVE';
```

---

## 11. NVL, NVL2, COALESCE, NULLIF

```sql
-- NVL(expr, default) — thay NULL bằng default
SELECT NVL(commission_pct, 0) FROM employees;
SELECT salary + salary * NVL(commission_pct, 0) AS total_comp FROM employees;

-- NVL2(expr, if_not_null, if_null)
SELECT NVL2(manager_id, 'Có quản lý', 'CEO') FROM employees;
SELECT NVL2(commission_pct, salary + salary*commission_pct, salary) AS total FROM employees;

-- COALESCE(expr1, expr2, ...) — giá trị non-NULL đầu tiên (ANSI standard)
SELECT COALESCE(phone, email, 'N/A') AS contact FROM employees;
SELECT COALESCE(bonus, commission, overtime_pay, 0) AS extra_pay FROM payroll;

-- NULLIF(a, b) — trả NULL nếu a = b, trả a nếu khác
SELECT NULLIF(salary, 0) FROM employees;  -- tránh division by zero
SELECT total / NULLIF(quantity, 0) AS unit_price FROM order_items;

-- LNNVL(condition) — trả TRUE khi condition FALSE hoặc NULL
SELECT * FROM employees WHERE LNNVL(commission_pct > 0.1);
-- tương đương: WHERE commission_pct <= 0.1 OR commission_pct IS NULL

-- NANVL (cho BINARY_FLOAT/BINARY_DOUBLE)
SELECT NANVL(float_col, 0) FROM measurements;
```

---

## 12. CASE & DECODE

```sql
-- Searched CASE
SELECT first_name, salary,
    CASE
        WHEN salary >= 30000000 THEN 'Senior'
        WHEN salary >= 20000000 THEN 'Mid'
        WHEN salary >= 10000000 THEN 'Junior'
        ELSE 'Intern'
    END AS level
FROM employees;

-- Simple CASE
SELECT order_id, CASE status
    WHEN 'P' THEN 'Pending' WHEN 'C' THEN 'Completed'
    WHEN 'X' THEN 'Cancelled' ELSE 'Unknown'
END AS status_label FROM orders;

-- CASE trong aggregate (Pivot thủ công)
SELECT dept_id,
    COUNT(CASE WHEN status='ACTIVE' THEN 1 END) AS active,
    COUNT(CASE WHEN status='INACTIVE' THEN 1 END) AS inactive,
    SUM(CASE WHEN hire_date >= DATE '2025-01-01' THEN salary ELSE 0 END) AS new_hire_salary
FROM employees GROUP BY dept_id;

-- CASE trong UPDATE
UPDATE employees SET salary = CASE
    WHEN MONTHS_BETWEEN(SYSDATE, hire_date) > 60 THEN salary * 1.15
    WHEN MONTHS_BETWEEN(SYSDATE, hire_date) > 24 THEN salary * 1.10
    ELSE salary * 1.05 END;

-- CASE trong ORDER BY
SELECT * FROM orders ORDER BY CASE status
    WHEN 'URGENT' THEN 1 WHEN 'PENDING' THEN 2 WHEN 'DONE' THEN 3 ELSE 4 END;

-- DECODE — Oracle specific (tương đương simple CASE)
SELECT DECODE(dept_id, 10,'IT', 20,'HR', 30,'Finance', 'Other') FROM employees;
SELECT DECODE(SIGN(salary - 20000000), 1,'High', 0,'Mid', -1,'Low') FROM employees;
-- DECODE(NULL, NULL, 'YES', 'NO') → 'YES' (khác CASE: NULL = NULL là FALSE)
```

---

## 13. Set Operators

```sql
-- UNION (loại trùng) / UNION ALL (giữ trùng — nhanh hơn)
SELECT emp_id, first_name, 'ACTIVE' src FROM employees WHERE status = 'ACTIVE'
UNION ALL
SELECT emp_id, first_name, 'ARCHIVE' src FROM employees_archive;

-- INTERSECT — giao
SELECT emp_id FROM employees WHERE dept_id = 10
INTERSECT
SELECT emp_id FROM orders WHERE total > 5000000;

-- MINUS — hiệu (A - B)
SELECT emp_id FROM employees
MINUS
SELECT emp_id FROM orders;  -- nhân viên chưa có đơn hàng

-- Kết hợp
SELECT emp_id FROM employees WHERE dept_id = 10
UNION
SELECT emp_id FROM employees WHERE salary > 20000000
MINUS
SELECT emp_id FROM employees WHERE status = 'INACTIVE';
-- ⚠️ Thứ tự ưu tiên: INTERSECT > UNION = MINUS (trái sang phải). Dùng () để rõ ràng.
```

---

# PHẦN 2: WINDOW FUNCTIONS (Analytic)

Tính toán trên tập dòng liên quan mà KHÔNG gom nhóm — mỗi dòng vẫn giữ nguyên.

```sql
function(args) OVER (
    [PARTITION BY col]         -- chia nhóm
    [ORDER BY col]             -- sắp xếp trong nhóm
    [ROWS/RANGE BETWEEN ...]   -- khung cửa sổ
)
```

## ROW_NUMBER, RANK, DENSE_RANK

```sql
-- So sánh 3 hàm xếp hạng (salary: 30M, 25M, 25M, 20M)
-- ROW_NUMBER: 1, 2, 3, 4  (luôn duy nhất)
-- RANK:       1, 2, 2, 4  (bỏ số hạng khi hòa)
-- DENSE_RANK: 1, 2, 2, 3  (không bỏ số hạng)

SELECT emp_id, dept_id, salary,
    ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rn,
    RANK()       OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rnk,
    DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS drnk
FROM employees;

-- UC: Top 1 lương cao nhất mỗi phòng
SELECT * FROM (
    SELECT e.*, ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rn
    FROM employees e WHERE status = 'ACTIVE'
) WHERE rn = 1;

-- UC: Top 3 sản phẩm bán chạy mỗi danh mục
SELECT * FROM (
    SELECT p.category, p.product_name, SUM(oi.quantity) AS total_sold,
           DENSE_RANK() OVER (PARTITION BY p.category ORDER BY SUM(oi.quantity) DESC) AS dr
    FROM products p JOIN order_items oi ON p.product_id = oi.product_id
    GROUP BY p.category, p.product_name
) WHERE dr <= 3;

-- UC: Pagination
SELECT * FROM (
    SELECT e.*, ROW_NUMBER() OVER (ORDER BY emp_id) AS rn FROM employees e
) WHERE rn BETWEEN 21 AND 30;  -- page 3, size 10
```

## LEAD & LAG

```sql
-- LAG(col, n, default): n dòng TRƯỚC | LEAD: n dòng SAU
SELECT first_name, salary,
    LAG(salary)  OVER (ORDER BY salary) AS prev_salary,
    LEAD(salary) OVER (ORDER BY salary) AS next_salary,
    salary - LAG(salary) OVER (ORDER BY salary) AS gap
FROM employees;

-- UC: Month-over-Month revenue
SELECT month, revenue,
    LAG(revenue) OVER (ORDER BY month) AS prev_month,
    ROUND((revenue - LAG(revenue) OVER (ORDER BY month))
          / NULLIF(LAG(revenue) OVER (ORDER BY month), 0) * 100, 2) AS mom_pct
FROM (
    SELECT TO_CHAR(order_date,'YYYY-MM') AS month, SUM(total) AS revenue
    FROM orders GROUP BY TO_CHAR(order_date,'YYYY-MM')
) ORDER BY month;

-- UC: Tìm gap trong sequence
SELECT emp_id, next_id, next_id - emp_id AS gap FROM (
    SELECT emp_id, LEAD(emp_id) OVER (ORDER BY emp_id) AS next_id FROM employees
) WHERE next_id - emp_id > 1;

-- UC: Session duration (thời gian giữa các event)
SELECT event_id, event_time,
    LEAD(event_time) OVER (PARTITION BY user_id ORDER BY event_time) AS next_event,
    (LEAD(event_time) OVER (PARTITION BY user_id ORDER BY event_time) - event_time)
        * 24 * 60 AS minutes_to_next
FROM user_events;
```

## Running/Sliding Aggregates

```sql
-- Running total (tổng lũy kế)
SELECT order_date, total,
    SUM(total) OVER (ORDER BY order_date ROWS UNBOUNDED PRECEDING) AS running_total
FROM orders;

-- Moving average 7 ngày
SELECT order_date, total,
    ROUND(AVG(total) OVER (ORDER BY order_date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW), 2)
    AS ma_7d FROM orders;

-- Running count & percentage
SELECT dept_id, salary,
    COUNT(*) OVER (ORDER BY salary) AS running_count,
    ROUND(COUNT(*) OVER (ORDER BY salary) * 100.0 /
          COUNT(*) OVER (), 2) AS running_pct
FROM employees;
```

## Các hàm khác

```sql
-- FIRST_VALUE / LAST_VALUE
SELECT first_name, dept_id, salary,
    FIRST_VALUE(first_name) OVER (PARTITION BY dept_id ORDER BY salary DESC) AS top_earner,
    LAST_VALUE(first_name)  OVER (PARTITION BY dept_id ORDER BY salary DESC
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS low_earner
FROM employees;

-- NTH_VALUE
SELECT first_name, salary,
    NTH_VALUE(first_name, 2) OVER (PARTITION BY dept_id ORDER BY salary DESC
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS second_highest
FROM employees;

-- NTILE — chia N nhóm bằng nhau
SELECT first_name, salary, NTILE(4) OVER (ORDER BY salary DESC) AS quartile FROM employees;

-- PERCENT_RANK & CUME_DIST
SELECT first_name, salary,
    ROUND(PERCENT_RANK() OVER (ORDER BY salary) * 100, 2) AS percentile,
    ROUND(CUME_DIST() OVER (ORDER BY salary) * 100, 2) AS cumulative_pct
FROM employees;

-- RATIO_TO_REPORT — tỉ lệ % so với tổng
SELECT dept_id, salary,
    ROUND(RATIO_TO_REPORT(salary) OVER (PARTITION BY dept_id) * 100, 2) AS pct_of_dept
FROM employees;

-- Window frames
ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW      -- từ đầu đến hiện tại
ROWS BETWEEN 3 PRECEDING AND 3 FOLLOWING               -- 3 trước + hiện tại + 3 sau
RANGE BETWEEN INTERVAL '7' DAY PRECEDING AND CURRENT ROW  -- 7 ngày gần nhất
```

---

# PHẦN 3: DDL — Data Definition Language

> ⚠️ Tất cả DDL tự động COMMIT. Không thể ROLLBACK.

## CREATE TABLE

```sql
-- Đầy đủ options
CREATE TABLE customers (
    id            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_name VARCHAR2(200) NOT NULL,
    email         VARCHAR2(100) CONSTRAINT uq_cust_email UNIQUE,
    phone         VARCHAR2(20),
    tier          VARCHAR2(20) DEFAULT 'STANDARD' CONSTRAINT chk_tier CHECK (tier IN ('VIP','GOLD','STANDARD')),
    credit_limit  NUMBER(12,2) DEFAULT 0 CHECK (credit_limit >= 0),
    is_active     NUMBER(1) DEFAULT 1,
    created_at    TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    updated_at    TIMESTAMP,
    notes         CLOB,
    avatar        BLOB
) TABLESPACE ts_app_data;

-- Virtual Column (11g+)
CREATE TABLE emp_ext (
    emp_id NUMBER PRIMARY KEY, salary NUMBER, bonus_pct NUMBER,
    total_comp NUMBER GENERATED ALWAYS AS (salary + salary * NVL(bonus_pct,0)) VIRTUAL
);

-- CTAS (Create Table As Select) — copy cấu trúc + dữ liệu
CREATE TABLE emp_backup AS SELECT * FROM employees WHERE status = 'ACTIVE';

-- CTAS chỉ copy cấu trúc (không data)
CREATE TABLE emp_template AS SELECT * FROM employees WHERE 1 = 0;

-- Global Temporary Table
CREATE GLOBAL TEMPORARY TABLE tmp_report (
    dept_id NUMBER, total NUMBER
) ON COMMIT DELETE ROWS;         -- xóa khi commit
-- ON COMMIT PRESERVE ROWS;     -- giữ đến hết session

-- Private Temporary Table (18c+) — chỉ visible trong session, tự xóa
CREATE PRIVATE TEMPORARY TABLE ORA$PTT_my_temp (
    id NUMBER, val VARCHAR2(100)
) ON COMMIT DROP DEFINITION;     -- drop khi commit

-- External Table — đọc file CSV/text bên ngoài
CREATE TABLE ext_csv_data (
    col1 VARCHAR2(100), col2 NUMBER, col3 DATE
) ORGANIZATION EXTERNAL (
    TYPE ORACLE_LOADER
    DEFAULT DIRECTORY ext_dir
    ACCESS PARAMETERS (
        RECORDS DELIMITED BY NEWLINE
        FIELDS TERMINATED BY ','
        OPTIONALLY ENCLOSED BY '"'
        MISSING FIELD VALUES ARE NULL
    )
    LOCATION ('data.csv')
) REJECT LIMIT UNLIMITED;
```

## CREATE VIEW / MATERIALIZED VIEW

```sql
-- View cơ bản
CREATE OR REPLACE VIEW v_active_emp AS
SELECT emp_id, first_name, last_name, dept_id, hire_date
FROM employees WHERE status = 'ACTIVE'
WITH CHECK OPTION;    -- ngăn INSERT/UPDATE vi phạm WHERE

-- View read-only
CREATE OR REPLACE VIEW v_salary_report AS
SELECT dept_id, AVG(salary) avg_sal, COUNT(*) cnt FROM employees
GROUP BY dept_id WITH READ ONLY;

-- Force View (tạo dù bảng base chưa tồn tại)
CREATE OR REPLACE FORCE VIEW v_future_table AS SELECT * FROM upcoming_table;

-- Materialized View
CREATE MATERIALIZED VIEW mv_sales_summary
BUILD IMMEDIATE REFRESH COMPLETE ON DEMAND
ENABLE QUERY REWRITE   -- optimizer tự dùng MView khi phù hợp
AS
SELECT TO_CHAR(order_date,'YYYY-MM') AS month, p.category,
       COUNT(*) AS cnt, SUM(oi.quantity * oi.unit_price) AS revenue
FROM orders o JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p ON oi.product_id = p.product_id
GROUP BY TO_CHAR(order_date,'YYYY-MM'), p.category;

-- Fast Refresh MView (cần MView Log)
CREATE MATERIALIZED VIEW LOG ON orders WITH ROWID, PRIMARY KEY INCLUDING NEW VALUES;
CREATE MATERIALIZED VIEW mv_fast REFRESH FAST ON COMMIT AS SELECT ...;

-- Refresh
BEGIN DBMS_MVIEW.REFRESH('MV_SALES_SUMMARY', 'C'); END; /  -- C=Complete, F=Fast
```

## INDEX

```sql
-- B-Tree (mặc định)
CREATE INDEX idx_emp_name ON employees(last_name, first_name);

-- Unique Index
CREATE UNIQUE INDEX idx_emp_email ON employees(email);

-- Function-based Index
CREATE INDEX idx_emp_upper ON employees(UPPER(last_name));
CREATE INDEX idx_ord_trunc ON orders(TRUNC(order_date));
-- Giờ query: WHERE UPPER(last_name) = 'NGUYEN' → dùng index

-- Bitmap Index (chỉ cho OLAP/DW — ít giá trị distinct)
CREATE BITMAP INDEX idx_emp_status_bmp ON employees(status);

-- Reverse Key Index (tránh contention trên sequence PK)
CREATE INDEX idx_ord_rev ON orders(order_id) REVERSE;

-- Invisible Index (test trước khi enable)
CREATE INDEX idx_test ON employees(salary) INVISIBLE;
ALTER INDEX idx_test VISIBLE;

-- Compressed Index
CREATE INDEX idx_comp ON employees(dept_id, status) COMPRESS 1;

-- Partial Index trên partitioned table (12c+)
CREATE INDEX idx_partial ON orders(customer_id) LOCAL INDEXING PARTIAL;

-- Rebuild
ALTER INDEX idx_emp_name REBUILD ONLINE;

-- Monitor usage
ALTER INDEX idx_emp_name MONITORING USAGE;
SELECT * FROM v$object_usage WHERE index_name = 'IDX_EMP_NAME';
```

## SEQUENCE

```sql
CREATE SEQUENCE seq_emp START WITH 1000 INCREMENT BY 1
    MINVALUE 1 MAXVALUE 9999999 CACHE 20 NOCYCLE;

INSERT INTO employees (emp_id, ...) VALUES (seq_emp.NEXTVAL, ...);
SELECT seq_emp.CURRVAL FROM dual;

-- Identity Column (12c+) — thay sequence thủ công
CREATE TABLE t (id NUMBER GENERATED ALWAYS AS IDENTITY, name VARCHAR2(100));
CREATE TABLE t (id NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY, name VARCHAR2(100));
```

## ALTER / DROP / TRUNCATE / RENAME / COMMENT

```sql
-- ALTER TABLE
ALTER TABLE employees ADD (middle_name VARCHAR2(50), birth_date DATE);
ALTER TABLE employees MODIFY (email VARCHAR2(200) NOT NULL);
ALTER TABLE employees RENAME COLUMN phone TO mobile;
ALTER TABLE employees DROP COLUMN middle_name;
ALTER TABLE employees SET UNUSED COLUMN address;  -- fast logical drop
ALTER TABLE employees DROP UNUSED COLUMNS;         -- physical drop later
ALTER TABLE employees ADD CONSTRAINT chk_sal CHECK (salary > 0);
ALTER TABLE employees DROP CONSTRAINT chk_sal;
ALTER TABLE employees ENABLE CONSTRAINT fk_dept;
ALTER TABLE employees DISABLE CONSTRAINT fk_dept;
ALTER TABLE employees MOVE TABLESPACE ts_new;      -- di chuyển tablespace
ALTER TABLE employees SHRINK SPACE CASCADE;        -- thu hồi dung lượng
ALTER TABLE employees ENABLE ROW MOVEMENT;         -- cần cho flashback

-- DROP
DROP TABLE employees CASCADE CONSTRAINTS PURGE;  -- không vào recycle bin
DROP VIEW v_active_emp;
DROP INDEX idx_emp_name;
DROP SEQUENCE seq_emp;
DROP MATERIALIZED VIEW mv_sales_summary;
DROP SYNONYM emp;
DROP PUBLIC SYNONYM departments;

-- TRUNCATE — xóa toàn bộ data (DDL, không rollback, không trigger)
TRUNCATE TABLE tmp_data DROP STORAGE;     -- giải phóng space
TRUNCATE TABLE tmp_data REUSE STORAGE;    -- giữ space để tái dùng
TRUNCATE TABLE parent CASCADE;            -- 12c+ truncate FK references

-- RENAME
RENAME old_table TO new_table;

-- COMMENT
COMMENT ON TABLE employees IS 'Bảng nhân viên chính';
COMMENT ON COLUMN employees.salary IS 'Lương tháng VNĐ, không gồm thưởng';
SELECT table_name, comments FROM user_tab_comments;
SELECT column_name, comments FROM user_col_comments WHERE table_name = 'EMPLOYEES';
```

## Constraints

```sql
-- PRIMARY KEY, FOREIGN KEY, UNIQUE, CHECK, NOT NULL
CREATE TABLE orders_v2 (
    order_id   NUMBER CONSTRAINT pk_ord PRIMARY KEY,
    emp_id     NUMBER CONSTRAINT fk_ord_emp REFERENCES employees(emp_id) ON DELETE SET NULL,
    total      NUMBER CONSTRAINT chk_total CHECK (total >= 0),
    status     VARCHAR2(20) DEFAULT 'NEW' CONSTRAINT chk_status CHECK (status IN ('NEW','PROCESSING','DONE','CANCELLED')),
    email      VARCHAR2(100) CONSTRAINT uq_ord_email UNIQUE,
    notes      VARCHAR2(4000) CONSTRAINT nn_notes NOT NULL
);

-- Composite PK + FK
CREATE TABLE order_items_v2 (
    order_id NUMBER, product_id NUMBER, qty NUMBER NOT NULL,
    CONSTRAINT pk_oi PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_oi_ord FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- Deferred constraint (check at commit instead of statement)
ALTER TABLE employees ADD CONSTRAINT fk_mgr FOREIGN KEY (manager_id)
    REFERENCES employees(emp_id) DEFERRABLE INITIALLY DEFERRED;
SET CONSTRAINTS fk_mgr IMMEDIATE;  -- force check now

-- View constraints
SELECT constraint_name, constraint_type, search_condition, status, validated
FROM user_constraints WHERE table_name = 'EMPLOYEES';
```

---

# PHẦN 4: TRANSACTION & SECURITY

## COMMIT / ROLLBACK / SAVEPOINT

```sql
-- COMMIT
INSERT INTO employees (...) VALUES (...);
UPDATE departments SET manager_id = 1001 WHERE dept_id = 10;
COMMIT;
COMMIT COMMENT 'ticket #1234';
COMMIT WRITE BATCH NOWAIT;      -- nhanh nhất
COMMIT WRITE IMMEDIATE WAIT;    -- an toàn nhất (mặc định)

-- ROLLBACK
DELETE FROM employees WHERE dept_id = 99;
ROLLBACK;  -- hủy DELETE

-- SAVEPOINT
SAVEPOINT sp1;
UPDATE employees SET salary = salary * 1.1 WHERE dept_id = 10;
SAVEPOINT sp2;
UPDATE employees SET salary = salary * 1.2 WHERE dept_id = 20;
ROLLBACK TO sp2;  -- hủy dept 20, giữ dept 10
COMMIT;

-- SET TRANSACTION
SET TRANSACTION READ ONLY;
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
SET TRANSACTION NAME 'monthly_payroll';
```

## LOCK

```sql
-- Table lock
LOCK TABLE employees IN EXCLUSIVE MODE NOWAIT;
LOCK TABLE employees IN SHARE MODE WAIT 10;

-- Row lock — SELECT FOR UPDATE
SELECT emp_id, salary FROM employees WHERE dept_id = 10 FOR UPDATE;
SELECT * FROM employees WHERE emp_id = 1001 FOR UPDATE NOWAIT;
SELECT * FROM employees WHERE emp_id = 1001 FOR UPDATE WAIT 5;

-- FOR UPDATE OF (chỉ lock bảng cụ thể)
SELECT e.first_name, d.dept_name FROM employees e
JOIN departments d ON e.dept_id = d.dept_id FOR UPDATE OF e.salary;

-- SKIP LOCKED — queue processing pattern (11g+)
SELECT * FROM task_queue WHERE status = 'PENDING'
FOR UPDATE SKIP LOCKED FETCH FIRST 10 ROWS ONLY;
```

## USER / ROLE / PROFILE / GRANT / REVOKE

```sql
-- USER
CREATE USER app_user IDENTIFIED BY "SecureP@ss123"
    DEFAULT TABLESPACE users TEMPORARY TABLESPACE temp
    QUOTA 500M ON users ACCOUNT UNLOCK;
ALTER USER app_user IDENTIFIED BY "NewP@ss" QUOTA UNLIMITED ON users;
ALTER USER app_user ACCOUNT LOCK;
DROP USER app_user CASCADE;

-- ROLE
CREATE ROLE role_readonly;
CREATE ROLE role_readwrite;
GRANT role_readonly TO role_readwrite;          -- kế thừa

-- PROFILE
CREATE PROFILE app_profile LIMIT
    PASSWORD_LIFE_TIME 90 FAILED_LOGIN_ATTEMPTS 5
    PASSWORD_LOCK_TIME 1/24 IDLE_TIME 30
    SESSIONS_PER_USER 10;
ALTER USER app_user PROFILE app_profile;

-- GRANT
GRANT CREATE SESSION TO app_user;
GRANT SELECT, INSERT, UPDATE ON hr.employees TO app_user;
GRANT UPDATE (salary) ON hr.employees TO hr_manager;    -- column-level
GRANT SELECT ON hr.employees TO app_user WITH GRANT OPTION;
GRANT ALL ON hr.employees TO app_admin;
GRANT role_readonly TO app_user;
GRANT EXECUTE ON pkg_employee_mgmt TO app_user;

-- REVOKE
REVOKE SELECT ON hr.employees FROM app_user;
REVOKE role_readwrite FROM app_user;
-- ⚠️ Object privilege + GRANT OPTION → REVOKE cascade
-- ⚠️ System privilege + ADMIN OPTION → REVOKE NOT cascade
```

---

# PHẦN 5: PL/SQL

## Block Structure

```sql
DECLARE
    -- Biến
    v_name       VARCHAR2(100) := 'default';
    v_salary     NUMBER(10,2);
    v_is_active  BOOLEAN := TRUE;
    c_tax        CONSTANT NUMBER := 0.1;
    
    -- %TYPE / %ROWTYPE
    v_emp_sal    employees.salary%TYPE;
    v_emp_row    employees%ROWTYPE;
    
    -- Record type
    TYPE t_summary IS RECORD (id NUMBER, name VARCHAR2(100), total NUMBER);
    v_sum t_summary;
    
    -- Collection types
    TYPE t_num_list IS TABLE OF NUMBER INDEX BY PLS_INTEGER;  -- Associative array
    TYPE t_name_tab IS TABLE OF VARCHAR2(100);                -- Nested table
    TYPE t_id_arr IS VARRAY(100) OF NUMBER;                   -- VARRAY
    v_ids t_num_list;
    v_names t_name_tab := t_name_tab();
BEGIN
    SELECT * INTO v_emp_row FROM employees WHERE emp_id = 1001;
    DBMS_OUTPUT.PUT_LINE(v_emp_row.first_name || ': ' || v_emp_row.salary);
EXCEPTION
    WHEN NO_DATA_FOUND THEN DBMS_OUTPUT.PUT_LINE('Not found');
    WHEN TOO_MANY_ROWS THEN DBMS_OUTPUT.PUT_LINE('Multiple rows');
    WHEN OTHERS THEN DBMS_OUTPUT.PUT_LINE('Error: ' || SQLCODE || ' - ' || SQLERRM);
END;
/
```

## IF / CASE

```sql
-- IF
IF v_salary >= 30000000 THEN v_level := 'SENIOR';
ELSIF v_salary >= 20000000 THEN v_level := 'MID';
ELSIF v_salary >= 10000000 THEN v_level := 'JUNIOR';
ELSE v_level := 'INTERN';
END IF;

-- CASE statement (khác CASE expression)
CASE v_dept_id
    WHEN 10 THEN process_it();
    WHEN 20 THEN process_hr();
    ELSE process_other();
END CASE;
```

## LOOP / FOR / WHILE

```sql
-- Basic LOOP
LOOP
    v_counter := v_counter + 1;
    EXIT WHEN v_counter > 10;
    CONTINUE WHEN MOD(v_counter, 2) = 0;  -- skip even
    DBMS_OUTPUT.PUT_LINE(v_counter);
END LOOP;

-- FOR loop (numeric)
FOR i IN 1..10 LOOP DBMS_OUTPUT.PUT_LINE(i); END LOOP;
FOR i IN REVERSE 1..10 LOOP DBMS_OUTPUT.PUT_LINE(i); END LOOP;

-- FOR loop (cursor — phổ biến nhất)
FOR rec IN (SELECT emp_id, first_name, salary FROM employees WHERE dept_id = 10) LOOP
    DBMS_OUTPUT.PUT_LINE(rec.first_name || ': ' || rec.salary);
    IF rec.salary < 15000000 THEN
        UPDATE employees SET salary = salary * 1.1 WHERE emp_id = rec.emp_id;
    END IF;
END LOOP;

-- WHILE loop
WHILE v_month <= 12 LOOP
    -- process month
    v_month := v_month + 1;
END LOOP;
```

## Cursor (Explicit)

```sql
DECLARE
    CURSOR c_emp (p_dept NUMBER) IS
        SELECT emp_id, first_name, salary FROM employees
        WHERE dept_id = p_dept AND status = 'ACTIVE' ORDER BY salary DESC;
    v_rec c_emp%ROWTYPE;
BEGIN
    OPEN c_emp(10);
    LOOP
        FETCH c_emp INTO v_rec;
        EXIT WHEN c_emp%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE(v_rec.first_name || ': ' || v_rec.salary);
    END LOOP;
    DBMS_OUTPUT.PUT_LINE('Total: ' || c_emp%ROWCOUNT);
    CLOSE c_emp;
END;
/

-- Cursor attributes: %FOUND, %NOTFOUND, %ROWCOUNT, %ISOPEN

-- Ref Cursor (dynamic cursor)
DECLARE
    TYPE t_ref IS REF CURSOR;
    v_cur t_ref;
    v_name VARCHAR2(100); v_sal NUMBER;
BEGIN
    OPEN v_cur FOR 'SELECT first_name, salary FROM employees WHERE dept_id = :d' USING 10;
    LOOP
        FETCH v_cur INTO v_name, v_sal;
        EXIT WHEN v_cur%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE(v_name || ': ' || v_sal);
    END LOOP;
    CLOSE v_cur;
END;
/

-- SYS_REFCURSOR (predefined weak ref cursor)
CREATE OR REPLACE FUNCTION fn_get_emps(p_dept NUMBER) RETURN SYS_REFCURSOR IS
    v_cur SYS_REFCURSOR;
BEGIN
    OPEN v_cur FOR SELECT * FROM employees WHERE dept_id = p_dept;
    RETURN v_cur;
END;
/
```

## BULK COLLECT & FORALL

```sql
DECLARE
    TYPE t_emp_tab IS TABLE OF employees%ROWTYPE;
    v_emps t_emp_tab;
BEGIN
    -- BULK COLLECT — fetch nhiều dòng 1 lần (10-50x nhanh hơn loop)
    SELECT * BULK COLLECT INTO v_emps FROM employees WHERE dept_id = 10;
    DBMS_OUTPUT.PUT_LINE('Fetched: ' || v_emps.COUNT);

    -- BULK COLLECT với LIMIT (tránh hết memory)
    DECLARE
        CURSOR c IS SELECT * FROM employees;
        v_batch t_emp_tab;
    BEGIN
        OPEN c;
        LOOP
            FETCH c BULK COLLECT INTO v_batch LIMIT 1000;
            EXIT WHEN v_batch.COUNT = 0;
            
            FORALL i IN 1..v_batch.COUNT
                UPDATE employees SET salary = v_batch(i).salary * 1.05
                WHERE emp_id = v_batch(i).emp_id;
            COMMIT;
        END LOOP;
        CLOSE c;
    END;

    -- FORALL với SAVE EXCEPTIONS (tiếp tục dù có lỗi)
    BEGIN
        FORALL i IN 1..v_emps.COUNT SAVE EXCEPTIONS
            INSERT INTO emp_archive VALUES v_emps(i);
    EXCEPTION
        WHEN OTHERS THEN
            FOR j IN 1..SQL%BULK_EXCEPTIONS.COUNT LOOP
                DBMS_OUTPUT.PUT_LINE('Error at index ' || SQL%BULK_EXCEPTIONS(j).ERROR_INDEX
                    || ': ' || SQLERRM(-SQL%BULK_EXCEPTIONS(j).ERROR_CODE));
            END LOOP;
    END;
END;
/
```

## PROCEDURE

```sql
CREATE OR REPLACE PROCEDURE proc_adjust_salary (
    p_emp_id  IN  NUMBER,
    p_pct     IN  NUMBER DEFAULT 10,
    p_new_sal OUT NUMBER
) AS
    v_current NUMBER;
BEGIN
    SELECT salary INTO v_current FROM employees WHERE emp_id = p_emp_id FOR UPDATE;
    p_new_sal := ROUND(v_current * (1 + p_pct/100), 2);
    UPDATE employees SET salary = p_new_sal WHERE emp_id = p_emp_id;
    DBMS_OUTPUT.PUT_LINE(v_current || ' → ' || p_new_sal);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20001, 'Employee not found: ' || p_emp_id);
END;
/

-- Gọi
DECLARE v_sal NUMBER;
BEGIN
    proc_adjust_salary(p_emp_id => 1001, p_pct => 15, p_new_sal => v_sal);
    COMMIT;
END;
/
```

## FUNCTION

```sql
CREATE OR REPLACE FUNCTION fn_annual_salary(p_emp_id NUMBER) RETURN NUMBER
DETERMINISTIC   -- cache kết quả cho cùng input
AS
    v_sal NUMBER;
BEGIN
    SELECT salary * 12 + salary * 12 * NVL(commission_pct, 0) INTO v_sal
    FROM employees WHERE emp_id = p_emp_id;
    RETURN v_sal;
EXCEPTION WHEN NO_DATA_FOUND THEN RETURN NULL;
END;
/
-- Dùng trong SQL: SELECT fn_annual_salary(emp_id) FROM employees;

-- Pipelined Table Function
CREATE OR REPLACE TYPE t_row AS OBJECT (dept_name VARCHAR2(100), cnt NUMBER, avg_sal NUMBER);
CREATE OR REPLACE TYPE t_tab AS TABLE OF t_row;

CREATE OR REPLACE FUNCTION fn_dept_report RETURN t_tab PIPELINED AS
BEGIN
    FOR rec IN (SELECT d.dept_name, COUNT(*) cnt, ROUND(AVG(e.salary)) avg_sal
                FROM departments d LEFT JOIN employees e ON d.dept_id = e.dept_id
                GROUP BY d.dept_name) LOOP
        PIPE ROW (t_row(rec.dept_name, rec.cnt, rec.avg_sal));
    END LOOP;
    RETURN;
END;
/
SELECT * FROM TABLE(fn_dept_report()) ORDER BY avg_sal DESC;
```

## PACKAGE

```sql
-- Spec (public interface)
CREATE OR REPLACE PACKAGE pkg_emp AS
    c_max_salary CONSTANT NUMBER := 100000000;
    PROCEDURE hire(p_first VARCHAR2, p_last VARCHAR2, p_email VARCHAR2,
                   p_salary NUMBER, p_dept NUMBER, p_id OUT NUMBER);
    PROCEDURE terminate(p_emp_id NUMBER);
    FUNCTION get_headcount(p_dept_id NUMBER) RETURN NUMBER;
END pkg_emp;
/

-- Body (implementation)
CREATE OR REPLACE PACKAGE BODY pkg_emp AS
    -- Private helper (không visible bên ngoài)
    PROCEDURE log_action(p_action VARCHAR2, p_emp_id NUMBER) IS
        PRAGMA AUTONOMOUS_TRANSACTION;
    BEGIN
        INSERT INTO audit_log VALUES (seq_log.NEXTVAL, p_action, SYSDATE, p_emp_id, USER);
        COMMIT;
    END;

    PROCEDURE hire(p_first VARCHAR2, p_last VARCHAR2, p_email VARCHAR2,
                   p_salary NUMBER, p_dept NUMBER, p_id OUT NUMBER) IS
    BEGIN
        IF p_salary > c_max_salary THEN
            RAISE_APPLICATION_ERROR(-20010, 'Salary exceeds limit');
        END IF;
        p_id := seq_emp.NEXTVAL;
        INSERT INTO employees (emp_id, first_name, last_name, email, salary, dept_id)
        VALUES (p_id, p_first, p_last, p_email, p_salary, p_dept);
        log_action('HIRE', p_id);
    END;

    PROCEDURE terminate(p_emp_id NUMBER) IS
    BEGIN
        UPDATE employees SET status = 'TERMINATED' WHERE emp_id = p_emp_id;
        IF SQL%ROWCOUNT = 0 THEN RAISE_APPLICATION_ERROR(-20011, 'Not found'); END IF;
        log_action('TERMINATE', p_emp_id);
    END;

    FUNCTION get_headcount(p_dept_id NUMBER) RETURN NUMBER IS
        v NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v FROM employees WHERE dept_id = p_dept_id AND status = 'ACTIVE';
        RETURN v;
    END;
END pkg_emp;
/

-- Sử dụng
DECLARE v_id NUMBER;
BEGIN
    pkg_emp.hire('Nguyen','Van A','nva@co.com', 15000000, 10, v_id);
    COMMIT;
END;
/
```

## TRIGGER

```sql
-- BEFORE trigger — validation + auto-fill
CREATE OR REPLACE TRIGGER trg_emp_before
BEFORE INSERT OR UPDATE ON employees FOR EACH ROW
BEGIN
    :NEW.email := LOWER(:NEW.email);
    :NEW.updated_at := SYSTIMESTAMP;
    IF :NEW.salary < 0 THEN RAISE_APPLICATION_ERROR(-20020, 'Salary < 0'); END IF;
    IF INSERTING THEN :NEW.created_at := SYSTIMESTAMP; END IF;
END;
/

-- AFTER trigger — audit log
CREATE OR REPLACE TRIGGER trg_salary_audit
AFTER UPDATE OF salary ON employees FOR EACH ROW
BEGIN
    INSERT INTO salary_audit (emp_id, old_sal, new_sal, changed_by, changed_at)
    VALUES (:OLD.emp_id, :OLD.salary, :NEW.salary, USER, SYSTIMESTAMP);
END;
/

-- INSTEAD OF trigger — DML trên VIEW
CREATE OR REPLACE TRIGGER trg_view_insert
INSTEAD OF INSERT ON v_active_emp FOR EACH ROW
BEGIN
    INSERT INTO employees (emp_id, first_name, last_name, dept_id, status)
    VALUES (seq_emp.NEXTVAL, :NEW.first_name, :NEW.last_name, :NEW.dept_id, 'ACTIVE');
END;
/

-- Compound Trigger — tránh mutating table error
CREATE OR REPLACE TRIGGER trg_compound
FOR UPDATE OF salary ON employees COMPOUND TRIGGER
    TYPE t_ids IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
    v_depts t_ids; v_idx PLS_INTEGER := 0;
    BEFORE EACH ROW IS BEGIN
        IF :NEW.salary > :OLD.salary * 1.5 THEN
            RAISE_APPLICATION_ERROR(-20030, 'Increase > 50%');
        END IF;
        v_idx := v_idx + 1; v_depts(v_idx) := :NEW.dept_id;
    END BEFORE EACH ROW;
    AFTER STATEMENT IS BEGIN
        FOR i IN 1..v_idx LOOP
            -- safe to query employees here
            NULL;
        END LOOP;
    END AFTER STATEMENT;
END trg_compound;
/

-- DDL Trigger
CREATE OR REPLACE TRIGGER trg_ddl_audit AFTER DDL ON SCHEMA
BEGIN
    INSERT INTO ddl_log VALUES (SYSTIMESTAMP, USER, ORA_DICT_OBJ_TYPE, ORA_DICT_OBJ_NAME, ORA_SYSEVENT);
END;
/

-- Logon Trigger
CREATE OR REPLACE TRIGGER trg_logon AFTER LOGON ON DATABASE
BEGIN
    INSERT INTO login_log VALUES (USER, SYSTIMESTAMP, SYS_CONTEXT('USERENV','IP_ADDRESS'));
    COMMIT;
END;
/

-- Quản lý
ALTER TRIGGER trg_salary_audit DISABLE;
ALTER TRIGGER trg_salary_audit ENABLE;
DROP TRIGGER trg_salary_audit;
```

## Exception Handling

```sql
DECLARE
    e_salary_too_high EXCEPTION;                          -- user-defined
    PRAGMA EXCEPTION_INIT(e_salary_too_high, -20099);     -- associate with error code
BEGIN
    IF v_salary > 100000000 THEN
        RAISE e_salary_too_high;
    END IF;

    -- RAISE_APPLICATION_ERROR: -20000 to -20999
    RAISE_APPLICATION_ERROR(-20001, 'Custom error message', TRUE);  -- TRUE = keep error stack

EXCEPTION
    WHEN NO_DATA_FOUND THEN NULL;
    WHEN DUP_VAL_ON_INDEX THEN DBMS_OUTPUT.PUT_LINE('Duplicate');
    WHEN e_salary_too_high THEN DBMS_OUTPUT.PUT_LINE('Too high');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Code: ' || SQLCODE);
        DBMS_OUTPUT.PUT_LINE('Msg: '  || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('Stack: '|| DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
        RAISE;  -- re-raise
END;
/
```

## Dynamic SQL

```sql
-- EXECUTE IMMEDIATE
DECLARE
    v_sql VARCHAR2(4000);
    v_count NUMBER;
BEGIN
    -- DDL
    EXECUTE IMMEDIATE 'CREATE TABLE tmp_test (id NUMBER)';
    
    -- DML với bind variable
    EXECUTE IMMEDIATE 'UPDATE employees SET salary = salary * :pct WHERE dept_id = :dept'
        USING 1.1, 10;
    
    -- Query INTO
    EXECUTE IMMEDIATE 'SELECT COUNT(*) FROM employees WHERE dept_id = :d'
        INTO v_count USING 10;
    
    -- RETURNING
    EXECUTE IMMEDIATE 'DELETE FROM employees WHERE emp_id = :id RETURNING first_name INTO :n'
        USING 1001 RETURNING INTO v_name;
END;
/

-- DBMS_SQL — cho dynamic SQL phức tạp hơn
-- Dùng khi số cột/bind variables không biết trước compile time
```

---

# PHẦN 6: PERFORMANCE

## Hints

```sql
SELECT /*+ FULL(e) */ * FROM employees e;                       -- force Full Table Scan
SELECT /*+ INDEX(e idx_emp_dept) */ * FROM employees e;         -- force dùng index
SELECT /*+ NO_INDEX(e) */ * FROM employees e;                   -- cấm index
SELECT /*+ PARALLEL(e, 8) */ * FROM employees e;                -- parallel query
SELECT /*+ FIRST_ROWS(10) */ * FROM employees e ORDER BY salary DESC;  -- tối ưu cho top N

-- Join hints
SELECT /*+ USE_NL(e d) */ ...   -- Nested Loop (bảng nhỏ + index)
SELECT /*+ USE_HASH(e d) */ ... -- Hash Join (bảng lớn, không index)
SELECT /*+ USE_MERGE(e d) */ ...-- Sort-Merge Join
SELECT /*+ LEADING(d e o) */ ...-- thứ tự join
SELECT /*+ ORDERED */ ...       -- join theo thứ tự FROM

-- DML hints
INSERT /*+ APPEND */ INTO archive SELECT * FROM source;         -- direct-path
INSERT /*+ APPEND PARALLEL(4) */ INTO archive SELECT ...;       -- parallel DML

-- Kết hợp
SELECT /*+ LEADING(d e) USE_HASH(e) PARALLEL(4) */ e.*, d.dept_name
FROM departments d JOIN employees e ON d.dept_id = e.dept_id;
```

## EXPLAIN PLAN & DBMS_XPLAN

```sql
-- Xem execution plan
EXPLAIN PLAN FOR
SELECT e.first_name, d.dept_name, SUM(o.total) FROM employees e
JOIN departments d ON e.dept_id = d.dept_id
JOIN orders o ON e.emp_id = o.emp_id
WHERE o.order_date >= DATE '2025-01-01' GROUP BY e.first_name, d.dept_name;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY());                          -- basic
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY(format => 'ALL'));            -- full detail
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR(format => 'ALLSTATS LAST'));  -- actual stats

-- Đọc plan: vào trong → ra ngoài, dưới → trên
-- Operations quan trọng:
-- TABLE ACCESS FULL      → full scan (⚠️ nếu bảng lớn)
-- INDEX RANGE SCAN       → tốt ✅
-- INDEX UNIQUE SCAN      → rất tốt ✅
-- INDEX FULL SCAN        → ⚠️ kiểm tra
-- HASH JOIN              → tốt cho bảng lớn ✅
-- NESTED LOOPS           → tốt khi bảng ngoài nhỏ ✅
-- SORT MERGE JOIN        → ⚠️ tốn memory

-- Thu thập statistics (CỰC KỲ QUAN TRỌNG)
BEGIN
    DBMS_STATS.GATHER_TABLE_STATS('HR', 'EMPLOYEES',
        estimate_percent => DBMS_STATS.AUTO_SAMPLE_SIZE,
        method_opt => 'FOR ALL COLUMNS SIZE AUTO', cascade => TRUE);
END;
/
BEGIN DBMS_STATS.GATHER_SCHEMA_STATS('HR'); END;
/
```

---

# PHẦN 7: PARTITION

```sql
-- RANGE Partition (theo ngày, số)
CREATE TABLE orders_part (
    order_id NUMBER, order_date DATE, total NUMBER, status VARCHAR2(20)
) PARTITION BY RANGE (order_date) (
    PARTITION p_2024 VALUES LESS THAN (DATE '2025-01-01'),
    PARTITION p_2025 VALUES LESS THAN (DATE '2026-01-01'),
    PARTITION p_future VALUES LESS THAN (MAXVALUE)
);

-- INTERVAL Partition (tự động tạo partition mới)
CREATE TABLE audit_log (
    log_id NUMBER, action VARCHAR2(50), log_date DATE
) PARTITION BY RANGE (log_date)
INTERVAL (NUMTOYMINTERVAL(1, 'MONTH'))
(PARTITION p_init VALUES LESS THAN (DATE '2025-01-01'));

-- HASH Partition (phân bổ đều)
CREATE TABLE txn (id NUMBER, acct NUMBER, amt NUMBER)
PARTITION BY HASH (acct) PARTITIONS 8;

-- LIST Partition (danh sách giá trị)
CREATE TABLE cust_region (id NUMBER, name VARCHAR2(200), region VARCHAR2(20))
PARTITION BY LIST (region) (
    PARTITION p_north VALUES ('HA_NOI','HAI_PHONG'),
    PARTITION p_south VALUES ('HCM','CAN_THO'),
    PARTITION p_other VALUES (DEFAULT)
);

-- Composite: Range-Hash
CREATE TABLE sales (id NUMBER, sale_date DATE, cust_id NUMBER, amt NUMBER)
PARTITION BY RANGE (sale_date) SUBPARTITION BY HASH (cust_id) SUBPARTITIONS 4 (
    PARTITION p_q1 VALUES LESS THAN (DATE '2025-04-01'),
    PARTITION p_q2 VALUES LESS THAN (DATE '2025-07-01')
);

-- Quản lý partition
ALTER TABLE orders_part ADD PARTITION p_2026 VALUES LESS THAN (DATE '2027-01-01');
ALTER TABLE orders_part DROP PARTITION p_2024;
ALTER TABLE orders_part TRUNCATE PARTITION p_2024;
ALTER TABLE orders_part SPLIT PARTITION p_future AT (DATE '2027-01-01')
    INTO (PARTITION p_2026, PARTITION p_future);
ALTER TABLE orders_part MERGE PARTITIONS p_2024, p_2025 INTO PARTITION p_2024_2025;
ALTER TABLE orders_part EXCHANGE PARTITION p_2025 WITH TABLE orders_staging;

-- Query partition cụ thể
SELECT * FROM orders_part PARTITION (p_2025);

-- Xem partitions
SELECT table_name, partition_name, high_value, num_rows
FROM user_tab_partitions WHERE table_name = 'ORDERS_PART' ORDER BY partition_position;
```

---

# PHẦN 8: MULTI SCHEMA & MULTI TENANT

## Schema / Synonym / Data Dictionary

```sql
-- Schema = User trong Oracle
SELECT USER FROM dual;
SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM dual;
ALTER SESSION SET CURRENT_SCHEMA = hr;  -- chuyển schema mặc định

-- Synonym
CREATE OR REPLACE SYNONYM emp FOR hr.employees;
CREATE OR REPLACE PUBLIC SYNONYM departments FOR hr.departments;
-- Ưu tiên: Local object > Private synonym > Public synonym

-- Data Dictionary Views (USER_ / ALL_ / DBA_)
SELECT username, account_status, created FROM dba_users WHERE oracle_maintained = 'N';
SELECT table_name, num_rows, last_analyzed FROM user_tables ORDER BY num_rows DESC;
SELECT grantee, privilege, grantable FROM dba_tab_privs WHERE table_name = 'EMPLOYEES';
SELECT name, type, referenced_name FROM user_dependencies WHERE name = 'V_ACTIVE_EMP';
SELECT object_name, object_type, status FROM user_objects WHERE status = 'INVALID';

-- AUTHID
CREATE OR REPLACE PROCEDURE proc_shared AUTHID DEFINER AS ...    -- chạy với quyền người tạo
CREATE OR REPLACE PROCEDURE proc_invoker AUTHID CURRENT_USER AS ...-- chạy với quyền người gọi
```

## Oracle Multitenant (CDB/PDB)

```sql
SELECT CDB FROM V$DATABASE;                   -- YES = multitenant
SELECT con_id, name, open_mode FROM V$PDBS;
SHOW CON_NAME;
ALTER SESSION SET CONTAINER = pdb_app;

-- Tạo PDB
CREATE PLUGGABLE DATABASE pdb_prod ADMIN USER pdb_admin IDENTIFIED BY "Pass123"
    FILE_NAME_CONVERT = ('/pdbseed/', '/pdb_prod/');

-- Open/Close
ALTER PLUGGABLE DATABASE pdb_prod OPEN;
ALTER PLUGGABLE DATABASE ALL OPEN;
ALTER PLUGGABLE DATABASE pdb_prod CLOSE IMMEDIATE;
ALTER PLUGGABLE DATABASE pdb_prod SAVE STATE;   -- auto-open khi CDB start

-- Unplug/Plug
ALTER PLUGGABLE DATABASE pdb_staging UNPLUG INTO '/backup/pdb_staging.xml';
DROP PLUGGABLE DATABASE pdb_staging KEEP DATAFILES;
CREATE PLUGGABLE DATABASE pdb_staging USING '/backup/pdb_staging.xml'
    FILE_NAME_CONVERT = ('/old/', '/new/');

-- Clone PDB
CREATE PLUGGABLE DATABASE pdb_test FROM pdb_prod
    FILE_NAME_CONVERT = ('/pdb_prod/', '/pdb_test/');

-- Common vs Local User
CREATE USER C##common_admin IDENTIFIED BY "Pass" CONTAINER = ALL;
ALTER SESSION SET CONTAINER = pdb_prod;
CREATE USER local_user IDENTIFIED BY "Pass";

-- Service Name (cho connection string)
-- jdbc:oracle:thin:@//host:1521/service_name
SELECT name, pdb FROM V$SERVICES;
```

---

# PHẦN 9: STORAGE & MONITORING & BACKUP

## Storage

```sql
-- Tablespace
CREATE TABLESPACE ts_data DATAFILE '/u01/data01.dbf' SIZE 1G
    AUTOEXTEND ON NEXT 100M MAXSIZE 10G;
CREATE TEMPORARY TABLESPACE ts_temp TEMPFILE '/u01/temp01.dbf' SIZE 2G AUTOEXTEND ON;
CREATE UNDO TABLESPACE ts_undo DATAFILE '/u01/undo01.dbf' SIZE 2G RETENTION GUARANTEE;

ALTER TABLESPACE ts_data ADD DATAFILE '/u01/data02.dbf' SIZE 1G;
ALTER DATABASE DATAFILE '/u01/data01.dbf' RESIZE 2G;
DROP TABLESPACE ts_old INCLUDING CONTENTS AND DATAFILES;

-- Xem dung lượng
SELECT df.tablespace_name,
    ROUND(df.total_mb, 2) AS total_mb,
    ROUND(df.total_mb - NVL(fs.free_mb,0), 2) AS used_mb,
    ROUND(NVL(fs.free_mb,0), 2) AS free_mb
FROM (SELECT tablespace_name, SUM(bytes)/1048576 total_mb FROM dba_data_files GROUP BY tablespace_name) df
LEFT JOIN (SELECT tablespace_name, SUM(bytes)/1048576 free_mb FROM dba_free_space GROUP BY tablespace_name) fs
ON df.tablespace_name = fs.tablespace_name ORDER BY used_mb DESC;

-- Top tables by size
SELECT segment_name, ROUND(bytes/1048576,2) AS mb FROM user_segments
WHERE segment_type = 'TABLE' ORDER BY bytes DESC FETCH FIRST 10 ROWS ONLY;
```

## Monitoring

```sql
-- V$SESSION — sessions hoạt động
SELECT sid, serial#, username, status, sql_id, event, wait_class,
    ROUND(last_call_et/60,1) AS idle_min, machine, program
FROM v$session WHERE type = 'USER' AND status = 'ACTIVE' ORDER BY last_call_et DESC;

-- Kill session
ALTER SYSTEM KILL SESSION 'sid,serial#' IMMEDIATE;

-- V$SQL — top SQL
SELECT sql_id, executions, ROUND(elapsed_time/1e6,2) AS total_sec,
    ROUND(elapsed_time/NULLIF(executions,0)/1e6,4) AS avg_sec,
    ROUND(buffer_gets/NULLIF(executions,0)) AS avg_gets,
    SUBSTR(sql_text,1,100) AS preview
FROM v$sql WHERE parsing_schema_name NOT IN ('SYS','SYSTEM')
ORDER BY elapsed_time DESC FETCH FIRST 20 ROWS ONLY;

-- V$LOCK — blocking sessions
SELECT s1.sid blocker, s1.username blocker_user,
    s2.sid blocked, s2.username blocked_user, s2.seconds_in_wait
FROM v$session s2 JOIN v$session s1 ON s2.blocking_session = s1.sid
WHERE s2.blocking_session IS NOT NULL;

-- AWR
BEGIN DBMS_WORKLOAD_REPOSITORY.CREATE_SNAPSHOT(); END; /
SELECT snap_id, begin_interval_time FROM dba_hist_snapshot ORDER BY snap_id DESC FETCH FIRST 10 ROWS ONLY;
-- AWR report: @?/rdbms/admin/awrrpt.sql

-- ASH — active sessions trong 5 phút
SELECT sample_time, session_id, sql_id, event, session_state
FROM v$active_session_history WHERE sample_time > SYSDATE - INTERVAL '5' MINUTE;

-- Top wait events
SELECT event, wait_class, COUNT(*) samples,
    ROUND(COUNT(*)*100/SUM(COUNT(*)) OVER(),2) pct
FROM v$active_session_history WHERE sample_time > SYSDATE - INTERVAL '1' HOUR
AND session_state = 'WAITING' GROUP BY event, wait_class ORDER BY samples DESC FETCH FIRST 10 ROWS ONLY;
```

## Backup & Recovery

```sql
-- RMAN (chạy từ OS: $ rman target /)
-- CONFIGURE RETENTION POLICY TO RECOVERY WINDOW OF 7 DAYS;
-- BACKUP DATABASE PLUS ARCHIVELOG;
-- BACKUP INCREMENTAL LEVEL 0 DATABASE;
-- BACKUP INCREMENTAL LEVEL 1 DATABASE;
-- BACKUP AS COMPRESSED BACKUPSET DATABASE;
-- RESTORE DATABASE; RECOVER DATABASE; ALTER DATABASE OPEN RESETLOGS;

-- FLASHBACK TABLE
ALTER TABLE employees ENABLE ROW MOVEMENT;
FLASHBACK TABLE employees TO TIMESTAMP SYSTIMESTAMP - INTERVAL '2' HOUR;
FLASHBACK TABLE employees TO BEFORE DROP;          -- từ recycle bin
FLASHBACK TABLE employees TO BEFORE DROP RENAME TO emp_restored;

-- FLASHBACK QUERY — xem data ở thời điểm trước
SELECT * FROM employees AS OF TIMESTAMP (SYSTIMESTAMP - INTERVAL '1' HOUR) WHERE emp_id = 1001;

-- So sánh hiện tại vs quá khứ
SELECT c.emp_id, c.salary AS now, h.salary AS before, c.salary - h.salary AS diff
FROM employees c
JOIN employees AS OF TIMESTAMP (SYSTIMESTAMP - INTERVAL '1' HOUR) h ON c.emp_id = h.emp_id
WHERE c.salary != h.salary;

-- FLASHBACK VERSIONS — lịch sử thay đổi
SELECT emp_id, salary, versions_operation, versions_starttime, versions_endtime
FROM employees VERSIONS BETWEEN TIMESTAMP SYSTIMESTAMP - INTERVAL '24' HOUR AND SYSTIMESTAMP
WHERE emp_id = 1001 ORDER BY versions_starttime;

-- ARCHIVELOG
SELECT log_mode FROM v$database;
-- Bật: SHUTDOWN IMMEDIATE; STARTUP MOUNT; ALTER DATABASE ARCHIVELOG; ALTER DATABASE OPEN;

-- Recycle Bin
SELECT original_name, type, droptime FROM recyclebin ORDER BY droptime DESC;
PURGE RECYCLEBIN;
```

---

# PHẦN 10: ORACLE SPECIFIC

## ROWNUM

```sql
-- Lấy N dòng đầu (KHÔNG sort)
SELECT * FROM employees WHERE ROWNUM <= 10;

-- ❌ SAI: ROWNUM gán TRƯỚC ORDER BY
SELECT * FROM employees WHERE ROWNUM <= 5 ORDER BY salary DESC;

-- ✅ ĐÚNG: wrap trong subquery
SELECT * FROM (SELECT * FROM employees ORDER BY salary DESC) WHERE ROWNUM <= 5;

-- ❌ ROWNUM = N (N > 1) không bao giờ trả kết quả
SELECT * FROM employees WHERE ROWNUM = 5;

-- Pagination cổ điển
SELECT * FROM (
    SELECT e.*, ROWNUM rn FROM (SELECT * FROM employees ORDER BY emp_id) e WHERE ROWNUM <= 30
) WHERE rn > 20;

-- ✅ 12c+: dùng FETCH FIRST thay ROWNUM
SELECT * FROM employees ORDER BY emp_id OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY;
```

## ROWID

```sql
SELECT ROWID, emp_id, first_name FROM employees WHERE emp_id = 1001;

-- Truy cập nhanh nhất
SELECT * FROM employees WHERE ROWID = 'AAAHYqAABAAALKJAAA';

-- Giải mã ROWID
SELECT DBMS_ROWID.ROWID_OBJECT(ROWID) obj, DBMS_ROWID.ROWID_RELATIVE_FNO(ROWID) file_no,
    DBMS_ROWID.ROWID_BLOCK_NUMBER(ROWID) block, DBMS_ROWID.ROWID_ROW_NUMBER(ROWID) row_no
FROM employees WHERE emp_id = 1001;

-- UC: Xóa trùng lặp
DELETE FROM employees WHERE ROWID NOT IN (
    SELECT MIN(ROWID) FROM employees GROUP BY email);

-- ⚠️ ROWID thay đổi khi: ALTER TABLE MOVE, EXPORT/IMPORT, partition operations
```

## CONNECT BY / START WITH / LEVEL

```sql
-- Cây tổ chức
SELECT LEVEL, LPAD(' ',(LEVEL-1)*4) || first_name AS org_tree, emp_id, manager_id
FROM employees START WITH manager_id IS NULL
CONNECT BY PRIOR emp_id = manager_id ORDER SIBLINGS BY last_name;

-- Đường dẫn (breadcrumb)
SELECT emp_id, SYS_CONNECT_BY_PATH(first_name, ' → ') AS path,
    CONNECT_BY_ISLEAF AS is_leaf, CONNECT_BY_ROOT first_name AS ceo
FROM employees START WITH manager_id IS NULL CONNECT BY PRIOR emp_id = manager_id;

-- Tìm tất cả cấp dưới
SELECT * FROM employees START WITH emp_id = 100 CONNECT BY PRIOR emp_id = manager_id;

-- Đi ngược lên từ nhân viên → CEO
SELECT LEVEL, first_name, manager_id
FROM employees START WITH emp_id = 150 CONNECT BY PRIOR manager_id = emp_id;

-- NOCYCLE — xử lý vòng lặp
SELECT LEVEL, emp_id, CONNECT_BY_ISCYCLE FROM employees
START WITH emp_id = 1 CONNECT BY NOCYCLE PRIOR emp_id = manager_id;

-- UC: Tạo dãy số
SELECT LEVEL AS n FROM dual CONNECT BY LEVEL <= 100;

-- UC: Tạo calendar tháng hiện tại
SELECT TRUNC(SYSDATE,'MONTH') + LEVEL - 1 AS dt,
    TO_CHAR(TRUNC(SYSDATE,'MONTH') + LEVEL - 1, 'DY') AS day_name
FROM dual CONNECT BY LEVEL <= EXTRACT(DAY FROM LAST_DAY(SYSDATE));

-- UC: Tách chuỗi CSV
SELECT TRIM(REGEXP_SUBSTR('A,B,C,D', '[^,]+', 1, LEVEL)) AS val
FROM dual CONNECT BY REGEXP_SUBSTR('A,B,C,D', '[^,]+', 1, LEVEL) IS NOT NULL;

-- UC: Time slots (mỗi 30 phút)
SELECT TO_CHAR(TRUNC(SYSDATE) + (LEVEL-1)*30/1440, 'HH24:MI') AS slot
FROM dual CONNECT BY LEVEL <= 48;

-- UC: Tổng lương team (manager + all reports)
SELECT CONNECT_BY_ROOT first_name AS manager, SUM(salary) total, COUNT(*) team_size
FROM employees START WITH manager_id IS NULL
CONNECT BY PRIOR emp_id = manager_id GROUP BY CONNECT_BY_ROOT first_name;
```

---

# PHẦN 11: ORACLE FUNCTIONS THƯỜNG DÙNG

## String

```sql
SELECT LENGTH('Oracle') FROM dual;                           -- 6
SELECT SUBSTR('Oracle DB', 1, 6) FROM dual;                  -- Oracle
SELECT INSTR('Oracle', 'a') FROM dual;                       -- 3
SELECT UPPER('oracle'), LOWER('ORACLE'), INITCAP('hello world') FROM dual;
SELECT LPAD(42, 8, '0') FROM dual;                           -- 00000042
SELECT RPAD('Hi', 10, '.') FROM dual;                        -- Hi........
SELECT TRIM('  hello  '), LTRIM('xxhello','x'), RTRIM('helloxx','x') FROM dual;
SELECT REPLACE('2025-01-15', '-', '/') FROM dual;            -- 2025/01/15
SELECT TRANSLATE('abc123', 'abc', 'xyz') FROM dual;          -- xyz123
SELECT CONCAT('Hello', ' World') FROM dual;                  -- Hello World
SELECT REVERSE('Oracle') FROM dual;                          -- elcarO (21c+)
SELECT ASCII('A'), CHR(65) FROM dual;                        -- 65, A
```

## Number

```sql
SELECT ROUND(15.678, 1), ROUND(15.678, -1) FROM dual;       -- 15.7, 20
SELECT TRUNC(15.678, 1), TRUNC(15.678, -1) FROM dual;       -- 15.6, 10
SELECT CEIL(15.1), FLOOR(15.9) FROM dual;                    -- 16, 15
SELECT MOD(17, 5), REMAINDER(17, 5) FROM dual;               -- 2, 2
SELECT ABS(-42), SIGN(-42), SIGN(0), SIGN(42) FROM dual;    -- 42, -1, 0, 1
SELECT POWER(2, 10), SQRT(144) FROM dual;                    -- 1024, 12
SELECT TO_CHAR(1234567.89, '9,999,999.00') FROM dual;       -- 1,234,567.89
SELECT TO_CHAR(0.15, '990.00%') FROM dual;                  -- not direct, use *100
```

## Date / Timestamp

```sql
SELECT SYSDATE, SYSTIMESTAMP, CURRENT_TIMESTAMP FROM dual;
SELECT ADD_MONTHS(SYSDATE, 3), ADD_MONTHS(SYSDATE, -6) FROM dual;
SELECT MONTHS_BETWEEN(DATE '2025-06-15', DATE '2025-01-01') FROM dual;  -- 5.45...
SELECT NEXT_DAY(SYSDATE, 'MONDAY') FROM dual;
SELECT LAST_DAY(SYSDATE) FROM dual;                          -- cuối tháng
SELECT TRUNC(SYSDATE, 'MONTH') FROM dual;                   -- đầu tháng
SELECT TRUNC(SYSDATE, 'YEAR') FROM dual;                    -- đầu năm
SELECT ROUND(SYSDATE, 'MONTH') FROM dual;                   -- đầu tháng gần nhất

SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') FROM dual;
SELECT TO_CHAR(SYSDATE, 'DD/MM/YYYY Day') FROM dual;
SELECT TO_DATE('15/01/2025', 'DD/MM/YYYY') FROM dual;
SELECT TO_TIMESTAMP('2025-01-15 14:30:00', 'YYYY-MM-DD HH24:MI:SS') FROM dual;

SELECT EXTRACT(YEAR FROM SYSDATE), EXTRACT(MONTH FROM SYSDATE), EXTRACT(DAY FROM SYSDATE) FROM dual;
SELECT SYSDATE - DATE '2025-01-01' AS days_diff FROM dual;   -- số ngày giữa 2 date

-- Interval
SELECT SYSDATE + INTERVAL '7' DAY FROM dual;
SELECT SYSDATE + INTERVAL '2' HOUR FROM dual;
SELECT SYSTIMESTAMP + INTERVAL '30' MINUTE FROM dual;
SELECT NUMTOYMINTERVAL(18, 'MONTH') FROM dual;               -- +01-06
SELECT NUMTODSINTERVAL(3.5, 'DAY') FROM dual;                -- +03 12:00:00
```

## Conversion

```sql
SELECT TO_NUMBER('1,234,567.89', '9,999,999.99') FROM dual;
SELECT TO_CHAR(12345, 'FM99999') FROM dual;                  -- FM removes leading spaces
SELECT CAST('123' AS NUMBER), CAST(SYSDATE AS TIMESTAMP) FROM dual;
SELECT TO_CLOB('text'), TO_BLOB(UTL_RAW.CAST_TO_RAW('data')) FROM dual;
```

---

# PHẦN 12: USEFUL PATTERNS

## Pagination (tổng hợp)

```sql
-- 12c+: OFFSET FETCH (✅ recommended)
SELECT * FROM employees ORDER BY emp_id OFFSET :page_size * (:page - 1) ROWS FETCH NEXT :page_size ROWS ONLY;

-- ROW_NUMBER (mọi version)
SELECT * FROM (SELECT e.*, ROW_NUMBER() OVER (ORDER BY emp_id) rn FROM employees e)
WHERE rn BETWEEN 21 AND 30;

-- ROWNUM (legacy)
SELECT * FROM (SELECT e.*, ROWNUM rn FROM (SELECT * FROM employees ORDER BY emp_id) e WHERE ROWNUM <= 30) WHERE rn > 20;
```

## Upsert patterns

```sql
-- MERGE (✅ recommended)
MERGE INTO target t USING source s ON (t.id = s.id)
WHEN MATCHED THEN UPDATE SET t.val = s.val
WHEN NOT MATCHED THEN INSERT VALUES (s.id, s.val);

-- Exception-based (legacy)
BEGIN
    INSERT INTO target VALUES (...);
EXCEPTION WHEN DUP_VAL_ON_INDEX THEN
    UPDATE target SET ... WHERE id = ...;
END;
```

## Gap-free sequence check

```sql
SELECT prev_id + 1 AS gap_start, id - 1 AS gap_end FROM (
    SELECT emp_id AS id, LAG(emp_id) OVER (ORDER BY emp_id) AS prev_id FROM employees
) WHERE id - prev_id > 1;
```

## Running balance

```sql
SELECT txn_date, amount,
    SUM(CASE WHEN type='CREDIT' THEN amount ELSE -amount END)
        OVER (ORDER BY txn_date ROWS UNBOUNDED PRECEDING) AS balance
FROM transactions;
```

## Conditional aggregation (Pivot)

```sql
SELECT dept_id,
    SUM(CASE WHEN TO_CHAR(hire_date,'YYYY')='2023' THEN 1 ELSE 0 END) AS y2023,
    SUM(CASE WHEN TO_CHAR(hire_date,'YYYY')='2024' THEN 1 ELSE 0 END) AS y2024,
    SUM(CASE WHEN TO_CHAR(hire_date,'YYYY')='2025' THEN 1 ELSE 0 END) AS y2025
FROM employees GROUP BY dept_id;
```

## Hierarchical path aggregation

```sql
SELECT emp_id, first_name, LEVEL AS depth,
    SYS_CONNECT_BY_PATH(first_name, '/') AS org_path,
    CONNECT_BY_ROOT first_name AS top_manager,
    CONNECT_BY_ISLEAF AS is_individual_contributor
FROM employees START WITH manager_id IS NULL
CONNECT BY PRIOR emp_id = manager_id;
```

---

> 🔥 **Tổng kết Best Practices:**
> 1. Dùng bind variables (`:param`) thay literal — tối ưu shared pool
> 2. Thu thập DBMS_STATS sau bulk load
> 3. Đọc EXPLAIN PLAN trước khi deploy query mới
> 4. Tránh hàm trên cột trong WHERE (phá index) — tạo function-based index nếu cần
> 5. BULK COLLECT + FORALL thay vì row-by-row loop (10-50x nhanh hơn)
> 6. Dùng COALESCE thay NVL (ANSI standard)
> 7. Dùng ANSI JOIN thay (+) syntax
> 8. Dùng EXISTS thay IN khi bảng lớn (đặc biệt NOT EXISTS thay NOT IN)
> 9. UNION ALL thay UNION khi biết không trùng
> 10. FETCH FIRST thay ROWNUM (12c+)

CREATE DATABASE IF NOT EXISTS booknest;

USE booknest;

CREATE TABLE IF NOT EXISTS books (
    book_id VARCHAR(20) PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    author VARCHAR(100) NOT NULL,
    total_copies INT NOT NULL,
    available_copies INT NOT NULL,

    CONSTRAINT chk_total_copies
        CHECK (total_copies >= 0),

    CONSTRAINT chk_available_copies
        CHECK (available_copies >= 0),

    CONSTRAINT chk_available_limit
        CHECK (available_copies <= total_copies)
);

CREATE TABLE IF NOT EXISTS members (
    member_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS borrow_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(20) NOT NULL,
    book_id VARCHAR(20) NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE NULL,
    days_kept INT NOT NULL DEFAULT 0,
    fine DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    CONSTRAINT fk_borrow_member
        FOREIGN KEY (member_id)
        REFERENCES members(member_id),

    CONSTRAINT fk_borrow_book
        FOREIGN KEY (book_id)
        REFERENCES books(book_id),

    CONSTRAINT chk_days_kept
        CHECK (days_kept >= 0),

    CONSTRAINT chk_fine
        CHECK (fine >= 0)
);
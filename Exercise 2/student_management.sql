CREATE DATABASE student_management;
USE student_management;

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    score DOUBLE,
    image VARCHAR(255),
    address VARCHAR(255),
    note TEXT
);

INSERT INTO students (id, name, score, image, address, note) VALUES
(1, 'Alice Johnson', 8.5, 'images/alice_johnson.png', '123 Maple Street, NY', 'Excellent in Math'),
(2, 'Bob Smith', 7.0, 'images/bob_smith.png', '456 Oak Avenue, CA', 'Needs improvement in Science'),
(3, 'Charlie Davis', 9.3, 'images/charlie_davis.png', '789 Pine Road, TX', 'Top scorer in English'),
(4, 'Diana Miller', 6.8, 'images/diana_miller.png', '321 Birch Lane, FL', 'Struggles with assignments'),
(5, 'Ethan Wilson', 8.6, 'images/ethan_wilson.png', '654 Cedar Blvd, WA', 'Active in sports'),
(6, 'Fiona Brown', 9.1, 'images/fiona_brown.png', '987 Spruce Drive, IL', 'Loves programming'),
(7, 'George Anderson', 7.5, 'images/george_anderson.png', '159 Redwood Ct, AZ', 'Participates in debate club'),
(8, 'Hannah Thomas', 8.9, 'images/hannah_thomas.png', '753 Willow St, NV', 'Good in history'),
(9, 'Isaac White', 8.2, 'images/isaac_white.png', '951 Elm St, MI', 'Needs more practice in physics'),
(10, 'Julia Harris', 9.4, 'images/julia_harris.png', '852 Sycamore Dr, CO', 'Outstanding in chemistry');
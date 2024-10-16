/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  mac
 * Created: 16 thg 8, 2024
 */


CREATE TABLE IF NOT EXISTS payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    payment_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true
);

ALTER TABLE orders
ADD COLUMN payment_id INT,
ADD CONSTRAINT fk_orders_payment
FOREIGN KEY (payment_id) REFERENCES payments(id);
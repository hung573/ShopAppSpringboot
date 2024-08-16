/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  mac
 * Created: 16 thg 8, 2024
 */


CREATE TABLE IF NOT EXISTS coupons (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true
);

ALTER TABLE orders
ADD COLUMN coupon_id INT,
ADD CONSTRAINT fk_orders_coupon
FOREIGN KEY (coupon_id) REFERENCES coupons(id);

ALTER TABLE order_details
ADD COLUMN coupon_id INT,
ADD CONSTRAINT fk_order_details_coupon
FOREIGN KEY (coupon_id) REFERENCES coupons(id);

CREATE TABLE IF NOT EXISTS coupon_conditions(
    id INT AUTO_INCREMENT PRIMARY KEY,
    coupon_id int NOT null,
    attribute varchar(255) NOT null,
    operator varchar(10) NOT null, 
    value varchar(255) NOT null,
    discount_amount float NOT null,
    FOREIGN KEY (coupon_id) REFERENCES coupons(id) 
);

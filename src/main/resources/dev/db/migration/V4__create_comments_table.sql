/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  mac
 * Created: 11 thg 8, 2024
 */

CREATE TABLE comments (
	id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    user_id INT,
    content VARCHAR(500),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
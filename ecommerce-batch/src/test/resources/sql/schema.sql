drop table if exists products;

create table products
(
    product_id       varchar(255) primary key,
    seller_id        bigint       not null,
    category         varchar(255) not null,
    product_name     varchar(255) not null,
    sales_start_date date,
    sales_end_date   date,
    product_status   varchar(50),

    brand            varchar(255),
    manufacturer     varchar(255),
    sales_price      Integer      not null,
    stock_quantity   Integer   default 0,
    created_at       timestamp default current_timestamp,
    updated_at       timestamp default current_timestamp

);


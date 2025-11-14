create table if not exists orders.purchase_order (
    id uuid primary key,
    account_id varchar(36) not null,
    created_at timestamptz not null,
    total numeric(19, 2) not null
);

create index if not exists idx_purchase_order_account
    on orders.purchase_order (account_id);

create table if not exists orders.purchase_order_item (
    id uuid primary key,
    order_id uuid not null references orders.purchase_order (id) on delete cascade,
    product_id varchar(36) not null,
    product_name varchar(255) not null,
    product_unit varchar(50) not null,
    product_price numeric(19, 2) not null,
    quantity integer not null,
    total numeric(19, 2) not null
);

create index if not exists idx_purchase_order_item_order
    on orders.purchase_order_item (order_id);


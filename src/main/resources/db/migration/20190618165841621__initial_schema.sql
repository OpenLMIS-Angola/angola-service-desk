CREATE TABLE service_desk_customers (
    id uuid NOT NULL PRIMARY KEY,
    email character varying(255) NOT NULL,
    customerid character varying(255) NOT NULL
);

ALTER TABLE service_desk_customers
  ADD CONSTRAINT unq_customer_email UNIQUE (email, customerid);
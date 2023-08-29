create table file_entry
(
    id serial,
    file_name text NOT NULL UNIQUE,
    file_size bigint NOT NULL,
    file_data bytea,
    PRIMARY KEY (id)
);
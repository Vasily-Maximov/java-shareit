DROP TABLE IF EXISTS users, items, bookings, requests, comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar(50) NOT NULL,
    email varchar(50) UNIQUE NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS requests (
    id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description varchar NOT NULL,
    requester_id integer NOT NULL,
    created timestamp WITHOUT TIME ZONE,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_requestor FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
    id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar(50) NOT NULL,
    description varchar(50) NOT NULL,
    available boolean NOT NULL,
    owner_id integer NOT NULL,
    request_id  integer,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_items_owner_id FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date timestamp NOT NULL,
    end_date timestamp NOT NULL,
    item_id integer NOT NULL,
    booker_id integer NOT NULL,
    status varchar(50) NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_bookings_item_id FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_booker_id FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text varchar(300) NOT NULL,
    item_id integer NOT NULL,
    author_id integer NOT NULL,
    created timestamp WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_author_id FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);
-- 1. Enable Extensions
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE user_gender AS ENUM ('MALE', 'FEMALE');
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE ride_status AS ENUM ('SCHEDULED', 'FULL', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE booking_status AS ENUM ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELLED');
CREATE TYPE ride_gender_preference AS ENUM ('ANY', 'FEMALE_ONLY', 'MALE_ONLY');

CREATE TABLE bookings
(
    id           CHAR(36)       DEFAULT uuid_generate_v4() NOT NULL,
    ride_id      CHAR(36)                                  NOT NULL,
    passenger_id CHAR(36)                                  NOT NULL,
    seats_booked INTEGER        DEFAULT 1,
    price_total  numeric(10, 2)                            NOT NULL,
    status       BOOKING_STATUS DEFAULT 'PENDING',
    created_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at   TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT bookings_pkey PRIMARY KEY (id)
);

CREATE TABLE reviews
(
    id          CHAR(36) DEFAULT uuid_generate_v4() NOT NULL,
    booking_id  CHAR(36)                            NOT NULL,
    reviewer_id CHAR(36)                            NOT NULL,
    reviewee_id CHAR(36)                            NOT NULL,
    rating      INTEGER                             NOT NULL,
    comment     TEXT,
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT reviews_pkey PRIMARY KEY (id)
);

CREATE TABLE rides
(
    id                CHAR(36)               DEFAULT uuid_generate_v4() NOT NULL,
    driver_id         CHAR(36)                                          NOT NULL,
    origin_name       VARCHAR(255)                                      NOT NULL,
    origin_point      GEOGRAPHY                                         NOT NULL,
    destination_name  VARCHAR(255)                                      NOT NULL,
    destination_point GEOGRAPHY                                         NOT NULL,
    departure_time    TIMESTAMP WITHOUT TIME ZONE                            NOT NULL,
    total_seats       INTEGER                                           NOT NULL,
    available_seats   INTEGER                                           NOT NULL,
    price_per_seat    numeric(10, 2)                                    NOT NULL,
    gender_preference RIDE_GENDER_PREFERENCE DEFAULT 'ANY',
    allows_smoking    BOOLEAN                DEFAULT FALSE,
    status            RIDE_STATUS            DEFAULT 'SCHEDULED',
    created_at        TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    version           INTEGER                DEFAULT 0,
    CONSTRAINT rides_pkey PRIMARY KEY (id)
);

CREATE TABLE spatial_ref_sys
(
    srid      INTEGER NOT NULL,
    auth_name VARCHAR(256),
    auth_srid INTEGER,
    srtext    VARCHAR(2048),
    proj4text VARCHAR(2048),
    CONSTRAINT spatial_ref_sys_pkey PRIMARY KEY (srid)
);

CREATE TABLE users
(
    id                  CHAR(36)  DEFAULT uuid_generate_v4() NOT NULL,
    email               VARCHAR(255)                         NOT NULL,
    password_hash       VARCHAR(255)                         NOT NULL,
    phone_number        VARCHAR(20)                          NOT NULL,
    first_name          VARCHAR(100)                         NOT NULL,
    last_name           VARCHAR(100)                         NOT NULL,
    gender              USER_GENDER                          NOT NULL,
    date_of_birth       date                                 NOT NULL,
    bio                 TEXT,
    profile_picture_url TEXT,
    cin_number          VARCHAR(20),
    email_verified      BOOLEAN   DEFAULT FALSE,
    role                USER_ROLE DEFAULT 'USER',
    created_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at          TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    deleted_at          TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE vehicles
(
    id              CHAR(36) DEFAULT uuid_generate_v4() NOT NULL,
    owner_id        CHAR(36)                            NOT NULL,
    make            VARCHAR(50)                         NOT NULL,
    model           VARCHAR(50)                         NOT NULL,
    color           VARCHAR(30)                         NOT NULL,
    license_plate   VARCHAR(20)                         NOT NULL,
    production_year INTEGER,
    created_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT vehicles_pkey PRIMARY KEY (id)
);

ALTER TABLE bookings
    ADD CONSTRAINT bookings_ride_id_passenger_id_key UNIQUE (ride_id, passenger_id);

ALTER TABLE reviews
    ADD CONSTRAINT reviews_booking_id_reviewer_id_key UNIQUE (booking_id, reviewer_id);

ALTER TABLE users
    ADD CONSTRAINT users_cin_number_key UNIQUE (cin_number);

ALTER TABLE users
    ADD CONSTRAINT users_email_key UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT users_phone_number_key UNIQUE (phone_number);

ALTER TABLE vehicles
    ADD CONSTRAINT vehicles_license_plate_key UNIQUE (license_plate);

ALTER TABLE bookings
    ADD CONSTRAINT bookings_passenger_id_fkey FOREIGN KEY (passenger_id) REFERENCES users (id) ON DELETE NO ACTION;

ALTER TABLE bookings
    ADD CONSTRAINT bookings_ride_id_fkey FOREIGN KEY (ride_id) REFERENCES rides (id) ON DELETE NO ACTION;

ALTER TABLE reviews
    ADD CONSTRAINT reviews_booking_id_fkey FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE NO ACTION;

ALTER TABLE reviews
    ADD CONSTRAINT reviews_reviewee_id_fkey FOREIGN KEY (reviewee_id) REFERENCES users (id) ON DELETE NO ACTION;

ALTER TABLE reviews
    ADD CONSTRAINT reviews_reviewer_id_fkey FOREIGN KEY (reviewer_id) REFERENCES users (id) ON DELETE NO ACTION;

ALTER TABLE rides
    ADD CONSTRAINT rides_driver_id_fkey FOREIGN KEY (driver_id) REFERENCES users (id) ON DELETE NO ACTION;

ALTER TABLE vehicles
    ADD CONSTRAINT vehicles_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE;

CREATE VIEW geography_columns AS
SELECT current_database()               AS f_table_catalog,
       n.nspname                        AS f_table_schema,
       c.relname                        AS f_table_name,
       a.attname                        AS f_geography_column,
       postgis_typmod_dims(a.atttypmod) AS coord_dimension,
       postgis_typmod_srid(a.atttypmod) AS srid,
       postgis_typmod_type(a.atttypmod) AS type
FROM pg_class c,
     pg_attribute a,
     pg_type t,
     pg_namespace n
WHERE t.typname = 'geography'::name
  AND a.attisdropped = false
  AND a.atttypid = t.oid
  AND a.attrelid = c.oid
  AND c.relnamespace = n.oid
  AND (c.relkind = ANY (ARRAY ['r'::"char", 'v'::"char", 'm'::"char", 'f'::"char", 'p'::"char"]))
  AND NOT pg_is_other_temp_schema(c.relnamespace)
  AND has_table_privilege(c.oid, 'SELECT'::text);

alter table geography_columns
    owner to postgres;

grant select on geography_columns to public;

CREATE VIEW geometry_columns AS
SELECT current_database() ::character varying(256)              AS f_table_catalog,
       n.nspname                                               AS f_table_schema,
       c.relname                                               AS f_table_name,
       a.attname                                               AS f_geometry_column,
       COALESCE(postgis_typmod_dims(a.atttypmod), sn.ndims, 2) AS coord_dimension,
       COALESCE(NULLIF(postgis_typmod_srid(a.atttypmod), 0), sr.srid,
                0)                                             AS srid,
       replace(replace(COALESCE(NULLIF(upper(postgis_typmod_type(a.atttypmod)), 'GEOMETRY'::text), st.type,
                                'GEOMETRY'::text), 'ZM'::text, ''::text), 'Z'::text,
               ''::text)::character varying(30)                AS type
FROM pg_class c
         JOIN pg_attribute a ON a.attrelid = c.oid AND NOT a.attisdropped
         JOIN pg_namespace n ON c.relnamespace = n.oid
         JOIN pg_type t ON a.atttypid = t.oid
         LEFT JOIN (SELECT s.connamespace,
                           s.conrelid,
                           s.conkey,
                           (regexp_match(s.consrc, 'geometrytype\(\w+\)\s*=\s*''(\w+)'''::text, 'i'::text))[1] AS type
                    FROM (SELECT pg_constraint.connamespace,
                                 pg_constraint.conrelid,
                                 pg_constraint.conkey,
                                 pg_get_constraintdef(pg_constraint.oid) AS consrc
                          FROM pg_constraint) s
                    WHERE s.consrc ~* 'geometrytype\(\w+\)\s*=\s*''\w+'''::text) st
                   ON st.connamespace = n.oid AND st.conrelid = c.oid AND (a.attnum = ANY (st.conkey))
         LEFT JOIN (SELECT s.connamespace,
                           s.conrelid,
                           s.conkey,
                           (regexp_match(s.consrc, 'ndims\(\w+\)\s*=\s*(\d+)'::text, 'i'::text))[1]::integer AS ndims
                    FROM (SELECT pg_constraint.connamespace,
                                 pg_constraint.conrelid,
                                 pg_constraint.conkey,
                                 pg_get_constraintdef(pg_constraint.oid) AS consrc
                          FROM pg_constraint) s
                    WHERE s.consrc ~* 'ndims\(\w+\)\s*=\s*\d+'::text) sn
                   ON sn.connamespace = n.oid AND sn.conrelid = c.oid AND (a.attnum = ANY (sn.conkey))
         LEFT JOIN (SELECT s.connamespace,
                           s.conrelid,
                           s.conkey,
                           (regexp_match(s.consrc, 'srid\(\w+\)\s*=\s*(\d+)'::text, 'i'::text))[1]::integer AS srid
                    FROM (SELECT pg_constraint.connamespace,
                                 pg_constraint.conrelid,
                                 pg_constraint.conkey,
                                 pg_get_constraintdef(pg_constraint.oid) AS consrc
                          FROM pg_constraint) s
                    WHERE s.consrc ~* 'srid\(\w+\)\s*=\s*\d+'::text) sr
                   ON sr.connamespace = n.oid AND sr.conrelid = c.oid AND (a.attnum = ANY (sr.conkey))
WHERE (c.relkind = ANY (ARRAY ['r'::"char", 'v'::"char", 'm'::"char", 'f'::"char", 'p'::"char"]))
  AND NOT c.relname = 'raster_columns'::name
  AND t.typname = 'geometry'::name
  AND NOT pg_is_other_temp_schema(c.relnamespace)
  AND has_table_privilege(c.oid, 'SELECT'::text);

alter table geometry_columns
    owner to postgres;

grant select on geometry_columns to public;
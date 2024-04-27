DROP TABLE IF EXISTS source, tariff_zone, standard_sfc, source_properties, users, authorities CASCADE;

CREATE TABLE IF NOT EXISTS sources
(
    source_id      UUID         NOT NULL DEFAULT gen_random_uuid(),
    source_name VARCHAR(50) NOT NULL,
    source_address VARCHAR(255) NOT NULL,
    source_type    varchar(7)   NOT NULL,

    CONSTRAINT pk_source PRIMARY KEY (source_id)
);

CREATE TABLE IF NOT EXISTS tariff_zones
(
    zone_id   INTEGER NOT NULL,
    zone_name VARCHAR(255),

    CONSTRAINT pk_zone PRIMARY KEY (zone_id)
);

CREATE TABLE IF NOT EXISTS source_properties
(
    source_id     UUID    NOT NULL,
    ssfc_year     INTEGER NOT NULL,
    source_branch INTEGER NOT NULL,
    zone_id       INTEGER NOT NULL,

    CONSTRAINT pk_source_prop PRIMARY KEY (source_id, ssfc_year),
    CONSTRAINT fk_source_prop FOREIGN KEY (source_id) REFERENCES sources (source_id),
    CONSTRAINT fk_zone_prop FOREIGN KEY (zone_id) REFERENCES tariff_zones (zone_id)

);

CREATE TABLE IF NOT EXISTS standard_sfcs
(
    ssfc_id       UUID             NOT NULL,
    source_id     UUID             NOT NULL,
    ssfc_year INTEGER     NOT NULL,
    generation    DOUBLE PRECISION NOT NULL,
    own_needs     DOUBLE PRECISION NOT NULL,
    production    DOUBLE PRECISION NOT NULL,
    standard_sfc  DOUBLE PRECISION NOT NULL,
    standard_sfcg DOUBLE PRECISION NOT NULL,
    ssfc_month    INTEGER          NOT NULL,
    fuel_type VARCHAR(17) NOT NULL,

    CONSTRAINT pk_ssfc PRIMARY KEY (ssfc_id),
    CONSTRAINT fk_source_prop FOREIGN KEY (source_id, ssfc_year) REFERENCES source_properties (source_id, ssfc_year)
);

CREATE TABLE IF NOT EXISTS users
(
    username VARCHAR(50)  NOT NULL PRIMARY KEY,
    password VARCHAR(500) NOT NULL,
    enabled  BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS authorities
(
    username  VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username)
);

CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);
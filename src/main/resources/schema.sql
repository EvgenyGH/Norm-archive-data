-- DROP TABLE IF EXISTS source, standardsfc CASCADE;

CREATE TABLE IF NOT EXISTS source
(
    source_id       UUID         NOT NULL DEFAULT gen_random_uuid(),
    source_capacity REAL         NOT NULL,
    source_address  VARCHAR(255) NOT NULL,
    source_branch   INTEGER      NOT NULL,
    source_name     VARCHAR(50)  NOT NULL,
    tariff_zone     VARCHAR(255) NOT NULL,
    CONSTRAINT pk_source PRIMARY KEY (source_id)
);

CREATE TABLE If Not Exists standardsfc
(
    fuel_consumption     DOUBLE PRECISION NOT NULL,
    fuel_consumption_std DOUBLE PRECISION NOT NULL,
    generation           DOUBLE PRECISION NOT NULL,
    own_needs            DOUBLE PRECISION NOT NULL,
    production           DOUBLE PRECISION NOT NULL,
    standard_sfc         DOUBLE PRECISION NOT NULL,
    standard_sfcg        DOUBLE PRECISION NOT NULL,
    source_id            UUID             NOT NULL,
    ssfc_id              UUID             NOT NULL,
    fuel_type            VARCHAR(30)      NOT NULL,

    CONSTRAINT pk_ssfc PRIMARY KEY (ssfc_id),
    CONSTRAINT fk_source FOREIGN KEY (source_id) REFERENCES source (source_id)
);

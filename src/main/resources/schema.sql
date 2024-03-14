DROP TABLE IF EXISTS calories, source, standardsfc CASCADE;

CREATE TABLE calories (
    calories_id UUID NOT NULL ,
    name        VARCHAR(30) NOT NULL,
    calories    DOUBLE PRECISION NOT NULL,
    CONSTRAINT calories_pk PRIMARY KEY (calories_id)
);

CREATE TABLE source (
    source_id       UUID NOT NULL DEFAULT gen_random_uuid(),
    source_capacity real         not null,
    source_address  varchar(255) not null,
    source_branch   varchar(255) not null,
    source_name     varchar(255) not null,
    tariff_zone     varchar(255) not null,
    CONSTRAINT pk_source PRIMARY KEY (source_id)
);

alter table public.source
    owner to "Tester";

create table public.standardsfc
(
    fuel_consumption     double precision not null,
    fuel_consumption_std double precision not null,
    generation           double precision not null,
    own_needs            double precision not null,
    production           double precision not null,
    standard_sfc         double precision not null,
    standard_sfcg        double precision not null,
    calories             uuid             not null
        constraint fk5ftqpjnxrh05qh1mmbe0ge3u9
            references public.calories,
    source_id            uuid             not null
        constraint fk6ko7co3p5x1ntqm9bgnhab6m0
            references public.source,
    ssfc_id              uuid             not null
        primary key,
    fuel_type            varchar(255)     not null
        constraint standardsfc_fuel_type_check
            check ((fuel_type)::text = ANY ((ARRAY ['GAS'::character varying, 'DIESEL'::character varying])::text[]))
);

--
-- PostgreSQL database dump
--

-- Dumped from database version 15.2 (Debian 15.2-1.pgdg110+1)
-- Dumped by pg_dump version 15.2 (Debian 15.2-1.pgdg110+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: t_aliases; Type: TABLE; Schema: public; Owner: bit_admin
--

CREATE TABLE public.t_aliases (
    id integer NOT NULL,
    git_login character varying(256) DEFAULT ''::character varying NOT NULL,
    messenger_login character varying(256) NOT NULL,
    git_source_id integer NOT NULL,
    messenger_type_id integer NOT NULL
);


ALTER TABLE public.t_aliases OWNER TO bit_admin;

--
-- Name: t_aliases_id_seq; Type: SEQUENCE; Schema: public; Owner: bit_admin
--

ALTER TABLE public.t_aliases ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.t_aliases_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: t_git_sources; Type: TABLE; Schema: public; Owner: bit_admin
--

CREATE TABLE public.t_git_sources (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    source character varying(256) NOT NULL
);


ALTER TABLE public.t_git_sources OWNER TO bit_admin;

--
-- Name: TABLE t_git_sources; Type: COMMENT; Schema: public; Owner: bit_admin
--

COMMENT ON TABLE public.t_git_sources IS 'Типы гит репозиториев с их краткой записью';


--
-- Name: t_git_sources_id_seq; Type: SEQUENCE; Schema: public; Owner: bit_admin
--

CREATE SEQUENCE public.t_git_sources_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.t_git_sources_id_seq OWNER TO bit_admin;

--
-- Name: t_git_sources_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bit_admin
--

ALTER SEQUENCE public.t_git_sources_id_seq OWNED BY public.t_git_sources.id;


--
-- Name: t_messenger_type; Type: TABLE; Schema: public; Owner: bit_admin
--

CREATE TABLE public.t_messenger_type (
    id integer NOT NULL,
    name character varying(256) NOT NULL
);


ALTER TABLE public.t_messenger_type OWNER TO bit_admin;

--
-- Name: t_messenger_type_id_seq; Type: SEQUENCE; Schema: public; Owner: bit_admin
--

CREATE SEQUENCE public.t_messenger_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.t_messenger_type_id_seq OWNER TO bit_admin;

--
-- Name: t_messenger_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bit_admin
--

ALTER SEQUENCE public.t_messenger_type_id_seq OWNED BY public.t_messenger_type.id;


--
-- Name: t_repositories; Type: TABLE; Schema: public; Owner: bit_admin
--

CREATE TABLE public.t_repositories (
    id integer NOT NULL,
    full_name character varying(512) NOT NULL,
    short_name character varying(256) NOT NULL,
    git_source_id integer NOT NULL
);


ALTER TABLE public.t_repositories OWNER TO bit_admin;

--
-- Name: t_repositories_id_seq; Type: SEQUENCE; Schema: public; Owner: bit_admin
--

CREATE SEQUENCE public.t_repositories_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.t_repositories_id_seq OWNER TO bit_admin;

--
-- Name: t_repositories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bit_admin
--

ALTER SEQUENCE public.t_repositories_id_seq OWNED BY public.t_repositories.id;


--
-- Name: t_repository_subscribes; Type: TABLE; Schema: public; Owner: bit_admin
--

CREATE TABLE public.t_repository_subscribes (
    id integer NOT NULL,
    chat_id character varying(256) NOT NULL,
    messenger_type_id integer NOT NULL,
    repository_id integer NOT NULL
);


ALTER TABLE public.t_repository_subscribes OWNER TO bit_admin;

--
-- Name: t_repository_subscribes_id_seq; Type: SEQUENCE; Schema: public; Owner: bit_admin
--

CREATE SEQUENCE public.t_repository_subscribes_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.t_repository_subscribes_id_seq OWNER TO bit_admin;

--
-- Name: t_repository_subscribes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: bit_admin
--

ALTER SEQUENCE public.t_repository_subscribes_id_seq OWNED BY public.t_repository_subscribes.id;


--
-- Name: t_git_sources id; Type: DEFAULT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_git_sources ALTER COLUMN id SET DEFAULT nextval('public.t_git_sources_id_seq'::regclass);


--
-- Name: t_messenger_type id; Type: DEFAULT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_messenger_type ALTER COLUMN id SET DEFAULT nextval('public.t_messenger_type_id_seq'::regclass);


--
-- Name: t_repositories id; Type: DEFAULT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_repositories ALTER COLUMN id SET DEFAULT nextval('public.t_repositories_id_seq'::regclass);


--
-- Name: t_repository_subscribes id; Type: DEFAULT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_repository_subscribes ALTER COLUMN id SET DEFAULT nextval('public.t_repository_subscribes_id_seq'::regclass);


--
-- Data for Name: t_aliases; Type: TABLE DATA; Schema: public; Owner: bit_admin
--

COPY public.t_aliases (id, git_login, messenger_login, git_source_id, messenger_type_id) FROM stdin;
\.


--
-- Data for Name: t_git_sources; Type: TABLE DATA; Schema: public; Owner: bit_admin
--

COPY public.t_git_sources (id, name, source) FROM stdin;
\.


--
-- Data for Name: t_messenger_type; Type: TABLE DATA; Schema: public; Owner: bit_admin
--

COPY public.t_messenger_type (id, name) FROM stdin;
\.


--
-- Data for Name: t_repositories; Type: TABLE DATA; Schema: public; Owner: bit_admin
--

COPY public.t_repositories (id, full_name, short_name, git_source_id) FROM stdin;
\.


--
-- Data for Name: t_repository_subscribes; Type: TABLE DATA; Schema: public; Owner: bit_admin
--

COPY public.t_repository_subscribes (id, chat_id, messenger_type_id, repository_id) FROM stdin;
\.


--
-- Name: t_aliases_id_seq; Type: SEQUENCE SET; Schema: public; Owner: bit_admin
--

SELECT pg_catalog.setval('public.t_aliases_id_seq', 4, true);


--
-- Name: t_git_sources_id_seq; Type: SEQUENCE SET; Schema: public; Owner: bit_admin
--

SELECT pg_catalog.setval('public.t_git_sources_id_seq', 9, true);


--
-- Name: t_messenger_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: bit_admin
--

SELECT pg_catalog.setval('public.t_messenger_type_id_seq', 1, true);


--
-- Name: t_repositories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: bit_admin
--

SELECT pg_catalog.setval('public.t_repositories_id_seq', 13, true);


--
-- Name: t_repository_subscribes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: bit_admin
--

SELECT pg_catalog.setval('public.t_repository_subscribes_id_seq', 11, true);


--
-- Name: t_aliases t_aliases_id_key; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_aliases
    ADD CONSTRAINT t_aliases_id_key UNIQUE (id);


--
-- Name: t_aliases t_aliases_pkey; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_aliases
    ADD CONSTRAINT t_aliases_pkey PRIMARY KEY (id);


--
-- Name: t_git_sources t_git_sources_code_key; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_git_sources
    ADD CONSTRAINT t_git_sources_code_key UNIQUE (id);


--
-- Name: t_git_sources t_git_sources_pkey; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_git_sources
    ADD CONSTRAINT t_git_sources_pkey PRIMARY KEY (id);


--
-- Name: t_git_sources t_git_sources_source_key; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_git_sources
    ADD CONSTRAINT t_git_sources_source_key UNIQUE (source);


--
-- Name: t_messenger_type t_messenger_type_id_key; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_messenger_type
    ADD CONSTRAINT t_messenger_type_id_key UNIQUE (id);


--
-- Name: t_messenger_type t_messenger_type_name_key; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_messenger_type
    ADD CONSTRAINT t_messenger_type_name_key UNIQUE (name);


--
-- Name: t_messenger_type t_messenger_type_pkey; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_messenger_type
    ADD CONSTRAINT t_messenger_type_pkey PRIMARY KEY (id);


--
-- Name: t_repositories t_repositories_id_key; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_repositories
    ADD CONSTRAINT t_repositories_id_key UNIQUE (id);


--
-- Name: t_repositories t_repositories_pkey; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_repositories
    ADD CONSTRAINT t_repositories_pkey PRIMARY KEY (id);


--
-- Name: t_repository_subscribes t_repository_subscribes_id_key; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_repository_subscribes
    ADD CONSTRAINT t_repository_subscribes_id_key UNIQUE (id);


--
-- Name: t_repository_subscribes t_repository_subscribes_pkey; Type: CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_repository_subscribes
    ADD CONSTRAINT t_repository_subscribes_pkey PRIMARY KEY (id);


--
-- Name: t_aliases t_aliases_t_git_sources_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_aliases
    ADD CONSTRAINT t_aliases_t_git_sources_id_fk FOREIGN KEY (git_source_id) REFERENCES public.t_git_sources(id) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: t_aliases t_aliases_t_messenger_type_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_aliases
    ADD CONSTRAINT t_aliases_t_messenger_type_id_fk FOREIGN KEY (messenger_type_id) REFERENCES public.t_messenger_type(id) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: t_repositories t_repositories_t_git_sources_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_repositories
    ADD CONSTRAINT t_repositories_t_git_sources_id_fk FOREIGN KEY (git_source_id) REFERENCES public.t_git_sources(id) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: t_repository_subscribes t_repository_subscribes_t_messenger_type_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_repository_subscribes
    ADD CONSTRAINT t_repository_subscribes_t_messenger_type_id_fk FOREIGN KEY (messenger_type_id) REFERENCES public.t_messenger_type(id) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- Name: t_repository_subscribes t_repository_subscribes_t_repositories_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: bit_admin
--

ALTER TABLE ONLY public.t_repository_subscribes
    ADD CONSTRAINT t_repository_subscribes_t_repositories_id_fk FOREIGN KEY (repository_id) REFERENCES public.t_repositories(id) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;


--
-- PostgreSQL database dump complete
--


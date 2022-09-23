CREATE TABLE investigations_entity (
	id varchar NOT NULL,
	description varchar(1000) NULL,
	created_date timestamp NOT NULL,
	updated_date timestamp NULL,
	created_by varchar NOT NULL,
	updated_by varchar NULL,
	is_deleted bool NOT NULL DEFAULT false,
	"type" varchar NULL,
	CONSTRAINT investigations_pk PRIMARY KEY (id)
);

CREATE TABLE notification_entity (
	id varchar NOT NULL,
	investigations_id varchar NOT NULL,
	status varchar NOT NULL,
	created_date timestamp NOT NULL,
	updated_date timestamp NULL,
	created_by varchar NOT NULL,
	updated_by varchar NULL,
	bpn_number varchar NULL,
	edc_url varchar NULL,
	contract_agreement_id varchar NULL,
	notification_reference_id varchar NULL,
	CONSTRAINT investigations_parts_pk PRIMARY KEY (id)
);

ALTER TABLE notification_entity ADD CONSTRAINT investigations_parts_fk FOREIGN KEY (investigations_id) REFERENCES investigations_entity(id);


CREATE TABLE notification_entity_parts_entity (
	notification_entity_id varchar(255) NOT NULL,
	part_id varchar(255) NULL
);

ALTER TABLE notification_entity_parts_entity ADD CONSTRAINT fk_notification_entity FOREIGN KEY (notification_entity_id) REFERENCES notification_entity(id);

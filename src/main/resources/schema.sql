CREATE TABLE if not exists invoice(
	id		BIGSERIAL PRIMARY KEY NOT NULL,
	user_id varchar(255) NOT NULL,
	pdf_url varchar(500) NOT NULL,
	amount integer NOT NULL,
	in_month varchar(3) NOT NULL,
	created_date timestamp NOT NULL,
	last_modified_date timestamp NOT NULL,
	version integer NOT NULL	

);
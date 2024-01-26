drop database if exists bedandbreakfast;
create database bedandbreakfast;
use bedandbreakfast;

create table users (
	email varchar(128),
    name varchar(128),
    primary key (email)
);

create table bookings (
	booking_id char(8),
    listing_id varchar(20),
    duration int,
    email varchar(128),
    primary key (booking_id),
    foreign key(email) references users(email)
);

create table reviews (
	id int auto_increment,
    date timestamp default current_timestamp,
    listing_id varchar(20),
    reviewer_name varchar(64),
    comments text,
    primary key (id)
);

LOAD DATA INFILE '/var/lib/mysql-files/users.csv'
INTO TABLE bedandbreakfast.users
FIELDS TERMINATED BY ',';
    
select * from users;

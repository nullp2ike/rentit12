truncate users cascade;
truncate authorities cascade;
truncate assignments cascade;
truncate plant cascade;
truncate purchase_order cascade;

insert into users VALUES (0, 't', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'user@rentit.com', 0);
insert into users VALUES (1, 't', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'admin', 0);

insert into authorities VALUES (0, 'ROLE_USER', 0);
insert into authorities VALUES (1, 'ROLE_ADMIN', 0);

insert into assignments VALUES(0, 0, 0, 0);
insert into assignments VALUES(1, 0, 1, 1);

insert into plant VALUES(0, 'Nice', 'Tractor', 100, 0);
insert into plant VALUES(1, 'Old', 'Truck', 2000, 0);



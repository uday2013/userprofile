CREATE TABLE user_profile (
  id         BIGINT(20) NOT NULL AUTO_INCREMENT,
  first_name VARCHAR (20) NOT NULL,
  last_name  VARCHAR (20) NOT NULL,
  email      VARCHAR (255) NOT NULL,

  PRIMARY KEY (id),
  UNIQUE KEY (email)
) ENGINE = innodb DEFAULT CHARSET = utf8;
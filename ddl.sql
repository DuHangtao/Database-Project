BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS `Streaming_Service` (
	`Vendor_ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`Name`	VARCHAR ( 10 ),
	`URL`	VARCHAR ( 10 )
);
CREATE TABLE IF NOT EXISTS `Theatre` (
	`Vendor_ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`Name`	VARCHAR ( 10 ),
	`Street`	VARCHAR ( 10 ),
	`City`	VARCHAR ( 10 ),
	`State`	VARCHAR ( 10 ),
	`Zip`	INTEGER,
	`Country`	VARCHAR ( 10 )
);
CREATE TABLE IF NOT EXISTS `Studio` (
	`Studio_ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`Name`	INTEGER
);
CREATE TABLE IF NOT EXISTS `Genre` (
	`Genre_ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`Name`	INTEGER
);
CREATE TABLE IF NOT EXISTS `Users` (
	`ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`Email`	VARCHAR ( 10 ),
	`Password`	VARCHAR ( 10 ),
	`First_name`	VARCHAR ( 10 ),
	`Last_name`	VARCHAR ( 10 ),
	`Profile_picture`	BLOB,
	`Street`	VARCHAR ( 10 ),
	`City`	VARCHAR ( 10 ),
	`State`	VARCHAR ( 10 ),
	`Postal_code`	INTEGER,
	`Country`	VARCHAR ( 10 )
);
CREATE TABLE IF NOT EXISTS `Movie` (
	`ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`Cover_image`	BLOB,
	`Name`	VARCHAR ( 10 ),
	`Release_date`	VARCHAR ( 10 ),
	`Genre_ID`	INTEGER,
	`Studio_ID`	INTEGER,
	FOREIGN KEY(`Genre_ID`) REFERENCES `Genre`(`Genre_ID`),
	FOREIGN KEY(`Studio_ID`) REFERENCES `Studio`(`Studio_ID`)
);
CREATE TABLE IF NOT EXISTS `User_favourite_movie` (
	`User_ID`	INTEGER,
	`Movie_ID`	INTEGER,
	PRIMARY KEY(`User_ID`,`Movie_ID`),
	FOREIGN KEY(`Movie_ID`) REFERENCES `Movie`(`ID`),
	FOREIGN KEY(`User_ID`) REFERENCES `Users`(`ID`)
);
CREATE TABLE IF NOT EXISTS `Method` (
	`Method_ID`	INTEGER PRIMARY KEY AUTOINCREMENT
);
CREATE TABLE IF NOT EXISTS `Orders` (
	`Confirmation_number`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`User_ID`	INTEGER,
	`Movie_ID`	INTEGER,
	`Date`	DATETIME,
	`Dollar_amount`	INTEGER,
	`Method_ID`	INTEGER,
	FOREIGN KEY(`Method_ID`) REFERENCES `Method`(`Method_ID`),
	FOREIGN KEY(`Movie_ID`) REFERENCES `Movie`(`ID`),
	FOREIGN KEY(`User_ID`) REFERENCES `Users`(`ID`)
);
CREATE TABLE IF NOT EXISTS `Phone` (
	`User_ID`	INTEGER,
	`Phone_number`	INTEGER,
	FOREIGN KEY(`User_ID`) REFERENCES `Users`(`ID`),
	PRIMARY KEY(`Phone_number`,`User_ID`)
);
CREATE TABLE IF NOT EXISTS `Movie_Showing` (
	`Showing_ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`Start_Time`	VARCHAR ( 10 ),
	`Theatre_Number`	INTEGER,
	`Vendor_ID`	INTEGER,
	`Movie_ID`	INTEGER,
	FOREIGN KEY(`Vendor_ID`) REFERENCES `Theatre`(`Vendor_ID`),
	FOREIGN KEY(`Movie_ID`) REFERENCES `Movie`(`ID`)
);
CREATE TABLE IF NOT EXISTS `Ticket` (
	`Method_ID`	INTEGER,
	`Seat`	VARCHAR ( 10 ),
	`Showing_ID`	INTEGER,
	FOREIGN KEY(`Showing_ID`) REFERENCES `Movie_Showing`(`Showing_ID`),
	PRIMARY KEY(`Method_ID`),
	FOREIGN KEY(`Method_ID`) REFERENCES `Method`(`Method_ID`)
);
CREATE TABLE IF NOT EXISTS `Stream` (
	`Method_ID`	INTEGER,
	`URL`	VARCHAR ( 10 ),
	`End_Time`	VARCHAR ( 10 ),
	`Movie_ID`	INTEGER,
	`Streaming_Service_ID`	INTEGER,
	FOREIGN KEY(`Movie_ID`) REFERENCES `Movie`(`ID`),
	FOREIGN KEY(`Streaming_Service_ID`) REFERENCES `Streaming_Service`(`Vendor_ID`),
	PRIMARY KEY(`Method_ID`),
	FOREIGN KEY(`Method_ID`) REFERENCES `Method`(`Method_ID`)
);
CREATE TABLE IF NOT EXISTS `Person` (
	`ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`Last_name`	VARCHAR ( 10 ),
	`First_name`	VARCHAR ( 10 ),
	`Date_of_birth`	VARCHAR ( 10 ),
	`Picture`	BLOB,
	`Gender`	VARCHAR ( 10 )
);
CREATE TABLE IF NOT EXISTS `Movie_photo` (
	`Image_ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`Movie_ID`	INTEGER,
	`Movie_Image`	BLOB,
	FOREIGN KEY(`Movie_ID`) REFERENCES `Movie`(`ID`)
);
CREATE TABLE IF NOT EXISTS `Producer` (
	`Movie_ID`	INTEGER,
	`Person_ID`	INTEGER,
	PRIMARY KEY(`Movie_ID`,`Person_ID`),
	FOREIGN KEY(`Movie_ID`) REFERENCES `Movie`(`ID`),
	FOREIGN KEY(`Person_ID`) REFERENCES `Person`(`ID`)
);
CREATE TABLE IF NOT EXISTS `Director` (
	`Movie_ID`	INTEGER,
	`Person_Id`	INTEGER,
	FOREIGN KEY(`Movie_ID`) REFERENCES `Movie`(`ID`),
	PRIMARY KEY(`Movie_ID`,`Person_Id`),
	FOREIGN KEY(`Person_Id`) REFERENCES `Person`(`ID`)
);

CREATE TABLE IF NOT EXISTS `Actor` (
	`Person_ID`	INTEGER,
	`Movie_ID`	INTEGER,
	FOREIGN KEY(`Person_ID`) REFERENCES `Person`(`ID`),
	PRIMARY KEY(`Person_ID`,`Movie_ID`),
	FOREIGN KEY(`Movie_ID`) REFERENCES `Movie`(`ID`)
);
CREATE TABLE IF NOT EXISTS `Characters` (
	`Person_ID`	INTEGER,
	`Movie_ID`	INTEGER,
	`Character_ID`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`Name`	VARCHAR ( 10 ),
	`Picture`	BLOB,
	FOREIGN KEY(`Movie_ID`) REFERENCES `Movie`(`ID`),
	FOREIGN KEY(`Person_ID`) REFERENCES `Person`(`ID`)
);
COMMIT;

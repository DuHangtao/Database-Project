
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Scanner;


public class DatabaseApp {

  public static void main(String[] args) throws ClassNotFoundException {

    // Using SQLite or MariaDB?
    final String PARAM;
    PARAM = "databaseApp.sqlite";

    // load the sqlite-JDBC driver using the current class loader
    // make the connection URI, based on DBMS
    final String CONN_URI;
    Class.forName("org.sqlite.JDBC");
    final SQLiteConfig config = new SQLiteConfig();
    config.setReadOnly(true);
    CONN_URI = ("jdbc:sqlite:" + PARAM);

    // using try-with-resource automatically closes
    // connection to the database (and input scanner)
    try (final Connection connection = DriverManager.getConnection(CONN_URI);
         final Scanner input = new Scanner(System.in);) {

      //does the person have access to the database?
      //becomes true if they put in a valid username/password combination
      boolean access = false;
      //does the user want to stop?
      //becomes true if user presses "-1"
      boolean stop = false;

      //option to log in or register
      if (args.length != 1) {
        System.out.println("Welcome to QwikTix!");
        System.out.printf("Click 1 to log in, click 2 to register: ");
        final String option = input.nextLine();

        //register new user
        if (option.equals("2")) {
          //generate sql
          PreparedStatement statement = connection.prepareStatement(
                  "INSERT INTO Users(Email, Password, First_name, Last_name, " +
                          "Profile_picture, Street, City, State, Postal_code, Country)" +
                          "values(?,?,?,?,?,?,?,?,?,?)");
          //get inputs and bind parameters
          System.out.printf("Please enter user's email: ");
          String email = input.nextLine();
          statement.setString(1, email);
          System.out.printf("Please enter user's password: ");
          String password = input.nextLine();
          String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
          statement.setString(2, hashed);
          System.out.printf("Please enter user's first name: ");
          String firstName = input.nextLine();
          statement.setString(3, firstName);
          System.out.printf("Please enter user's last name: ");
          String lastName = input.nextLine();
          statement.setString(4, lastName);
          System.out.printf("Please enter user's profile picture: ");
          String profilePicture = input.nextLine();
          statement.setString(5, profilePicture);
          System.out.printf("Please enter user's street: ");
          String street = input.nextLine();
          statement.setString(6, street);
          System.out.printf("Please enter user's city: ");
          String city = input.nextLine();
          statement.setString(7, city);
          System.out.printf("Please enter user's state: ");
          String state = input.nextLine();
          statement.setString(8, state);
          System.out.printf("Please enter user's postal code: ");
          String postalCode = input.nextLine();
          statement.setString(9, postalCode);
          System.out.printf("Please enter user's country: ");
          String country = input.nextLine();
          statement.setString(10, country);

          System.out.println("Welcome, " + firstName + "!");
          access = true;
          //execute
          statement.executeUpdate();

          //get user id
          PreparedStatement id = connection.prepareStatement(
                  "SELECT u.ID FROM Users u WHERE u.email = ?");
          id.setString(1, email);
          final ResultSet res = id.executeQuery();
          System.out.println("ID: " + res.getString("ID"));
        } else if (option.equals("1")) {

          //user log in

          //how many attempts have been made to log in
          int attempts = 0;

          while (!access) {
            //generate sql
            PreparedStatement logInQuery = connection.prepareStatement(
                    "SELECT u.email AS email, u.password AS password FROM Users u WHERE u.email = ?");
            //get inputs, bind parameters
            System.out.printf("Enter your email: ");
            String user = input.nextLine();
            System.out.printf("Enter your password: ");
            logInQuery.setString(1, user);
            String candidate = input.nextLine();

            // get results
           try (ResultSet res = logInQuery.executeQuery()) {
              String hashed = res.getString("password");
              //username/password matched table
              if (BCrypt.checkpw(candidate, hashed)) {
                //access granted
                access = true;
                System.out.println();
                System.out.println("Welcome!");

                //get user id
                PreparedStatement id = connection.prepareStatement(
                        "SELECT u.ID FROM Users u WHERE u.email = ?");
                id.setString(1, user);
                final ResultSet re = id.executeQuery();
                System.out.println("ID: " + re.getString("ID"));
                //can only attempt to log in a certain number of times
              } else if (attempts < 5) {
                System.out.println("User not found");
                attempts++;
              } else {
                System.out.println("You've been locked out");
                break;
              }
            } catch(SQLException e) {
             System.out.println("Invalid log in info");
             System.exit(0);
           }
          }
        }

        //correct username/password
        if (access) {

          if (args.length != 1) {
            //Print query options
            System.out.printf("Usage: java %s <%s>\n", DatabaseApp.class.getCanonicalName(), PARAM);
            //tasks
            System.out.printf("a) Register a new user\n");
            System.out.printf("b) Record that a user loves a movie\n");
            System.out.printf("c) Order a ticket from a local theatre\n");
            System.out.printf("d) Credit an existing actress for a movie\n");
            System.out.printf("e) Provide a ranked list of revenue generated from the top-10 studios\n");
            System.out.printf("f) Find all movies directed by a person (supplied via last name)\n");
            System.out.printf("g) Load the cover images and names of movies ordered by a particular user\n");
            System.out.printf("h) Find all movies released this year that a user loves but has not ordered\n");
            System.out.printf("i) Find all people (name, picture, and role) credited for a particular movie (supplied by name)\n");
            System.out.printf("j) Provide a ranked list of revenue generated from the top-3 movie genres\n");
            //complex queries
            System.out.printf("c1) Find all movie showings in which at least two tickets were sold\n");
            System.out.printf("c2) Find top-revenue movies by theater for a specific year\n");
            System.out.printf("c3) Find the sales information for a movie, both streams and tickets\n");
            System.out.printf("c4) Find information for all people associated with movies the user has seen\n");
            System.out.printf("c5) Find the top 5 most favorited genres\n");
            System.out.printf("Enter -1 to end program\n");
            System.out.println();
            //System.exit(0);
          }
          while (!stop) {
            // get input(s)
            System.out.printf("Which query do you want to do: ");
            final String param = input.nextLine();

            if (param.equals("a")) {
              PreparedStatement statement = connection.prepareStatement(
                      "INSERT INTO Users(Email, Password, First_name, Last_name, " +
                              "Profile_picture, Street, City, State, Postal_code, Country)" +
                              "values(?,?,?,?,?,?,?,?,?,?)");
              //get inputs and bind parameters
              System.out.printf("Please enter user's email: ");
              String email = input.nextLine();
              statement.setString(1, email);
              System.out.printf("Please enter user's password: ");
              String password = input.nextLine();
              String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
              statement.setString(2, hashed);
              System.out.printf("Please enter user's first name: ");
              String firstName = input.nextLine();
              statement.setString(3, firstName);
              System.out.printf("Please enter user's last name: ");
              String lastName = input.nextLine();
              statement.setString(4, lastName);
              System.out.printf("Please enter user's profile picture: ");
              String profilePicture = input.nextLine();
              statement.setString(5, profilePicture);
              System.out.printf("Please enter user's street: ");
              String street = input.nextLine();
              statement.setString(6, street);
              System.out.printf("Please enter user's city: ");
              String city = input.nextLine();
              statement.setString(7, city);
              System.out.printf("Please enter user's state: ");
              String state = input.nextLine();
              statement.setString(8, state);
              System.out.printf("Please enter user's postal code: ");
              String postalCode = input.nextLine();
              statement.setString(9, postalCode);
              System.out.printf("Please enter user's country: ");
              String country = input.nextLine();
              statement.setString(10, country);

              System.out.println("Welcome, " + firstName + "!");
              //execute
              statement.executeUpdate();
              statement.close();
            }

            if (param.equals("b")) {
              //print all movies and ID's
              PreparedStatement movies = connection.prepareStatement(
                      "SELECT m.Name, m.ID FROM Movie m\n" +
                              "ORDER BY m.ID");
              ResultSet res = movies.executeQuery();
              while (res.next()) {
                System.out.println("Movie: " + res.getString("Name") + ", " + "id: " + res.getString("ID"));
              }

              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "INSERT INTO User_favourite_movie(USER_ID, MOVIE_ID)" +
                              " VALUES(?, ?);");
              //get inputs, bind parameters
              System.out.printf("Please enter user's id: ");
              String userId = input.nextLine();
              statement.setString(1, userId);
              System.out.printf("Please enter movie's id: ");
              String movieId = input.nextLine();
              statement.setString(2, movieId);
              statement.executeUpdate();
              statement.close();

              //try (ResultSet r = statement.executeQuery()) {
              //  statement.executeUpdate();
              //  System.out.println("Executed successfully");
              //} catch(SQLException e) {
               // System.out.println("User already loves this movie");
              //  System.exit(0);
              //}


            } else if (param.equals("c")) {
              //generate sql for method
              PreparedStatement rowNumberStatement = connection.prepareStatement(
                      "select count(Method.Method_ID)  as total from Method");
              ResultSet res = rowNumberStatement.executeQuery();
              String rowNumber = res.getString("total");
              Integer number = Integer.parseInt(rowNumber) + 2;
              String rows = number.toString();

              PreparedStatement statement1 = connection.prepareStatement(
                      "INSERT INTO Method(Method_ID) VALUES(?)");


              statement1.setString(1, rows);

              //execute, altering method table
              statement1.executeUpdate();
              System.out.println("This is your method's id: " + rows + "\n");


              PreparedStatement movies = connection.prepareStatement(
                      "SELECT m.Name, m.ID FROM Movie m " +
                              "ORDER BY m.Name");
              ResultSet result = movies.executeQuery();
              System.out.println("Here is the information for all the movies");
              while (result.next()) {
                System.out.println("Movie: " + result.getString("Name") + ", " + "id: "
                        + result.getString("ID"));
              }
              System.out.println("\n");
              res.close();
              System.out.println("In order to make an order, please enter the person information\n");

              //generate sql for orders
              PreparedStatement statement2 = connection.prepareStatement(
                      "INSERT INTO Orders(User_ID,Movie_ID,Date," +
                              "Dollar_amount,Method_ID) VALUES(?, ?,?, 20, ?);");
              //get inputs, bind parameters

              System.out.printf("Please enter your user's id: ");
              String user_ID = input.nextLine();
              statement2.setString(1, user_ID);

              System.out.printf("Please enter the movie's id that you want to order: ");
              String movie_ID = input.nextLine();
              statement2.setString(2, movie_ID);

              statement2.setString(3, new Timestamp(System.currentTimeMillis()).toString());


              //
              statement2.setString(4, rows);

              //update orders table
              statement2.executeUpdate();

              //query for ticket table
              PreparedStatement statement3 = connection.prepareStatement(
                      "INSERT INTO Ticket(Method_ID,Seat,Showing_ID) VALUES(?, ?,?);");
              //get inputs, bind parameters

              statement3.setString(1, rows);

              System.out.printf("Seats' number available are:\n" +
                      "1A 1B 1C 1D 1E\n" +
                      "2A 2B 2C 2D 2E\n" +
                      "3A 3B 3C 3D 3E\n" +
                      "4A 4B 4C 4D 4E\n" +
                      "Please enter the seat number for the ticket: ");
              String seatNumber = input.nextLine();
              statement3.setString(2, seatNumber);

              PreparedStatement showingID = connection.prepareStatement(
                      "select m.Showing_ID as Showing_ID," +
                              "m.Start_Time as startTime, m.Vendor_ID as Vendor_ID " +
                              "from Movie_Showing m " +
                              "where m.Movie_ID = ?");
              showingID.setString(1, movie_ID);
              ResultSet resultSet = showingID.executeQuery();
              String showingId = resultSet.getString("Showing_ID");
              String startTime = resultSet.getString("startTime");
              String Vendor_ID = resultSet.getString("Vendor_ID");

              PreparedStatement theatre = connection.prepareStatement(
                      "select t.Name as tName " +
                              "from Theatre t " +
                              "where t.Vendor_ID = ?"
              );
              theatre.setString(1, Vendor_ID);
              ResultSet r = theatre.executeQuery();
              String tName = r.getString("tName");

              statement3.setString(3, showingId);

              //update ticket table
              statement3.executeUpdate();

              PreparedStatement order = connection.prepareStatement(
                      "Select o.confirmation_number as confirmationNumber, o.Date as date , " +
                              "o.Dollar_amount as totalMoney " +
                              "from Orders o " +
                              "where o.Method_ID = ? AND o.User_ID = ? AND o.Movie_ID = ?");
              order.setString(1, rows);
              order.setString(2, user_ID);
              order.setString(3, movie_ID);
              final ResultSet rs = order.executeQuery();
              String confirmation = rs.getString("confirmationNumber");
              String date = rs.getString("Date");
              String Dollar_amount = rs.getString("totalMoney");
              System.out.println("Here are some details about this order:\n" +
                      "Confirmation Number: " + confirmation + "\n" +
                      "Dollar Amount: " + Dollar_amount + "\n" +
                      "Order Time: " + date + "\n" +
                      "Theatre Name: " + tName + "\n" +
                      "Start Time: " + "Tomorrow " + startTime + "\n" +
                      "Seat Number: " + seatNumber);

              rs.close();

            }  else if (param.equals("d")) {
              //print all movies and ID's
              PreparedStatement movies = connection.prepareStatement(
                      "SELECT m.Name, m.ID FROM Movie m\n" +
                              "ORDER BY m.ID");
              ResultSet res = movies.executeQuery();
              while (res.next()) {
                System.out.println("Movie: " + res.getString("Name") + ", " + "id: " + res.getString("ID"));
              }
              System.out.println();
              //print all people and ID's
              PreparedStatement people = connection.prepareStatement(
                      "SELECT p.First_name AS first_Name, p.Last_name AS last_Name, p.ID FROM Person p ORDER BY p.ID");
              ResultSet re = people.executeQuery();
              while (re.next()) {
                System.out.println("Person: " + re.getString("first_Name") + " " + re.getString("last_Name") +
                        ", " + "id: " + re.getString("ID"));
              }
              //generate sql for actors table
              PreparedStatement statement1 = connection.prepareStatement(
                      "INSERT INTO Actor(Person_ID,Movie_ID) VALUES(?, ?);");
              //get inputs and bind parameters
              System.out.printf("Please enter the person id for this actor: ");
              String Person_ID = input.nextLine();
              statement1.setString(1, Person_ID);
              System.out.printf("Please enter the movie id that the actor acted in: ");
              String Movie_ID = input.nextLine();
              statement1.setString(2, Movie_ID);
              statement1.executeUpdate();

             // try (ResultSet combination = statement1.executeQuery()) {
                //update actors table
             //   statement1.executeUpdate();
             // } catch (SQLException e) {
             //   System.out.printf("Invalid: person already acts in this movie");
              //  System.exit(0);
              //}
              //generate sql for characters table
              PreparedStatement statement2 = connection.prepareStatement(
                      "INSERT INTO Characters(Person_ID,Movie_ID,Name,Picture) " +
                              "VALUES(?, ?, ?, ?);");
              //get inputs, bind parameters
              statement2.setString(1, Person_ID);
              statement2.setString(2, Movie_ID);
              System.out.printf("Please enter the Character's name that the actress played in the movie: ");
              String name = input.nextLine();
              statement2.setString(3, name);
              System.out.printf("Please enter the Character's picture that the actress act in the movie: ");
              String Picture = input.nextLine();
              statement2.setString(4, Picture);

              //update characters table
              statement2.executeUpdate();

              statement1.close();
              statement2.close();
            } else if (param.equals("e")) {
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "Select distinct s.name as studioName, sum(o.dollar_amount) as totalRevenue " +
                              "from Studio s\n" +
                              "join movie m on m.studio_id = s.studio_id " +
                              "join Movie_Showing ms on ms.movie_id = m.id " +
                              "join ticket t on t.showing_id = ms.showing_id " +
                              "join stream st on st.movie_id = m.id " +
                              "join Orders o on t.method_id = o.method_id or o.method_id = st.method_id " +
                              "group by 1 " +
                              "order by sum(o.dollar_amount) desc " +
                              "limit 10");

              //gets results
              final ResultSet rs = statement.executeQuery();
              int rankNumber = 0;
              //finds ranks and prints
              while (rs.next()) {
                rankNumber ++;
                String studioName = rs.getString("studioName");
                String totalRevenue = rs.getString("totalRevenue");
                String result = rankNumber +". Studio's name : " +  studioName +
                        " Total revenue: " + totalRevenue;
                System.out.println(result);
              }
              rs.close();

            } else if (param.equals("f")) {
              //print all people and ID's
              PreparedStatement people = connection.prepareStatement(
                      "SELECT p.First_name AS first_Name, p.Last_name AS last_Name, p.ID " +
                              "FROM Person p inner join Director d on d.Person_ID = p.ID\n");
              ResultSet re = people.executeQuery();
              while (re.next()) {
                System.out.println("Person: " + re.getString("first_Name") + " " + re.getString("last_Name") +
                        ", " + "id: " + re.getString("ID"));
              }
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "SELECT m.Name AS Movie_Name\n" +
                              "FROM Movie m INNER JOIN Director d\n" +
                              "ON m.ID = d.Movie_Id INNER JOIN Person p\n" +
                              "ON d.Person_id = p.ID\n" +
                              "WHERE p.Last_name = ?"
              );
              //get inputs, bind parameters
              System.out.printf("Please enter the person's last name: ");
              String Person_Last_Name = input.nextLine();
              statement.setString(1,Person_Last_Name);
              //get results
              final ResultSet rs = statement.executeQuery();
              int number = 0;
              while (rs.next()) {
                number++;
                String result = number +". Movie's name: "+ rs.getString("Movie_Name") ;
                System.out.println(result);
              }
              rs.close();
            } else if (param.equals("g")) {
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "Select distinct m.name AS movie, m.cover_image AS image "
                              + "from movie m join Movie_Showing ms on ms.movie_id = m.id "
                              + "join ticket t on t.showing_id = ms.showing_id "
                              + "join stream s on s.movie_id = m.id "
                              + "join Orders o on t.method_id = o.method_id or o.method_id = s.method_id "
                              + "join Users u on u.id = o.user_id "
                              + "where u.first_name = ? and u.last_name = ?;");
              //get inputs, bind parameters
              System.out.printf("Please enter user's first name: ");
              String firstName = input.nextLine();
              statement.setString(1, firstName);
              System.out.printf("Please enter user's last name: ");
              String lastName = input.nextLine();
              statement.setString(2,  lastName);

              //prepare statement
              final ResultSet res = statement.executeQuery();
              //get results
              while (res.next()) {
                System.out.println(res.getString("movie") + " " + res.getString("image"));
              }
              res.close();
            } else if (param.equals("h")) {
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "Select distinct m.name AS movie, m.release_date AS release_date\n" +
                              "from movie m\n" +
                              "join User_favourite_movie ufm on ufm.movie_id = m.id\n" +
                              "join Users u on u.id = ufm.user_id\n" +
                              "where u.first_name = ?\n" +
                              "and u.last_name = ?\n" +
                              "and m.release_date >= 2017\n" +
                              "and m.id not in (\n" +
                              "Select o.movie_id\n" +
                              "from Orders o\n" +
                              "join Users u on u.id = o.user_id\n" +
                              "where u.first_name = ?\n" +
                              "and u.last_name = ?);");
              //get inputs, bind parameters
              System.out.printf("Please enter user's first name: ");
              String firstName = input.nextLine();
              statement.setString(1, firstName);
              statement.setString(3,  firstName);
              System.out.printf("Please enter user's last name: ");
              String lastName = input.nextLine();
              statement.setString(2, lastName);
              statement.setString(4, lastName);

              //prepare statement
              final ResultSet res = statement.executeQuery();
              //get results
              while (res.next()) {
                System.out.println("Movie: " + res.getString("movie"));
                System.out.println("Release date: " + res.getString("release_date"));
                System.out.println();
              }
              res.close();

            } else if (param.equals("i")) {
              //print all movies and ID's
              PreparedStatement movies = connection.prepareStatement(
                      "SELECT m.Name, m.ID FROM Movie m\n" +
                              "ORDER BY m.ID");
              ResultSet r = movies.executeQuery();
              while (r.next()) {
                System.out.println("Movie: " + r.getString("Name") + ", " + "id: " + r.getString("ID"));
              }
              System.out.println();
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "Select distinct p.first_name AS first_name, p.last_name AS last_name,\n" +
                              "case \n" +
                              "when d.person_id = p.id then \"Director\"\n" +
                              "when pr.person_id = p.id then \"Producer\"\n" +
                              "when a.person_id = p.id then \"Actor\"\n" +
                              "end as roles\n" +
                              "from movie m\n" +
                              "join Director d on d.movie_id = m.id\n" +
                              "join Producer pr on pr.movie_id = m.id\n" +
                              "join Actor a on a.movie_id = m.id\n" +
                              "join Person p on p.id = a.person_id or p.id = d.person_id or p.id = pr.person_id\n" +
                              "where m.name = ?;");
              //get inputs, bind parameters
              System.out.printf("Please enter the movie name: ");
              String movie = input.nextLine();
              statement.setString(1, movie);
              //prepare statement
              final ResultSet res = statement.executeQuery();
              //get results
              while (res.next()) {
                System.out.println("Name: " + res.getString("first_name") + " " + res.getString("last_name"));
                System.out.println("Role: " + res.getString("roles"));
                System.out.println();
              }
              res.close();

            } else if (param.equals("j")) {
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "Select distinct g.name AS genre, sum(o.dollar_amount) AS amount\n" +
                              "from genre g\n" +
                              "join movie m on m.genre_id = g.genre_id\n" +
                              "join Movie_Showing ms on ms.movie_id = m.id\n" +
                              "join ticket t on t.showing_id = ms.showing_id\n" +
                              "join stream st on st.movie_id = m.id\n" +
                              "join Orders o on t.method_id = o.method_id or o.method_id = st.method_id\n" +
                              "group by 1\n" +
                              "order by sum(o.dollar_amount) desc\n" +
                              "limit 3");
              //prepare statement
              final ResultSet res = statement.executeQuery();
              //get results
              while (res.next()) {
                System.out.println("Genre: " + res.getString("genre") + ", Amount: $" + res.getString("amount"));
              }
            } else if (param.equals("c1")) {
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "SELECT m.Name AS Movie_Name, th.Name as Theater_Name,\n" +
                              "COUNT(ms.showing_id) AS Tickets\n" +
                              "FROM Movie m INNER JOIN Movie_Showing ms\n" +
                              "ON m.ID = ms.movie_Id INNER JOIN Ticket t\n" +
                              "ON ms.showing_Id = t.showing_Id INNER JOIN Theatre th\n" +
                              "ON ms.vendor_Id = th.vendor_Id\n" +
                              "GROUP BY ms.Showing_Id\n" +
                              "HAVING COUNT(ms.showing_Id) >= 2\n" +
                              "ORDER BY m.Name ASC, th.Name ASC;");
              //prepare statement
              final ResultSet res = statement.executeQuery();
              //get results
              while (res.next()) {
                System.out.println("Movie: " + res.getString("Movie_Name"));
                System.out.println("Theater: " + res.getString("Theater_Name"));
                System.out.println("Tickets sold: " + res.getString("Tickets"));
                System.out.println();
              }

            } else if (param.equals("c2")) {
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "Select distinct t.name AS theater, \n" +
                              "mo.name AS movie, \n" +
                              "\"$\" || sum(o.dollar_amount) revenue, \n" +
                              "count(t.method_id) tickets_sold\n" +
                              "from ticket t\n" +
                              "join orders o on o.method_id = t.method_id\n" +
                              "join movie_showing m on m.showing_id = t.showing_id\n" +
                              "join movie mo on mo.id = m.movie_id\n" +
                              "join theatre t on t.vendor_id = m.vendor_id\n" +
                              "where substr(o.date,1,4) = ? -- Parameterized by user input.\n" +
                              "group by 1,2\n" +
                              "having sum(o.dollar_amount) > 0\n" +
                              "order by 1 asc, 3 desc, 4 desc");
              //get inputs, bind parameters
              System.out.printf("Enter year: ");
              String year = input.nextLine();
              statement.setString(1, year);
              //prepare statement
              final ResultSet res = statement.executeQuery();
              //get results
              while (res.next()) {
                System.out.println("Theater: " + res.getString("theater"));
                System.out.println("Movie: " + res.getString("movie"));
                System.out.println("Revenue: " + res.getString("revenue"));
                System.out.println();
              }

            } else if (param.equals("c3")) {
              //print all movies and ID's
              PreparedStatement movies = connection.prepareStatement(
                      "SELECT m.Name, m.ID FROM Movie m\n" +
                              "ORDER BY m.ID");
              ResultSet r = movies.executeQuery();
              while (r.next()) {
                System.out.println("Movie: " + r.getString("Name") + ", " + "id: " + r.getString("ID"));
              }
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "with t1 as(\n" +
                              "Select m.name,\n" +
                              "case \n" +
                              "when o.method_id = t.method_id then 1\n" +
                              "else 0\n" +
                              "end as tickets,\n" +
                              "case \n" +
                              "when o.method_id = s.method_id then 1\n" +
                              "else 0\n" +
                              "end as streams\n" +
                              "from orders o\n" +
                              "join movie m on o.movie_id = m.id\n" +
                              "left outer join stream s on o.method_id = s.method_id\n" +
                              "left outer join ticket t on t.method_id = o.method_id\n" +
                              "where m.name = ?)--Parameterized by user input.\n" +
                              "select t1.name Movie_Name, sum(t1.tickets) Ticket_Sales, sum(t1.streams) Stream_Sales\n" +
                              "from t1\n" +
                              "group by 1\n" +
                              "order by sum(t1.tickets) + sum(t1.streams) desc, 1 asc\n" +
                              "");
              //get inputs, bind parameters
              System.out.printf("Enter movie: ");
              String movie = input.nextLine();
              statement.setString(1, movie);
              //prepare statement
              final ResultSet res = statement.executeQuery();
              //get results
              while (res.next()) {
                System.out.println("Movie: " + res.getString("Movie_Name"));
                System.out.println("Ticket sales: " + res.getString("Ticket_Sales"));
                System.out.println("Stream sales: " + res.getString("Stream_Sales"));
              }

            } else if (param.equals("c4")) {
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "Select distinct p.first_name, p.last_name,  cast(julianday('now') - julianday(p.date_of_birth) as int)/365 Age, \n" +
                              "case \n" +
                              "when d.person_id = p.id then \"Director\"\n" +
                              "when pr.person_id = p.id then \"Producer\"\n" +
                              "when a.person_id = p.id then \"Actor\"\n" +
                              "end as Roles, \n" +
                              "m.name MovieName, \n" +
                              "case\n" +
                              "when a.person_id = p.id then c.name\n" +
                              "else \"N/A\"\n" +
                              "end as CharacterName\n" +
                              "from movie m\n" +
                              "join Director d on d.movie_id = m.id\n" +
                              "join Producer pr on pr.movie_id = m.id\n" +
                              "join Actor a on a.movie_id = m.id\n" +
                              "join Characters c on a.person_id = c.person_id and a.movie_id = c.movie_id\n" +
                              "join Person p on p.id = a.person_id or p.id = d.person_id or p.id = pr.person_id\n" +
                              "where m.id in (\n" +
                              "select o.movie_id\n" +
                              "from orders o\n" +
                              "join ticket t on t.method_id = o.method_id\n" +
                              "join users u on u.id = o.user_id\n" +
                              "where u.first_name = ?\n" +
                              "and u.last_name = ?)\n" +
                              "order by 5 desc, 4 desc, 3 asc");
              //get inputs, bind parameters
              System.out.printf("Enter first name: ");
              String firstName = input.nextLine();
              statement.setString(1, firstName);
              System.out.printf("Enter last name: ");
              String lastName = input.nextLine();
              statement.setString(2, lastName);
              //prepare statement
              final ResultSet res = statement.executeQuery();
              //get results
              while (res.next()) {
                System.out.println("Name: " + res.getString("First_Name") + " " + res.getString("Last_Name"));
                System.out.println("Age: " + res.getString("Age"));
                System.out.println("Role: " + res.getString("Roles"));
                System.out.println("Movie: " + res.getString("MovieName"));
                System.out.println("Character: " + res.getString("CharacterName"));
                System.out.println();
              }
            } else if (param.equals("c5")) {
              //generate sql
              PreparedStatement statement = connection.prepareStatement(
                      "SELECT g.Name AS GenreName, COUNT(ufm.User_ID) as NumOfFavourite\n" +
                              "FROM Users u \n" +
                              "INNER JOIN User_favourite_movie ufm ON u.ID=ufm.User_ID\n" +
                              "INNER JOIN Movie m ON ufm.Movie_ID=m.ID\n" +
                              "INNER JOIN Genre g ON m.Genre_ID=g.Genre_ID\n" +
                              "GROUP BY g.Name\n" +
                              "HAVING Count(ufm.Movie_ID) > 0\n" +
                              "ORDER BY NumOfFavourite DESC\n" +
                              "LIMIT 5");
              //prepare statement
              final ResultSet res = statement.executeQuery();
              //get results
              while (res.next()) {
                System.out.println("Genre: " + res.getString("GenreName"));
                System.out.println("Favorites: " + res.getString("NumOfFavourite"));
                System.out.println();
              }

            } else if (param.equals("-1")) {
              stop = true;
              System.out.println("Hope you have a QwikTix day!");
            }
          }
        }
      }
    } catch (SQLException e) {
      System.out.printf("Error connecting to db: %s%n", e.getMessage());
      System.exit(0);
    }
  }
}






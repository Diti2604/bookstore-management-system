THIS PROJECT WAS A SCHOOL PROJECT IN THE SUBJECT OF OBJECT ORIENTED PROGRAMMING
BELOW YOU CAN FIND THE REQUIREMENTS OF THE PROJECTS, ONLY 1 CHANGE HAS BEEN MADE BY ME,
INSTEAD OF USING TEXT FILES AS THE TEACHER INITIALLY ASKED, I REMADE THIS PROJECT USING 
MYSQL DATABASE

BOOKSTORE
The focus of this software will be to manage all important steps of a Library. 
The software should keep data about all  the books in the library, such as ISBN of the book,
 title, category of the book, selling price, author, stock (an image of book cover is a bonus). 
At the same time,we should keep track of the sold products by
 their sold quantities, prices, and date of transaction. 
The application will have a three-level user system: Librarian, Manager and Administrator. 
Each will have different  views and usage of the software. Note that each of the users has 
a username and a password to enter in the software  (obviously a role as well). 


Librarian -> Has the right to check out books that a customer may need from the bookstore. 
This means that the  librarian should create a bill and enter the data of the bought books,
 such as ISBN of the book and its quantity. If the  book is out of stock or does not exist,
 give an alert to the librarian. The software should provide him the total amount  of the
 bill. The updates in the software file should be done  automatically by the database. 


Manager -> The manager has the right to supply the bookstore with the needed books. So, 
he/she can create new book category, authors, and/or add books of the same category to the
 stock of the bookstore. The manager should  be informed when entered in the system if there
 are less than 5 items of a book in the stock.  The same user may also check the performance
 of the librarians by checking the  books sold and the total amount  for a certain date 
 or between a certain period of time. Also, the statistics about the books sold should be
 provided to them whenever requested with daily, monthly and/or total filters. 


Administrator -> The administrator has the right to manage almost everything that Librarian
 and Manager does. Beside the above permissions, the admin has the right to manage the employees
 (Librarian and Manager), by registering, modifying, and deleting them. The data kept about employees 
include: name, birthday, phone, email, salary, access level, and other information about them. 
The software should also provide to the admin data about total incomes (total  of books sold) 
and total cost (total of items cost and staff salaries) during a period.

100% - Fully Working Software with JavaFX  
The software should be with JavaFX GUI, including a well-defined menu for each user access and properly working. 

 This means there should be no bugs, it should have a login system, and it should be easily manageable by the users of  the software. 
+20% - EXTRA Points  
You may take extra 20% of points if you can include better visualization of the statistics and extra 
useful features  not mentioned in the description. 
70% - Fully Working Software without JavaFX GUI 
The students who provide a solution without graphical user interface, may take up to 70% of the
 points of the project  if the software fulfills all the requirements described above but implemented
 in a non-graphical environment. 
Note: The software should be using all features of Object-Oriented Programming, such as: Inheritance,
 Polymorphism,  Abstract Classes, Interfaces, Exceptions, File Handling, Anonymous Inner Classes, etc…
 The absence of such features  will give you a certain reduction in your total project points. 

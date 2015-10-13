!borrowed("1984 by George Orwell").

+!borrowed(Book) <- borrow(Book).

+borrowing(Book,Date)[source(percept)] <- +borrowed(Book); +returnDate(Book,Date); .print("I should return ", Book, " before day ", Date).

+!returned(Book) : .random(N) & N < 0.25 <- .print("returning ", Book); +returned(Book).
+!returned(Book) <- .wait(4000); !returned(Book).
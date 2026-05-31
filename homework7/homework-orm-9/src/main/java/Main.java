import model.Book;
import orm.core.OrmException;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);
            em.createTable(Book.class);

            List<Book> books = Arrays.asList(
                    new Book("Clean Code", "Robert Martin", 2008, true),
                    new Book("Clean Architecture", "Robert Martin", 2017, true),
                    new Book("Effective Java", "Joshua Bloch", 2018, true),
                    new Book("Java Concurrency in Practice", "Brian Goetz", 2006, false),
                    new Book("Refactoring", "Martin Fowler", 2018, true)
            );
            em.saveAll(books);

            System.out.println("Books by Robert Martin: " + em.findAllWhere(Book.class, "author", "Robert Martin"));

            Book first = books.get(0);
            first.setAvailable(false);
            System.out.println("Updated rows: " + em.update(first));

            Optional<Book> one = em.findOneWhere(Book.class, "title", "Effective Java");
            System.out.println("findOneWhere: " + one);

            System.out.println("Deleted rows: " + em.delete(books.get(3)));
            System.out.println("Count after delete: " + em.count(Book.class));
            System.out.println("Exists first id: " + em.existsById(Book.class, first.getId()));

            try {
                em.saveAll(Arrays.asList(
                        new Book("Valid Book", "Some Author", 2020, true),
                        new Book(null, "Broken Author", 2021, true)
                ));
            } catch (OrmException e) {
                System.out.println("saveAll rollback because: " + e.getMessage());
            }

            System.out.println("Count after failed batch: " + em.count(Book.class));
        }
    }
}

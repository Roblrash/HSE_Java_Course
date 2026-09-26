import model.Student;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);
            em.createTable(Student.class);

            Student first = new Student("Ivan Ivanov", 20, 4.5, LocalDate.of(2024, 9, 1));
            Student second = new Student("Maria Petrova", 19, 4.8, LocalDate.of(2023, 9, 1));
            Student third = new Student("Petr Sidorov", 21, 4.2, LocalDate.of(2022, 9, 1));

            Long firstId = em.save(first);
            em.save(second);
            em.save(third);

            Optional<Student> found = em.findById(Student.class, firstId);
            List<Student> all = em.findAll(Student.class);
            Optional<Student> missing = em.findById(Student.class, 999L);

            System.out.println("findById existing: " + found);
            System.out.println("findAll: " + all);
            System.out.println("findById missing: " + missing);
        }
    }
}

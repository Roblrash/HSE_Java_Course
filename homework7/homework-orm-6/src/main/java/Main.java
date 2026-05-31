import model.Student;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);
            em.createTable(Student.class);

            Student first = new Student("Ivan Ivanov", 20, 4.5);
            Student second = new Student("Maria Petrova", 19, 4.8);
            Student third = new Student("Petr Sidorov", 21, 4.2);

            Long firstId = em.save(first);
            Long secondId = em.save(second);
            Long thirdId = em.save(third);

            System.out.println("Generated ids:");
            System.out.println(firstId);
            System.out.println(secondId);
            System.out.println(thirdId);
        }
    }
}

import model.RegistrationForm;
import orm.core.validation.ValidationException;
import orm.core.validation.Violation;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);
            em.createTable(RegistrationForm.class);

            RegistrationForm valid = new RegistrationForm("student", "verySecretPassword", 20);
            Long id = em.save(valid);
            System.out.println("Saved valid form id: " + id);

            try {
                em.save(new RegistrationForm("  ", "short", 15));
            } catch (ValidationException e) {
                System.out.println("Invalid form violations:");
                for (Violation violation : e.getViolations()) {
                    System.out.println(violation);
                }
            }

            try {
                em.saveAll(Arrays.asList(
                        new RegistrationForm("first_user", "strongPassword1", 22),
                        new RegistrationForm("x", "weak", 17)
                ));
            } catch (ValidationException e) {
                System.out.println("Batch rollback violations:");
                for (Violation violation : e.getViolations()) {
                    System.out.println(violation);
                }
            }

            System.out.println("Count after failed batch: " + em.count(RegistrationForm.class));
        }
    }
}

package homework6;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CsvParser parser = new CsvParser();

        List<Person> people = Arrays.asList(
                new Person(1, "Ivan Ivanov", 25),
                new Person(2, "Maria Petrova", 30)
        );

        parser.saveToCsv("people.csv", people, Person.class);
        parser.saveToCsv("people.csv.gz", people, Person.class);
        parser.saveToCsv("people.zip", people, Person.class);

        List<Person> fromCsv = parser.parseFromCsv("people.csv", Person.class);
        List<Person> fromGzip = parser.parseFromCsv("people.csv.gz", Person.class);
        List<Person> fromZip = parser.parseFromCsv("people.zip", Person.class);

        System.out.println("People from CSV: " + fromCsv);
        System.out.println("People from GZIP: " + fromGzip);
        System.out.println("People from ZIP: " + fromZip);

        List<Employee> employees = Arrays.asList(
                new Employee(1, "Ivan Ivanov", new Address("Lenina 10", 42)),
                new Employee(2, "Maria Petrova", new Address("Pushkina 5", 7))
        );

        parser.saveToCsv("employees.csv", employees, Employee.class);
        List<Employee> fromEmployees = parser.parseFromCsv("employees.csv", Employee.class);
        System.out.println("Employees with nested address: " + fromEmployees);

        List<Course> courses = Arrays.asList(
                new Course("Java Basics", Arrays.asList("oop", "collections", "streams"), Arrays.asList("Gandalf", "Frodo")),
                new Course("Spring Basics", Arrays.asList("spring", "boot", "web"), Arrays.asList("Arathorn", "Legolas"))
        );

        parser.saveToCsv("courses.csv", courses, Course.class);
        List<Course> fromCourses = parser.parseFromCsv("courses.csv", Course.class);
        System.out.println("Courses with list fields: " + fromCourses);
    }

    public static class Person {
        private int id;

        @CsvName("full_name")
        private String name;

        private int age;

        public Person() {
        }

        public Person(int id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public static class Address {
        private String street;
        private int flat;

        public Address() {
        }

        public Address(String street, int flat) {
            this.street = street;
            this.flat = flat;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "street='" + street + '\'' +
                    ", flat=" + flat +
                    '}';
        }
    }

    public static class Employee {
        private int id;

        @CsvName("full_name")
        private String name;

        private Address address;

        public Employee() {
        }

        public Employee(int id, String name, Address address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", address=" + address +
                    '}';
        }
    }

    public static class Course {
        private String name;

        @CsvCollection(delimiter = ";")
        private List<String> tags;

        private List<String> authors;

        public Course() {
        }

        public Course(String name, List<String> tags, List<String> authors) {
            this.name = name;
            this.tags = tags;
            this.authors = authors;
        }

        @Override
        public String toString() {
            return "Course{" +
                    "name='" + name + '\'' +
                    ", tags=" + tags +
                    ", authors=" + authors +
                    '}';
        }
    }
}

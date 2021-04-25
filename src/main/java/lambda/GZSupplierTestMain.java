package lambda;

public class GZSupplierTestMain {

    private static class Student {
        private String name;
        private int age;

        public Student() {
            this.name = "name";
            this.age = 10;
        }

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static void main(String[] args) {
        GZSupplier<Integer> gzSupplier = () -> new Integer(11);
        System.out.println(gzSupplier.get());

        GZSupplier<Student> gzSupplier1 = Student::new;
        System.out.println("student name: " + gzSupplier1.get().name);
        System.out.println("student age: " + gzSupplier1.get().age);
    }
}

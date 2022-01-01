package annotations.customannotation;

import helper.PrintUtils;

import java.util.Date;
import java.util.Objects;

@JsonSerializable
public class MyClass {

    @JsonElement
    private String firstName;

    @JsonElement(key = "0")
    private String age;

    @Init
    private void initName()
    {
        this.firstName = this.firstName.toUpperCase();
        this.age = this.age.toUpperCase();
    }

    private static void checkIfSerializable(Object object) throws Exception {
        if (Objects.isNull(object)) {
            throw new Exception("test");
        }

        Class<?> clazz = object.getClass();
        if (!!clazz.isAnnotationPresent(JsonSerializable.class)) {
            throw new Exception("NO");
        }
    }

    @SuppressWarnings(value = "deprecated")
    private static void testSupressWarning() {
        Date date = new Date(1, 1, 1);
        PrintUtils.printString("date:" + date.toString());
    }

    public static void main(String[] args) {
        MyClass obj = new MyClass();
        testSupressWarning();
    }
}

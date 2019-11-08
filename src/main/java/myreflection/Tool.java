package myreflection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tool {

    // обнуляем значение поля примитивного типа
    private static void clearPrimitiveValue(Object o, Field field) throws IllegalAccessException {
        String name = field.getType().getName();
        if (name.equals("boolean")) field.setBoolean(o, false);
        else if (name.equals("char")) field.setChar(o, '\u0000');
        else if (name.equals("byte")) field.setByte(o, (byte) 0);
        else if (name.equals("short")) field.setShort(o, (short) 0);
        else if (name.equals("int")) field.setInt(o, 0);
        else if (name.equals("long")) field.setLong(o, 0L);
        else if (name.equals("float")) field.setFloat(o, 0.0f);
        else if (name.equals("double")) field.setDouble(o, 0.0);
        else throw new IllegalArgumentException("Unknown type: " + name);
    }

    // приводим к строке значение поля примитивного типа
    private static String primitiveToString(Object o, Field field) throws IllegalAccessException {
        String name = field.getType().getName();
        if (name.equals("boolean")) return String.valueOf(field.getBoolean(o));
        else if (name.equals("char")) return String.valueOf(field.getChar(o));
        else if (name.equals("byte")) return String.valueOf(field.getByte(o));
        else if (name.equals("short")) return String.valueOf(field.getShort(o));
        else if (name.equals("int")) return String.valueOf(field.getInt(o));
        else if (name.equals("long")) return String.valueOf(field.getLong(o));
        else if (name.equals("float")) return String.valueOf(field.getFloat(o));
        else if (name.equals("double")) return String.valueOf(field.getDouble(o));
        else throw new IllegalArgumentException("Unknown type: " + name);
    }

    // обнуляем или удаляем (для Map) поле
    // если поле недоступно (например private), то будет IllegalAccessException
    public static void clearField(Object o, String name) throws IllegalAccessException {
        if (o == null || name == null) throw new IllegalArgumentException();
        if (o instanceof Map) {
            // удаляем поле из Map
            Map map = (Map) o;
            if (!map.containsKey(name)) throw new IllegalArgumentException();
            map.remove(name);
        } else {
            // обнуляем поле через reflection
            try {
                Field field = o.getClass().getDeclaredField(name);
                // если такого поля нет, то будет NoSuchFieldException
                Class cls = field.getType();
                if (cls.isPrimitive()) {
                    // примитивный тип, задаем значение по умолчанию
                    clearPrimitiveValue(o, field);
                } else {
                    // задаем значение null
                    field.set(o, null);
                }
            } catch (NoSuchFieldException nsfe) {
                throw new IllegalArgumentException("no field: " + name);
            }
        }
    }

    // если значение null, то возвращаем строку "(null)" иначе выполняем toString()
    private static String objectToString(Object value) {
        return (value == null) ? "(null)" : value.toString();
    }

    // конвертируем значение поля к String
    // если поле недоступно (например private), то будет IllegalAccessException
    public static String fieldToString(Object o, String name) throws IllegalAccessException {
        if (o == null || name == null) throw new IllegalArgumentException();
        if (o instanceof Map) {
            // получаем поле из Map
            Map map = (Map) o;
            if (!map.containsKey(name)) throw new IllegalArgumentException();
            Object value = map.get(name);
            return objectToString(value);
        } else {
            try {
                Field field = o.getClass().getDeclaredField(name);
                // если такого поля нет, то будет NoSuchFieldException
                Class cls = field.getType();
                if (cls.isPrimitive()) {
                    // примитивный тип
                    return primitiveToString(o, field);
                } else {
                    // получаем значение и конвертируем в String
                    return objectToString(field.get(o));
                }
            } catch (NoSuchFieldException nsfe) {
                throw new IllegalArgumentException("no field: " + name);
            }
        }
    }


    // проверяем поле на наличие
    // если все нормально, то метод ни чего не возвращает
    // если поля нет, то выбрасывает IllegalArgumentException
    public static void checkField(Object o, String name) throws IllegalArgumentException, IllegalAccessException {
        if (o == null || name == null) throw new IllegalArgumentException();
        if (o instanceof Map) {
            // проверяем поле из Map
            Map map = (Map) o;
            if (!map.containsKey(name)) throw new IllegalArgumentException();
        } else {
            // проверяем поле в объекте
            try {
                Field field = o.getClass().getDeclaredField(name);
                // если такого поля нет, то будет NoSuchFieldException
            } catch (NoSuchFieldException nsfe) {
                // заменяем NoSuchFieldException на IllegalArgumentException
                throw new IllegalArgumentException("no field: " + name);
            }
        }
    }

    //
    //  object - объект над которым проводим действия
    //  fieldsToCleanup - список полей которые нужно обнулить
    //  fieldsToOutput - список полей которые нужно вывести в консоль
    //
    public static void cleanup(Object object, Set<String> fieldsToCleanup, Set<String> fieldsToOutput) throws IllegalArgumentException, IllegalAccessException {
        if (object == null || fieldsToCleanup == null || fieldsToOutput == null) throw new IllegalArgumentException();
        // проверим наличие полей чтобы при отсутствии поля выбросить IllegalArgumentException
        // до того как начнем менять значения полей объекта
        for (String name : fieldsToCleanup) {
            checkField(object, name);
        }
        for (String name : fieldsToOutput) {
            checkField(object, name);
        }

        // обнуление полей
        for (String name : fieldsToCleanup) {
            clearField(object, name);
        }
        // вывод полей
        StringBuffer sb = new StringBuffer();
        for (String name : fieldsToOutput) {
            sb.append(name).append(" = ").append(fieldToString(object, name)).append("\n");
        }

        System.out.println("Result:\n" + sb.toString());
    }


    public static void main(String[] args) throws Exception {
        Example example = new Example();

        System.out.println(example);
        cleanup(example,
                new HashSet<String>(Arrays.asList("intVal", "dblValue", "boolVal")),
                new HashSet<String>(Arrays.asList("byteVal", "charVal")));

        System.out.println(example);
    }
}

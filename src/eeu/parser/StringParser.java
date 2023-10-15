package eeu.parser;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Reflect;
import mindustry.Vars;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static eeu.parser.ContentClasses.classes;

public class StringParser {
    public static Object parse(String input) {
        if (!input.startsWith("$")) return input;
        String sub = input.substring(1);
        switch (sub.substring(0, 1)) {
            case "G" -> {
                return parseGet(sub.substring(1));
            }
            case "I" -> {
                return Integer.parseInt(sub.substring(1));
            }
            default -> {
                return input;
            }
        }
    }

    public static Object parseGet(String get) {
        Seq<String> seq = splitCleverly(get, ">");
        var Class = findClass(seq.remove(0));
        if (seq.isEmpty()) return Class;
        if (!seq.first().contains(".")) {
            return readPath(Class, seq.remove(0));
        }
        seq = splitCleverly(seq.remove(0), ".");
        var base = readPath(Class, seq.remove(0));
        for (var s : seq) {
            base = readPath(base, s);
        }
        return base;
    }

    public static Object readPath(Object base, String in) {
        try {
            Object f;
            if (base instanceof Class c) f = findField(c, in);
            else f = findField(base, in);
            if (f != null) return f;
        } catch (Exception e) {
            if (parse(in) instanceof Integer i) try {
                return Array.get(base, i);
            } catch (Exception e1) {
                if (base instanceof Seq s) {
                    return s.get(i);
                } else if (base instanceof ArrayList s) {
                    return s.get(i);
                }
            }
            else {
                try {
                    if (base instanceof ObjectMap a) {
                        return a.get(parse(in));
                    }
                    Object m;
                    if (base instanceof Class c) m = findMethod(c, in);
                    else m = findMethod(base, in);
                    return m;
                } catch (Exception e1) {
                    Log.info(e1.getMessage());
                }
            }
        }
        return null;
    }

    //cleverly split!
    public static Seq<String> splitCleverly(String input, String regex) {
        int leftAmount = 0;
        int pointRight;
        Seq<String> strings = new Seq<>();
        String read = input;
        int i = 0;
        while (!read.isEmpty() && i < read.length()) {
            String str = String.valueOf(read.charAt(i));
            switch (str) {
                case "[", "(" -> leftAmount++;
                case "]", ")" -> {
                    leftAmount--;
                    if (leftAmount == 0 && i == read.length() - 1) {
                        strings.add(read);
                        read = "";
                    }
                }
                default -> {
                    if (str.equals(regex) && leftAmount == 0) {
                        pointRight = i + 1;
                        strings.add(read.substring(0, pointRight - 1));
                        read = read.substring(pointRight);
                        i = -1;
                    }
                    if (i == read.length() - 1) strings.add(read);
                }
            }
            i++;
        }
        strings.replace(s -> {
            if (s.startsWith("(") && s.endsWith(")")) return s.substring(1, s.length() - 1);
            else return s;
        });
        return strings;
    }

    public static Class<?> findClass(String name) {
        if (classes.containsKey(name)) return classes.get(name);
        return Reflect.invoke(Vars.mods.mainLoader(), "findClass", new String[]{name}, new Class[]{String.class});
    }

    public static Object findField(Class<?> Class, String name) {
        Object o;
        try {
            o = Reflect.get(Class, name);
        } catch (Exception e) {
            try {
                o = Class.getField(name).get(null);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return o;
    }

    public static Object findField(Object obj, String name) {
        Object o;
        try {
            o = Reflect.get(obj, name);
        } catch (Exception e) {
            try {
                o = obj.getClass().getField(name).get(obj);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return o;
    }

    public static Object findMethod(Class<?> Class, String name, Object[] args, Class<?>... parameterType) {
        Object o;
        try {
            o = Reflect.invoke(Class, name, args, parameterType);
        } catch (Exception e) {
            try {
                o = Class.getMethod(name, parameterType).invoke(null, args);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
        return o;
    }

    public static Object findMethod(Object obj, String name) {
        Object o;
        try {
            o = Reflect.invoke(obj, name);
        } catch (Exception e) {
            try {
                o = obj.getClass().getMethod(name).invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
        return o;
    }

    public static Object findMethod(Class<?> Class, String name) {
        Object o;
        try {
            o = Reflect.invoke(Class, name);
        } catch (Exception e) {
            try {
                o = Class.getMethod(name).invoke(null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
        return o;
    }

    public static Object findMethod(Object obj, String name, Object[] args, Class<?>... parameterType) {
        Object o;
        try {
            o = Reflect.invoke(obj, name, args, parameterType);
        } catch (Exception e) {
            try {
                o = obj.getClass().getMethod(name, parameterType).invoke(obj, args);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
        return o;
    }
}

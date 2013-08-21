package charliek.dw.exceptions

class Validate {

    static void isNotNull(Object o, String msg) {
        if (o == null) {
            throw new ValidationException(msg)
        }
    }

    static void isNull(Object o, String msg) {
        if (o != null) {
            throw new ValidationException(msg)
        }
    }

    static void match(Object o1, Object o2, String msg) {
        if (o1 != o2) {
            throw new ValidationException(msg)
        }
    }

}

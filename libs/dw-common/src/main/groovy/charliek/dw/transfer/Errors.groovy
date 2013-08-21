package charliek.dw.transfer

class Errors {
    List<FieldError> fieldErrors = []
    List<ClassError> objectErrors = []

    Errors() {}

    Errors(String objectError) {
        objectErrors << objectError
    }
}

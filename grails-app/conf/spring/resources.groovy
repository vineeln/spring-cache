import com.spring.OncePerRequestExceptionResolver

// Place your Spring DSL code here
beans = {
    exceptionHandler(OncePerRequestExceptionResolver) {
        exceptionMappings = ['java.lang.Exception': '/error']
    }
}

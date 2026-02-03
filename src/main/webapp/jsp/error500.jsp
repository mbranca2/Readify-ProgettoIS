<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Errore 500 - Errore del server</title>
</head>
<body>
<h1>Si è verificato un errore</h1>
<p>Messaggio: ${pageContext.exception.message}</p>
<pre>
        <%
            java.io.StringWriter sw = new java.io.StringWriter();
            pageContext.getException().printStackTrace(new java.io.PrintWriter(sw));
            out.println(sw.toString());
        %>
    </pre>
</body>
</html>
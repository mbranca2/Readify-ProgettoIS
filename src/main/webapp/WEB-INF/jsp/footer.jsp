<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="now" class="java.util.Date"/>

<footer>
    <div class="container">
        <div class="footer-content">
            <div class="footer-contact">
                <h3>Contatti</h3>
                <p>Email: <a href="mailto:contatti@readify.example">contatti@readify.example</a></p>
                <p>Telefono: +39 02 1234 5678</p>
                <p>Sede: Via dei Libri 12, Milano</p>
            </div>
            <p class="footer-copy">&copy; <fmt:formatDate value="${now}" pattern="yyyy"/> Readify. Tutti i diritti
                riservati.</p>
        </div>
    </div>
</footer>

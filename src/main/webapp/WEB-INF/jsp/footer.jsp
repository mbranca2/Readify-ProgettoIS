<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="now" class="java.util.Date" />

<footer>
  <div class="container">
    <p>&copy; <fmt:formatDate value="${now}" pattern="yyyy" /> Readify. Tutti i diritti riservati.</p>
  </div>
</footer>

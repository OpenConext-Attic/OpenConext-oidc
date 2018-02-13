<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>Submit This Form</title></head>
<body onload="javascript:document.forms[0].submit()">
<form method="post" action="${redirect_uri}">
    <c:if test="${not empty state}">
        <input type="hidden" name="state" value="${state}"/>
    </c:if>
    <c:if test="${not empty access_token}">
        <input type="hidden" name="access_token" value="${access_token}"/>
    </c:if>
    <input type="hidden" name="id_token" value="${id_token}"/>
</form>
</body>
</html>
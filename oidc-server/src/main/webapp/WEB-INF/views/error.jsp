<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@page import="org.springframework.security.oauth2.common.exceptions.OAuth2Exception"%>
<%@page import="org.springframework.http.HttpStatus"%>

<%

if (request.getAttribute("error") != null && request.getAttribute("error") instanceof OAuth2Exception) {
	request.setAttribute("errorCode", ((OAuth2Exception)request.getAttribute("error")).getOAuth2ErrorCode());
	request.setAttribute("message", ((OAuth2Exception)request.getAttribute("error")).getClass().getSimpleName());
} else if (request.getAttribute("javax.servlet.error.exception") != null) {
	Throwable t = (Throwable)request.getAttribute("javax.servlet.error.exception");
	request.setAttribute("errorCode",  t.getClass().getSimpleName() + " (" + request.getAttribute("javax.servlet.error.status_code") + ")");
} else if (request.getAttribute("javax.servlet.error.status_code") != null) {
	Integer code = (Integer)request.getAttribute("javax.servlet.error.status_code");
	HttpStatus status = HttpStatus.valueOf(code);
	request.setAttribute("errorCode", status.toString() + " " + status.getReasonPhrase());
} else {
	request.setAttribute("errorCode", "Server error");
}

%>

<spring:message code="error.title" var="title"/>
<o:header title="${title}" />
<o:topbar pageName="Home" />
<div class="container-fluid main">
	<div class="row-fluid">
		<div class="span12">
			<div class="hero-unit">
                <spring:message code="error.message" var="headerMessage"/>
                <p>${headerMessage}</p>
                <pre><c:out value="${ errorCode }" /><c:if test="${not empty message}"><c:out value=" : ${ message }" /></c:if></pre>
                <spring:message code="error.logDetails" var="footerMessage"/>
                <p>${footerMessage}</p>
            </div>
		</div>
	</div>
</div>
<o:footer />

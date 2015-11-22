<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<h1><spring:message code="home.welcome.title"/></h1>

<p><spring:message code="home.welcome.body"/></p>

<security:authorize access="!hasRole('ROLE_ADMIN')">
    <p>
        <a class="btn btn-primary btn-large" href="saml/login"><i class="icon-lock icon-white"></i>
            <spring:message code="topbar.login"/></a>
    </p>
</security:authorize>



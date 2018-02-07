<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<h1><spring:message code="openconext.welcome.title"/></h1>

<p><spring:message code="openconext.welcome.body"/></p>

<security:authorize access="!hasRole('ROLE_ADMIN')">
    <p>
        <a class="btn btn-primary btn-large" href="saml/login?prompt=login&idp-single-sign-on=234353ee1e96b88f9fa5f488a235982b">
            <i class="icon-lock icon-white"></i>
            <spring:message code="topbar.login"/></a>
    </p>
</security:authorize>



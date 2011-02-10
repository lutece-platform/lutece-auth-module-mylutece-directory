<%@ page errorPage="../../../../ErrorPage.jsp" %>

<jsp:useBean id="myluteceDirectoryUser" scope="session" class="fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web.MyluteceDirectoryUserJspBean" />

<%
myluteceDirectoryUser.init( request, myluteceDirectoryUser.RIGHT_MANAGE_MYLUTECE_DIRECTORY_USERS );
   	response.sendRedirect( myluteceDirectoryUser.doAssignRoleUser( request ) ); 
%>

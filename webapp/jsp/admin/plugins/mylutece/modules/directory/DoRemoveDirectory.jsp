<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="myluteceDirectory" scope="session" class="fr.paris.lutece.plugins.mylutece.modules.directory.authentication.web.MyluteceDirectoryJspBean" />


<% 
myluteceDirectory.init( request, myluteceDirectory.RIGHT_MANAGE_MYLUTECE_DIRECTORY);
    response.sendRedirect( myluteceDirectory.doRemoveDirectory( request ) );
%>

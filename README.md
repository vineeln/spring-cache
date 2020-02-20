grails run-app

http://localhost:8080/console

run the follwing in console for testing spring-cache

  ctx.springCacheService.findCustomerName("some")


for testing recursive loop for error hit the following
 
  http://localhost:8080/home/viewWithException
  
  http://localhost:8080/home/errorAction
  
  
// 1. while rendering views is the control moving out of Controllers & into DispatchServlet ??
// 2. SpringFilters are being applied prior to starting the hibernate session ??
//      are filters executing first & then the dispatcher servlet ??
//      when is hibernateSession being opened..

1. Hibernate Session doesn't exist in Servlet Filters so withNewSession or @Transaction is necessary
2. Any objects loaded "withNewSession" in Servlet Filters may not be assoicated with Hibernate session in controller
3. How to minimize edits on Member Object & its collections.
4. Views are rendered outside the scope of controllers, so an @Transaction annotation at controller level does not continue into a view.
5. Without a Transaction @ Controller scope we seem to to "dirtyCheck, flush & commit" at the end of each Transaction in Controller. 
6. Changes in Controller without @Transaction do not get persisted without a "flush:true"
7. StaleObject exception 
  -- @DynamicUpdate without a version on domain is that a valid option https://thoughts-on-java.org/hibernate-tips-exclude-unchanged-columns-generated-update-statements/
  -- MemberValue
8. Q. Would adding a child object mark parent as dirty ??  

9. CookieTheft is it happening to all requests or just for the ones where we do not have an authenticated session.
grails run-app

http://localhost:8080/console

run the follwing in console for testing spring-cache

  ctx.springCacheService.findCustomerName("some")


for testing recursive loop for error hit the following
 
  http://localhost:8080/home/viewWithException
  
  http://localhost:8080/home/errorAction

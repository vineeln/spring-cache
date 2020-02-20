package com.spring

import org.springframework.web.filter.OncePerRequestFilter
import spring.SpringCacheService

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomSimpleFilter extends OncePerRequestFilter {

    SpringCacheService springCacheService

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String context="filter: simpleFilter"
        def user = springCacheService.findLoggedInUser(context)
        CustomUtil.isAttached(context,user)

        filterChain.doFilter(request, response);
    }
}

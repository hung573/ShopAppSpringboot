///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package ShopApp.components;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
///**
// *
// * @author mac
// */
//@Aspect
//@Component
//public class UserActivityLogger {
//    //named pointcut
//    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
//    public void controllerMethods() {};
//    
//    
//    @Around("controllerMethods() && execution(* ShopApp.controllers.UserController.*(..))")
//    public Object logUserActivity(ProceedingJoinPoint joinPoint) throws Throwable {
//        // Ghi log trước khi thực hiện method
//        String methodName = joinPoint.getSignature().getName();
//        String remoteAddress = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//                .getRequest().getRemoteAddr();
//        System.out.println(methodName);
//        // Thực hiện method gốc
//        Object result = joinPoint.proceed();
//        // Ghi log sau khi thực hiện method
//        return result;
//    }
//}

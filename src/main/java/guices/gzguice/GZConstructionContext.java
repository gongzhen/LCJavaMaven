package guices.gzguice;

import com.google.inject.internal.*;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

final class GZConstructionContext<T> {
//    T currentReference;
//    boolean constructing;
//
//    List<DelegatingInvocationHandler<T>> invocationHandlers;
//
//    public T getCurrentReference() {
//        return currentReference;
//    }
//
//    public void removeCurrentReference() {
//        this.currentReference = null;
//    }
//
//    public void setCurrentReference(T currentReference) {
//        this.currentReference = currentReference;
//    }
//
//    public boolean isConstructing() {
//        return constructing;
//    }
//
//    public void startConstruction() {
//        this.constructing = true;
//    }
//
//    public void finishConstruction() {
//        this.constructing = false;
//        invocationHandlers = null;
//    }
//
//    public Object createProxy(Errors errors, Class<?> expectedType) throws ErrorsException {
//        // TODO: if I create a proxy which implements all the interfaces of
//        // the implementation type, I'll be able to get away with one proxy
//        // instance (as opposed to one per caller).
//
//        if (!expectedType.isInterface()) {
//            throw errors.cannotSatisfyCircularDependency(expectedType).toException();
//        }
//
//        if (invocationHandlers == null) {
//            invocationHandlers = new ArrayList<DelegatingInvocationHandler<T>>();
//        }
//
//        DelegatingInvocationHandler<T> invocationHandler = new DelegatingInvocationHandler<T>();
//        invocationHandlers.add(invocationHandler);
//
//        ClassLoader classLoader = BytecodeGen.getClassLoader(expectedType);
//        return expectedType.cast(Proxy.newProxyInstance(classLoader,
//                new Class[] { expectedType, CircularDependencyProxy.class }, invocationHandler));
//    }
//
//    public void setProxyDelegates(T delegate) {
//        if (invocationHandlers != null) {
//            for (DelegatingInvocationHandler<T> handler : invocationHandlers) {
//                handler.setDelegate(delegate);
//            }
//        }
//    }
}

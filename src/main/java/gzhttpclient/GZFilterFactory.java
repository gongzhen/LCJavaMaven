package gzhttpclient;

import java.util.Iterator;
import java.util.LinkedList;

public class GZFilterFactory {

    final LinkedList<Class<? extends GZHeaderFilter>> filterClasses = new LinkedList();

    LinkedList<GZHeaderFilter> getFilterChain() {
        LinkedList<GZHeaderFilter> l = new LinkedList();
        Iterator var2 = this.filterClasses.iterator();

        while(var2.hasNext()) {
            Class clazz = (Class)var2.next();

            try {
                GZHeaderFilter headerFilter = (GZHeaderFilter)clazz.getConstructor().newInstance();
                l.add(headerFilter);
            } catch (ReflectiveOperationException var5) {
                throw new InternalError(var5);
            }
        }

        return l;
    }
}

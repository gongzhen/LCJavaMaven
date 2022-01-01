package guices.gzguicemock;

import com.google.common.base.Preconditions;

import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface FourEyesSupplierNames {

    class Impl implements FourEyesSupplierNames {

        protected Impl(String supplier)
        {
            this.supplier_ = Preconditions.checkNotNull(supplier, "piece can't be null");
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return FourEyesSupplierNames.class;
        }

        private final String supplier_;
    }
}

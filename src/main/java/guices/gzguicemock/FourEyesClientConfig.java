package guices.gzguicemock;

import com.google.common.base.Preconditions;

import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER})
public @interface FourEyesClientConfig {
    Piece value();

    enum Piece
    {
        MOLTEN_SUPPLIER_NAME
    }

    class Impl implements FourEyesClientConfig
    {
        protected Impl(Piece piece)
        {
            this.piece_ = Preconditions.checkNotNull(piece, "piece can't be null");
        }

        public static FourEyesClientConfig fourEyesClientConfig(Piece piece)
        {
            return new Impl(piece);
        }

        @Override
        public Piece value()
        {
            return piece_;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return FourEyesClientConfig.class;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }

            if (!(o instanceof FourEyesClientConfig)) {
                return false;
            }

            FourEyesClientConfig other = (FourEyesClientConfig) o;

            return piece_ == other.value();
        }

        @Override
        public int hashCode()
        {
            // This is specified in java.lang.Annotation.
            return ((127 * "value".hashCode()) ^ piece_.hashCode());
        }

        private final Piece piece_;

    }
}

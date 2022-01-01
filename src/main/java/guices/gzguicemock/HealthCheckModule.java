package guices.gzguicemock;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import java.util.EnumMap;
import java.util.Map;

import static com.google.inject.Scopes.SINGLETON;
import static guices.gzguicemock.FourEyesClientConfig.Piece.MOLTEN_SUPPLIER_NAME;
import static guices.gzguicemock.SupplierSystem.MOLTEN;

public class HealthCheckModule extends PrivateModule {

    @Override
    protected void configure() {
//        bind(FourEyesClient.class).in(SINGLETON);
//        bind(FourEyesClientConfig.class).to(FourEyesClientConfig.Impl.class);
//        bind(FourEyesSupplierNames.class).to(FourEyesSupplierNames.Impl.class);
        // expose(FourEyesClient.class);
    }

//    @Provides
//    @FourEyesSupplierNames
//    @Singleton
//    public Map<SupplierSystem, String> getFourEyesSupplierNames(@FourEyesClientConfig(MOLTEN_SUPPLIER_NAME) String moltenSupplierName)
//    {
//        EnumMap<SupplierSystem, String> map = new EnumMap<>(SupplierSystem.class);
//        map.put(MOLTEN, moltenSupplierName);
//        return map;
//    }
}

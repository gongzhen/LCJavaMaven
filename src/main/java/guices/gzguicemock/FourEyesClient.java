package guices.gzguicemock;

import com.google.inject.Inject;

import java.util.Map;

public class FourEyesClient {

    @Inject
    public FourEyesClient(@FourEyesSupplierNames Map<SupplierSystem, String> supplierNames) {
        supplierNames_ = supplierNames;
    }

    private final Map<SupplierSystem, String> supplierNames_;
}

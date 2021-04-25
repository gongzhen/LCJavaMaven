package guices.gzguice;

public interface GZElement {

    Object getSource();

    void applyTo(GZBinder gzBinder);

}
